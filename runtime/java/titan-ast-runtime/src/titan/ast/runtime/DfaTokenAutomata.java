package titan.ast.runtime;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 识别token的dfa.
 *
 * @author tian wei jun
 */
public class DfaTokenAutomata {
  TokenDfa dfa;
  int eof = -1;
  ByteBufferedInputStream byteBufferedInputStream;

  LinkedList<Token> tokens = new LinkedList<>();
  StringBuilder oneTokenStringBuilder;
  int startIndexOfToken;

  /**
   * 初始化oneTokenStringBuilder、eof、dfa.
   *
   * @param dfa 识别token的 确定有限状态自动机
   */
  public DfaTokenAutomata(TokenDfa dfa) {
    this.dfa = dfa;
    this.oneTokenStringBuilder = new StringBuilder();
  }

  public List<Token> buildToken(String sourceFilePath) {
    List<Token> ret = null;
    try (FileInputStream fileInputStream = new FileInputStream(sourceFilePath)) {
      ret = buildToken(fileInputStream);
    } catch (IOException e) {
      throw new AstRuntimeException(e);
    }
    return ret;
  }

  /**
   * 根据输入流构造tokens.
   *
   * @param byteInputStream 将要识别文本的输入流.
   * @return
   */
  public List<Token> buildToken(InputStream byteInputStream) {
    List<Token> ret = null;
    try {
      // init
      byteBufferedInputStream = new ByteBufferedInputStream(byteInputStream);
      tokens.clear();
      // build
      boolean hasBuildedToken = false;
      do {
        hasBuildedToken = buildOneToken();
      } while (hasBuildedToken);
      ret = new ArrayList<>(tokens.size());
      ret.addAll(tokens);
    } catch (Exception e) {
      // clear
      byteBufferedInputStream.close();
      byteBufferedInputStream = null;
      tokens.clear();
      throw e;
    }

    return ret;
  }

  /**
   * 一开始处于初态，每吃一个字符转到下一个状态.
   *
   * @return boolean 是否完成一个token的构造，是的话返回true
   */
  private boolean buildOneToken() {
    TokenDfaState terminalState = getTerminalState();
    if (null == terminalState) { // 输入流读完结束
      return false;
    }
    Token token = new Token(startIndexOfToken);
    token.text = oneTokenStringBuilder.toString();
    token.terminal = terminalState.terminal;
    token.type = TokenType.getByGrammarAction(terminalState.terminal.action);
    tokens.add(token);

    return true;
  }

  private TokenDfaState getTerminalState() {
    oneTokenStringBuilder.delete(0, oneTokenStringBuilder.length());
    startIndexOfToken = byteBufferedInputStream.nextReadIndex;
    int ch = byteBufferedInputStream.read();
    if (ch == eof) {
      return null;
    }
    // first terminal state
    TokenDfaState firstTerminalState = null;
    TokenDfaState currentState = dfa.start;
    while (ch != eof) {
      TokenDfaState nextState = currentState.edges.get(ch);
      oneTokenStringBuilder.append((char) ch);
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
    if (null == firstTerminalState) {
      throw new AstRuntimeException(
          String.format("'%s' does not match any token", oneTokenStringBuilder.toString()));
    }
    // 重复嗅探更高优先级或贪婪
    int lengthOfToken = oneTokenStringBuilder.length();
    // heaviest terminal state
    TokenDfaState heaviestTerminalState = firstTerminalState;
    TerminalGrammar terminal = (TerminalGrammar) heaviestTerminalState.terminal;
    ch = byteBufferedInputStream.read();
    // 如果没有文本嗅探了直接跳出循环
    // 贪婪或者嗅探高优先级
    while (ch != eof) {
      TokenDfaState nextState = currentState.edges.get(ch);
      oneTokenStringBuilder.append((char) ch);
      currentState = nextState;
      if (null == nextState) { // 不通
        break;
      }
      if (FaStateType.isClosingTag(currentState.type)) { // 找到终态
        // 新状态具有更高优先级的，接受状态转移
        if (currentState.weight > heaviestTerminalState.weight) {
          heaviestTerminalState = currentState;
          terminal = (TerminalGrammar) heaviestTerminalState.terminal;
          lengthOfToken = oneTokenStringBuilder.length();
          byteBufferedInputStream.mark();
        }
        // 相同优先级说明状态是同一个token的终态
        // 如果是贪婪的，则增加识别的字符，接受状态转移
        // 不是贪婪的，则不接受状态转移
        if (currentState.weight == heaviestTerminalState.weight
            && terminal == heaviestTerminalState.terminal) {
          if (terminal.lookaheadMatchingMode == LookaheadMatchingMode.GREEDINESS) {
            heaviestTerminalState = currentState;
            terminal = (TerminalGrammar) heaviestTerminalState.terminal;
            lengthOfToken = oneTokenStringBuilder.length();
            byteBufferedInputStream.mark();
          }
        }
        // 新token优先级更低直接被覆盖，不接受替换旧终态
      }
      ch = byteBufferedInputStream.read();
    }
    byteBufferedInputStream.reset();
    oneTokenStringBuilder.delete(lengthOfToken, oneTokenStringBuilder.length());
    return heaviestTerminalState;
  }
}
