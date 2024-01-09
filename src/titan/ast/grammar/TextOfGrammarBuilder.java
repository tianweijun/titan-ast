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
    LinkedList<GrammarToken> statementTokens = getStatementTokens(tokensIt);
    while (!statementTokens.isEmpty()) {
      parseStatement(statementTokens);
      statementTokens = getStatementTokens(tokensIt);
    }
  }

  private void parseStatement(LinkedList<GrammarToken> statementTokens) {
    boolean hasSetState = setState(statementTokens);
    if (!hasSetState) {
      // 创建语法节点
      createGrammarNode(statementTokens);
    }
  }

  private boolean setState(LinkedList<GrammarToken> statementTokens) {
    boolean isStatementTokensWrong = false;
    boolean isPrefixOfIdentificationStatement = false;
    // 语句为空报错
    if (statementTokens.isEmpty()) {
      isStatementTokensWrong = true;
    } else { // 语句是标识语句前缀但是长度不是2则报错
      String firstTokenText = statementTokens.getFirst().text;
      isPrefixOfIdentificationStatement =
          firstTokenText.equals(PREFIX_START_GRAMMAR_STATEMENT)
              || firstTokenText.equals(PREFIX_TERMINAL_GRAMMAR_STATEMENT)
              || firstTokenText.equals(PREFIX_NONTERMINAL_GRAMMAR_STATEMENT)
              || firstTokenText.equals(PREFIX_FRAGMENT_GRAMMAR_STATEMENT)
              || firstTokenText.equals(PREFIX_START_ROOT_KEY_WORD)
              || firstTokenText.equals(PREFIX_KEY_WORD_STATEMENT);
      if (isPrefixOfIdentificationStatement && statementTokens.size() != 2) {
        isStatementTokensWrong = true;
      }
    }
    // 语句不合法则报错
    if (isStatementTokensWrong) {
      StringBuilder errorInfo =
          new StringBuilder("here the error occurs in setState, statementTokens is '");
      for (GrammarToken token : statementTokens) {
        errorInfo.append(token.text);
        errorInfo.append(" ");
      }
      errorInfo.append("'");
      throw new AstRuntimeException(errorInfo.toString());
    }
    if (!isPrefixOfIdentificationStatement) {
      return false;
    }
    // 标识语句
    String firstTokenText = statementTokens.getFirst().text;
    String contentTokenText = statementTokens.getLast().text;
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
      if (firstTokenText.equals(PREFIX_NONTERMINAL_GRAMMAR_STATEMENT)) {
        state = State.NONTERMINAL;
      }
      if (firstTokenText.equals(PREFIX_TERMINAL_GRAMMAR_STATEMENT)) {
        state = State.TERMINAL;
      }
      if (firstTokenText.equals(PREFIX_FRAGMENT_GRAMMAR_STATEMENT)) {
        state = State.TERMINAL_FRAGMENT;
      }
      if (firstTokenText.equals(PREFIX_KEY_WORD_STATEMENT)) {
        state = State.KEY_WORD;
      }
    } else if (contentTokenText.equals(END_GRAMMAR_DEFINITION)) {
      state = State.NORMAL;
    } else {
      throw new AstRuntimeException(
          String.format(
              "expect '%s,%s' after %s,error near %s %s.",
              BEGINNING_GRAMMAR_DEFINITION,
              END_GRAMMAR_DEFINITION,
              firstTokenText,
              firstTokenText,
              contentTokenText));
    }
    return true;
  }

  /**
   * 依据完整文本创建语法节点.
   *
   * @param statementTokens 从grammarName到;前的内容，同时没有skiptoken,如Newline : NewlineFragment -> skip
   */
  private void createGrammarNode(LinkedList<GrammarToken> statementTokens) {
    // NORMAL状态不能建立语法，出错
    boolean isWrong = state == State.NORMAL;
    if (statementTokens.size() < 3) { // 语句太短可能出错
      if (statementTokens.size() == 2) { // 不是空正则，出错
        isWrong = isWrong || !statementTokens.getLast().text.equals(KW_MID_OF_GRAMMAR_DEFINITION);
      } else { // 语句太短出错
        isWrong = true;
      }
    }

    // 出错，报异常
    if (isWrong) {
      StringBuilder errorInfo =
          new StringBuilder(
              "statementTokens  must be legal and in grammar node context ,create node of grammar is error in '");
      for (GrammarToken token : statementTokens) {
        errorInfo.append(token.text);
        errorInfo.append(" ");
      }
      errorInfo.append("'");
      throw new AstRuntimeException(errorInfo.toString());
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
      case NORMAL:
      default:
    }
    Iterator<GrammarToken> statementTokensIt = statementTokens.iterator();
    // grammar name
    GrammarToken token = statementTokensIt.next();
    grammar.name = grammarCharset.formatEscapeChar2Char(token.text);
    // Grammar attributes or :
    token = statementTokensIt.next(); // GrammarDescriptor or :
    // Grammar attributes
    while (!token.text.equals(KW_MID_OF_GRAMMAR_DEFINITION)) {
      if (GrammarAttribute.isGrammarAttributeToken(token)) {
        grammar.attributes.add(token);
      } else {
        StringBuilder errorInfo = new StringBuilder();
        while (statementTokensIt.hasNext()) {
          errorInfo.append(statementTokensIt.next());
          if (statementTokensIt.hasNext()) {
            errorInfo.append(" ");
          }
        }
        throw new AstRuntimeException(
            String.format(
                "'%s' is not a grammar attribute,grammar name:%s,error near '%s'.",
                token.text, grammar.name, errorInfo.toString()));
      }
      token = statementTokensIt.next();
    }
    // :
    if (!token.text.equals(KW_MID_OF_GRAMMAR_DEFINITION)) {
      throw new AstRuntimeException(String.format("expect ':' after %s.", grammar.name));
    }
    // text & action
    setTextAndActionOfGrammarNode(grammar, statementTokensIt);
    // add to languageGrammar
    if (state == State.KEY_WORD) {
      languageGrammar.addKeyWord(grammar);
    } else {
      languageGrammar.addGrammar(grammar);
    }
  }

  private void setTextAndActionOfGrammarNode(
      Grammar grammar, Iterator<GrammarToken> statementTokensIt) {
    while (statementTokensIt.hasNext()) {
      GrammarToken token = statementTokensIt.next();
      if (token.text.equals(KW_PREFIX_ACTION)) {
        // action
        token = statementTokensIt.next();
        grammar.action = GrammarAction.getActionByString(token.text);
        if (statementTokensIt.hasNext()) {
          throw new AstRuntimeException(
              String.format("after grammar action '%s',should be null.", token.text));
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
  private LinkedList<GrammarToken> getStatementTokens(Iterator<GrammarToken> tokensIt) {
    LinkedList<GrammarToken> statementTokens = new LinkedList<>();
    GrammarToken token = nextUsefulToken(tokensIt);
    while (token != null && !token.text.equals(KW_END_OF_GRAMMAR_DEFINITION)) {
      statementTokens.add(token);
      token = nextUsefulToken(tokensIt);
    }
    return statementTokens;
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
