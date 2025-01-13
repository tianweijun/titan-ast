package titan.ast.runtime;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import titan.ast.runtime.AstGeneratorResult.TokenParseErrorData;
import titan.ast.runtime.AstGeneratorResult.TokensResult;

/**
 * 识别token的dfa.
 *
 * @author tian wei jun
 */
public class DfaTokenAutomata implements TokenAutomata {

  final TokenDfa dfa;
  static final int EOF = -1;
  ByteBuffer tokenStringBuilder = new ByteBuffer(256);

  /**
   * 初始化oneTokenStringBuilder、eof、dfa.
   *
   * @param dfa 识别token的 确定有限状态自动机
   */
  public DfaTokenAutomata(TokenDfa dfa) {
    this.dfa = dfa;
  }

  @Override
  public TokensResult buildToken(String sourceFilePath) {
    TokensResult tokensResult = null;
    try (FileInputStream fileInputStream = new FileInputStream(sourceFilePath)) {
      tokensResult = buildToken(fileInputStream);
    } catch (IOException e) {
      tokensResult = TokensResult.generateSourceIoErrorResult(e.getMessage());
    }
    return tokensResult;
  }

  /**
   * 根据输入流构造tokens.
   *
   * @param byteInputStream 将要识别文本的输入流.
   * @return
   */
  @Override
  public TokensResult buildToken(InputStream byteInputStream) {
    // init
    ByteBufferedInputStream byteBufferedInputStream = new ByteBufferedInputStream(byteInputStream);
    TokensResult tokensResult = null;
    List<Token> tokens = new LinkedList<>();
    while (true) { // build one token
      BuildOneTokenMethodResult buildOneTokenMethodResult = null;
      try {
        buildOneTokenMethodResult = buildOneToken(byteBufferedInputStream);
      } catch (IOException e) {
        tokensResult = TokensResult.generateSourceIoErrorResult(e.getMessage());
        break;
      }
      BuildOneTokenMethodResultType buildOneTokenMethodResultType =
          buildOneTokenMethodResult.getType();
      if (buildOneTokenMethodResultType
          == BuildOneTokenMethodResultType.TOKEN) { // has built one token,add it
        tokens.add(buildOneTokenMethodResult.getTokenData());
        continue;
      }

      if (buildOneTokenMethodResultType == BuildOneTokenMethodResultType.ALL_TEXT_HAS_BEEN_BUILT) {
        ArrayList<Token> tokensArrayList = new ArrayList<Token>(tokens.size());
        tokensArrayList.addAll(tokens);
        tokensResult = TokensResult.generateOkResult(tokensArrayList);
        break;
      }
      if (buildOneTokenMethodResultType == BuildOneTokenMethodResultType.TOKEN_PARSE_ERROR) {
        BuildOneTokenMethodTokenGeneratorErrorData tokenGeneratorErrorData =
            buildOneTokenMethodResult.getBuildOneTokenMethodTokenGeneratorErrorData();
        ArrayList<Token> tokensArrayList = new ArrayList<Token>(tokens.size());
        tokensArrayList.addAll(tokens);
        tokensResult =
            TokensResult.generateTokenParseErrorResult(
                new TokenParseErrorData(
                    tokensArrayList,
                    tokenGeneratorErrorData.start,
                    tokenGeneratorErrorData.end,
                    tokenGeneratorErrorData.errorText));
        break;
      }
      break;
    }
    return tokensResult;
  }

