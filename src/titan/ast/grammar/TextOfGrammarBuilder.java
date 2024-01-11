package titan.ast.grammar;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import titan.ast.AstContext;
import titan.ast.grammar.io.GrammarCharset;
import titan.ast.grammar.io.GrammarToken;
import titan.ast.grammar.io.GrammarTokenType;
import titan.ast.runtime.AstRuntimeException;

/**
 * 创建语法节点，设置语法节点内容.
 *
 * @author tian wei jun
 */
public class TextOfGrammarBuilder {

  private static final String PREFIX_START_GRAMMAR_STATEMENT = "@StartGrammar";
  private static final String PREFIX_NONTERMINAL_GRAMMAR_STATEMENT = "@NonterminalGrammar";
  private static final String PREFIX_TERMINAL_GRAMMAR_STATEMENT = "@TerminalGrammar";
  private static final String PREFIX_FRAGMENT_GRAMMAR_STATEMENT = "@TerminalFragmentGrammar";
  private static final String BEGINNING_GRAMMAR_DEFINITION = "begin";
  private static final String END_GRAMMAR_DEFINITION = "end";

  private static final String KW_MID_OF_GRAMMAR_DEFINITION = ":";
  private static final String KW_PREFIX_ACTION = "->";
  private static final String KW_END_OF_GRAMMAR_DEFINITION = ";";

  private static final String PREFIX_START_ROOT_KEY_WORD = "@RootKeyWord";
  private static final String PREFIX_KEY_WORD_STATEMENT = "@KeyWord";

  private final LanguageGrammar languageGrammar;
  private final List<GrammarToken> grammarTokens;
  private final GrammarCharset grammarCharset;

  private State state = State.NORMAL;

  /**
   * 带参初始化，自动初始化grammarCharset.
   *
   * @param grammarTokens tokens
   * @param languageGrammar 语法对象
   */
  public TextOfGrammarBuilder(List<GrammarToken> grammarTokens, LanguageGrammar languageGrammar) {
    this.grammarTokens = grammarTokens;
    this.languageGrammar = languageGrammar;
    grammarCharset = AstContext.get().grammarCharset;
  }

  /** 创建语法节点 设置语法产生式内容. */
  public void build() {
    parseStatements();
  }

  private void parseStatements() {
    Iterator<GrammarToken> tokensIt = grammarTokens.iterator();
    LinkedList<GrammarToken> statementContentTokens = getStatementContentTokens(tokensIt);
    while (!statementContentTokens.isEmpty()) {
      parseStatementContent(statementContentTokens);
      statementContentTokens = getStatementContentTokens(tokensIt);
    }
  }

  private void parseStatementContent(LinkedList<GrammarToken> statementContentTokens) {
    boolean hasSetState = setState(statementContentTokens);
    if (!hasSetState) {
      // 创建语法节点
      createGrammarNode(statementContentTokens);
    }
  }

  private boolean setState(LinkedList<GrammarToken> statementContentTokens) {
    String firstTokenText = statementContentTokens.getFirst().text;
    if (!isPrefixOfStateStatement(firstTokenText)) {
      return false;
    }
    boolean isStatementContentTokensWrong = false;
    if (statementContentTokens.size() == 2) {
      // 标识语句
      String contentTokenText = statementContentTokens.getLast().text;
      // 设置开始语法，该标识语句放在任何状态下都可以，也不影响状态的改变
      if (firstTokenText.equals(PREFIX_START_GRAMMAR_STATEMENT)) {
        languageGrammar.updateStart(contentTokenText);
        return true;
      }
      // 设置RootKeyWord，该标识语句放在任何状态下都可以，也不影响状态的改变
      if (firstTokenText.equals(PREFIX_START_ROOT_KEY_WORD)) {
        languageGrammar.updateRootKeyWord(contentTokenText);
        return true;
      }
      // 语法定义状态管理
      if (contentTokenText.equals(BEGINNING_GRAMMAR_DEFINITION)) {
        switch (firstTokenText) {
          case PREFIX_NONTERMINAL_GRAMMAR_STATEMENT:
            state = State.NONTERMINAL;
            break;
          case PREFIX_TERMINAL_GRAMMAR_STATEMENT:
            state = State.TERMINAL;
            break;
          case PREFIX_FRAGMENT_GRAMMAR_STATEMENT:
            state = State.TERMINAL_FRAGMENT;
            break;
          case PREFIX_KEY_WORD_STATEMENT:
            state = State.KEY_WORD;
            break;
          default:
        }
      } else if (contentTokenText.equals(END_GRAMMAR_DEFINITION)) {
        state = State.NORMAL;
      } else {
        isStatementContentTokensWrong = true;
      }
    } else {
      isStatementContentTokensWrong = true;
    }
    // 语句不合法则报错
    if (isStatementContentTokensWrong) {
      StringBuilder statementContentTokensInfo = new StringBuilder();
      for (GrammarToken token : statementContentTokens) {
        statementContentTokensInfo.append(token.text).append(" ");
      }
      if (statementContentTokensInfo.length() > 0) {
        statementContentTokensInfo.delete(
            statementContentTokensInfo.length() - 1, statementContentTokensInfo.length());
      }
      GrammarToken lastContentToken = statementContentTokens.getLast();
      throw new AstRuntimeException(
          String.format(
              "special grammar statement is not legal,error near [%d,%d):%s",
              statementContentTokens.getFirst().start,
              lastContentToken.start + lastContentToken.text.length(),
              statementContentTokensInfo.toString()));
    }
    return true;
  }