  /**
   * .
   *
   * @return 返回null，表示输入流读取完了
   */
  private BuildOneTokenMethodResult buildOneToken(ByteBufferedInputStream byteBufferedInputStream)
      throws IOException {
    int startIndexOfToken = byteBufferedInputStream.nextReadIndex;
    int ch = byteBufferedInputStream.read();
    if (ch == EOF) { // 输入流读取完了
      return BuildOneTokenMethodResult.generateAllTextHasBeenBuiltResult();
    }
    tokenStringBuilder.clear();
    // first terminal state
    TokenDfaState firstTerminalState = null;
    TokenDfaState currentState = dfa.start;
    while (ch != EOF) {
      TokenDfaState nextState = currentState.edges.get(ch);
      tokenStringBuilder.append(ch);
      currentState = nextState;
      if (null == nextState) { // 不通
        break;
      }
      if (FaStateType.isClosingTag(currentState.type)) { // 找到终态
        firstTerminalState = currentState;
        byteBufferedInputStream.mark();
        break;
      }
      ch = byteBufferedInputStream.read();
    }
    if (null == firstTerminalState) { // does not match any token
      return BuildOneTokenMethodResult.generateTokenParseErrorResult(
          new BuildOneTokenMethodTokenGeneratorErrorData(
              startIndexOfToken,
              startIndexOfToken + tokenStringBuilder.length(),
              tokenStringBuilder.toString()));
    }
    // 重复嗅探更高优先级或贪婪
    int lengthOfToken = tokenStringBuilder.length();
    // heaviest terminal state
    TokenDfaState heaviestTerminalState = firstTerminalState;

    // 如果没有文本嗅探了直接跳出循环
    // 贪婪或者嗅探高优先级
    ch = byteBufferedInputStream.read();
    while (ch != EOF) {
      TokenDfaState nextState = currentState.edges.get(ch);
      tokenStringBuilder.append(ch);
      currentState = nextState;
      if (null == nextState) { // 不通
        break;
      }
      if (FaStateType.isClosingTag(currentState.type)) { // 找到终态
        // 新状态具有更高优先级的，接受终态转移
        boolean isHigherPriority = currentState.weight > heaviestTerminalState.weight;
        // 相同优先级说明状态是同一个token的终态
        // 如果是贪婪的，则增加识别的字符，接受终态转移
        // 不是贪婪的，则不接受终态转移
        boolean isSameAndGreediness =
            heaviestTerminalState.terminal == currentState.terminal
                && ((TerminalGrammar) heaviestTerminalState.terminal).lookaheadMatchingMode
                    == LookaheadMatchingMode.GREEDINESS;
        if (isHigherPriority || isSameAndGreediness) {
          heaviestTerminalState = currentState;
          lengthOfToken = tokenStringBuilder.length();
          byteBufferedInputStream.mark();
        }
        // 新token优先级更低直接被覆盖，不接受替换旧终态
      }
      ch = byteBufferedInputStream.read();
    }
    byteBufferedInputStream.reset();
    tokenStringBuilder.setPosition(lengthOfToken);

    // build token result
    Token token = new Token(startIndexOfToken);
    token.text = tokenStringBuilder.toString();
    token.terminal = heaviestTerminalState.terminal;
    token.type = TokenType.getByGrammarAction(heaviestTerminalState.terminal.action);
    return BuildOneTokenMethodResult.generateTokenResult(token);
  }

  private enum BuildOneTokenMethodResultType {
    TOKEN,
    ALL_TEXT_HAS_BEEN_BUILT,
    TOKEN_PARSE_ERROR
  }

  private static class BuildOneTokenMethodResult {
    private final BuildOneTokenMethodResultType type;
    private final Object data;

    public BuildOneTokenMethodResult(BuildOneTokenMethodResultType type, Object data) {
      this.type = type;
      this.data = data;
    }

    public BuildOneTokenMethodResultType getType() {
      return type;
    }

    public static BuildOneTokenMethodResult generateTokenResult(Token data) {
      return new BuildOneTokenMethodResult(BuildOneTokenMethodResultType.TOKEN, data);
    }

    public Token getTokenData() {
      return (Token) data;
    }

    public static BuildOneTokenMethodResult generateAllTextHasBeenBuiltResult() {
      return new BuildOneTokenMethodResult(
          BuildOneTokenMethodResultType.ALL_TEXT_HAS_BEEN_BUILT, null);
    }

    public static BuildOneTokenMethodResult generateTokenParseErrorResult(
        BuildOneTokenMethodTokenGeneratorErrorData data) {
      return new BuildOneTokenMethodResult(BuildOneTokenMethodResultType.TOKEN_PARSE_ERROR, data);
    }

    public BuildOneTokenMethodTokenGeneratorErrorData
        getBuildOneTokenMethodTokenGeneratorErrorData() {
      return (BuildOneTokenMethodTokenGeneratorErrorData) data;
    }
  }

  public static class BuildOneTokenMethodTokenGeneratorErrorData {
    public final int start;
    public final int end;
    public final String errorText;

    public BuildOneTokenMethodTokenGeneratorErrorData(int start, int end, String errorText) {
      this.start = start;
      this.end = end;
      this.errorText = errorText;
    }
  }
}