  private boolean isPrefixOfStateStatement(String prefixText) {
    return prefixText.equals(PREFIX_START_GRAMMAR_STATEMENT)
        || prefixText.equals(PREFIX_TERMINAL_GRAMMAR_STATEMENT)
        || prefixText.equals(PREFIX_NONTERMINAL_GRAMMAR_STATEMENT)
        || prefixText.equals(PREFIX_FRAGMENT_GRAMMAR_STATEMENT)
        || prefixText.equals(PREFIX_START_ROOT_KEY_WORD)
        || prefixText.equals(PREFIX_KEY_WORD_STATEMENT);
  }

  /**
   * 依据完整文本创建语法节点.
   *
   * @param statementContentTokens 从grammarName到;前的内容，同时没有skiptoken,如Newline : NewlineFragment ->
   *     skip
   */
  private void createGrammarNode(LinkedList<GrammarToken> statementContentTokens) {
    // NORMAL状态不能建立语法，出错
    boolean isWrong = state == State.NORMAL;
    if (statementContentTokens.size() < 3) { // 语句太短可能出错
      if (statementContentTokens.size() == 2) { // 不是空正则，出错
        isWrong =
            isWrong || !statementContentTokens.getLast().text.equals(KW_MID_OF_GRAMMAR_DEFINITION);
      } else { // 语句太短出错
        isWrong = true;
      }
    }
    int startIndex = statementContentTokens.getFirst().start;
    GrammarToken lastStatementContentToken = statementContentTokens.getLast();
    int endIndex = lastStatementContentToken.start + lastStatementContentToken.text.length();
    if (isWrong) {
      throw new AstRuntimeException(
          String.format(
              "definition of grammar is not legal,error near [%d,%d):%s",
              startIndex, endIndex, getStringOfStatementContentTokens(statementContentTokens)));
    }

    Grammar grammar = null;
    switch (state) {
      case NONTERMINAL:
        grammar = new NonterminaltGrammar(null);
        break;
      case TERMINAL:
      case KEY_WORD:
        grammar = new TerminalGrammar(null);
        break;
      case TERMINAL_FRAGMENT:
        grammar = new TerminalFragmentGrammar(null);
        break;
      default:
    }
    Iterator<GrammarToken> statementContentTokensIt = statementContentTokens.iterator();
    // grammar name
    GrammarToken token = statementContentTokensIt.next();
    grammar.name = grammarCharset.formatEscapeChar2Char(token.text);
    // Grammar attributes or :
    token = statementContentTokensIt.next(); // GrammarDescriptor or :
    // Grammar attributes
    while (!token.text.equals(KW_MID_OF_GRAMMAR_DEFINITION)) {
      if (GrammarAttribute.isGrammarAttributeToken(token)) {
        grammar.attributes.add(token);
      } else {
        throw new AstRuntimeException(
            String.format(
                "definition of grammar is not legal,'%s' is not a grammar attribute,error near [%d,%d)",
                token.text, token.start, token.start + token.text.length()));
      }
      if (!statementContentTokensIt.hasNext()) {
        break;
      }
      token = statementContentTokensIt.next();
    }
    // :
    if (!token.text.equals(KW_MID_OF_GRAMMAR_DEFINITION)) {
      throw new AstRuntimeException(
          String.format(
              "definition of grammar is not legal,expect '%s' but '%s',error near [%d,%d)",
              KW_MID_OF_GRAMMAR_DEFINITION,
              token.text,
              token.start,
              token.start + token.text.length()));
    }
    // text & action
    setTextAndActionOfGrammarNode(grammar, statementContentTokensIt);
    // add to languageGrammar
    if (state == State.KEY_WORD) {
      languageGrammar.addKeyWord(grammar);
    } else {
      languageGrammar.addGrammar(grammar);
    }
  }

  private String getStringOfStatementContentTokens(
      LinkedList<GrammarToken> statementContentTokens) {
    StringBuilder strBuilder = new StringBuilder();
    for (GrammarToken statementContentToken : statementContentTokens) {
      strBuilder.append(statementContentToken.text);
      strBuilder.append(" ");
    }
    if (strBuilder.length() > 0) {
      strBuilder.delete(strBuilder.length() - 1, strBuilder.length());
    }
    return strBuilder.toString();
  }

  private void setTextAndActionOfGrammarNode(
      Grammar grammar, Iterator<GrammarToken> statementContentTokensIt) {
    while (statementContentTokensIt.hasNext()) {
      GrammarToken token = statementContentTokensIt.next();
      if (token.text.equals(KW_PREFIX_ACTION)) {
        // action
        token = statementContentTokensIt.next();
        grammar.action = GrammarAction.getActionByString(token.text);
        if (statementContentTokensIt.hasNext()) {
          throw new AstRuntimeException(
              String.format(
                  "after grammar action is '%s',should be null,error near [%d,%d)",
                  token.text, token.start, token.start + token.text.length()));
        }
        break;
      }
      grammar.text.add(token);
    }
  }

  /**
   * 构造一个语法节点所需的文本.
   *
   * @param tokensIt token流的遍历器
   * @return 从grammarName到;前的内容，同时没有skiptoken,如Newline : NewlineFragment -> skip
   */
  private LinkedList<GrammarToken> getStatementContentTokens(Iterator<GrammarToken> tokensIt) {
    LinkedList<GrammarToken> statementContentTokens = new LinkedList<>();
    if (!tokensIt.hasNext()) {
      return statementContentTokens;
    }
    // 结尾必须是KW_END_OF_GRAMMAR_DEFINITION(;)
    boolean isContentStatementRight = false;
    GrammarToken token = tokensIt.next();
    int startIndex = token.start;
    int endIndex = startIndex + token.text.length();
    while (true) {
      if (!GrammarTokenType.isSkip(token.type)) {
        if (token.text.equals(KW_END_OF_GRAMMAR_DEFINITION)) {
          isContentStatementRight = true;
          break;
        } else {
          statementContentTokens.add(token);
        }
      }
      if (!tokensIt.hasNext()) {
        break;
      }
      token = tokensIt.next();
      endIndex = token.start + token.text.length();
    }
    if (!isContentStatementRight) {
      StringBuilder contentStatementInfo = new StringBuilder();
      for (GrammarToken statementContentToken : statementContentTokens) {
        contentStatementInfo.append(statementContentToken.text).append(" ");
      }
      if (contentStatementInfo.length() > 0) {
        contentStatementInfo.delete(
            contentStatementInfo.length() - 1, contentStatementInfo.length());
      }

      throw new AstRuntimeException(
          String.format(
              "postfix of grammar statement is '%s',error near [%d,%d):%s",
              KW_END_OF_GRAMMAR_DEFINITION, startIndex, endIndex, contentStatementInfo.toString()));
    }
    return statementContentTokens;
  }

  private GrammarToken nextUsefulToken(Iterator<GrammarToken> tokensIt) {
    GrammarToken token = null;
    while (tokensIt.hasNext()) {
      GrammarToken tmp = tokensIt.next();
      if (!GrammarTokenType.isSkip(tmp.type)) {
        token = tmp;
        break;
      }
    }
    return token;
  }

  public enum State {
    NORMAL,
    TERMINAL_FRAGMENT,
    TERMINAL,
    NONTERMINAL,
    KEY_WORD
  }
}
