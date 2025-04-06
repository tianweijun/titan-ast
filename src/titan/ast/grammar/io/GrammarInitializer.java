package titan.ast.grammar.io;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.DerivedTerminalGrammarAutomataDetail.RootTerminalGrammarMapDetail;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.GrammarAction;
import titan.ast.grammar.GrammarAttribute;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.NonterminalGrammar;
import titan.ast.grammar.TerminalFragmentGrammar;
import titan.ast.grammar.TerminalGrammar;

/**
 * 创建语法节点，设置语法节点内容.
 *
 * @author tian wei jun
 */
public class GrammarInitializer {

  private static final String PREFIX_START_GRAMMAR_STATEMENT = "@StartGrammar";
  private static final String PREFIX_NONTERMINAL_GRAMMAR_STATEMENT = "@NonterminalGrammar";
  private static final String PREFIX_TERMINAL_GRAMMAR_STATEMENT = "@TerminalGrammar";
  private static final String PREFIX_FRAGMENT_GRAMMAR_STATEMENT = "@TerminalFragmentGrammar";
  private static final String KW_BEGIN = "begin";
  private static final String KW_END = "end";

  private static final String KW_COLON = ":";
  private static final String KW_RIGHT_ARROW = "->";
  private static final String KW_SEMI = ";";

  private static final String KW_DERIVE = "derive";
  private static final String PREFIX_DERIVED_TERMINAL_GRAMMAR_STATEMENT = "@DerivedTerminalGrammar";

  private final LanguageGrammar languageGrammar;
  private final List<GrammarToken> grammarTokens;

  private State state = State.NORMAL;
  private RootTerminalGrammarMapDetail rootTerminalGrammarMapDetail;

  /**
   * 带参初始化，自动初始化grammarCharset.
   *
   * @param grammarTokens tokens
   * @param languageGrammar 语法对象
   */
  public GrammarInitializer(List<GrammarToken> grammarTokens, LanguageGrammar languageGrammar) {
    this.grammarTokens = grammarTokens;
    this.languageGrammar = languageGrammar;
  }

  /** 创建语法节点 设置语法产生式内容. */
  public void initByGrammarTokens() {
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
      Grammar grammar = createGrammarNode(statementContentTokens);
      if (grammar.isTerminal()) {
        ((TerminalGrammar) grammar).setLookaheadMatchingMode();
      }
      // add to languageGrammar
      if (state == State.DERIVED_TERMINAL) {
        rootTerminalGrammarMapDetail.addTerminalGrammar(grammar);
      } else {
        languageGrammar.addGrammar(grammar);
      }
    }
  }

  private boolean setState(LinkedList<GrammarToken> statementContentTokens) {
    if (!isPrefixOfStateStatement(statementContentTokens.getFirst().text)) {
      return false;
    }
    int sizeOfStatementContentTokens = statementContentTokens.size();
    if (sizeOfStatementContentTokens == 2) {
      doSetStateFor2ContentTokens(statementContentTokens);
    } else if (sizeOfStatementContentTokens == 3) {
      doSetStateFor3ContentTokens(statementContentTokens);
    } else {
      GrammarToken lastContentToken = statementContentTokens.getLast();
      throw new AstRuntimeException(
          String.format(
              "special grammar statement is not legal,error near [%d,%d):%s",
              statementContentTokens.getFirst().start,
              lastContentToken.start + lastContentToken.text.length(),
              getStatementContentTokensInfo(statementContentTokens)));
    }
    return true;
  }

  private void doSetStateFor3ContentTokens(LinkedList<GrammarToken> statementContentTokens) {
    // @DerivedTerminalGrammar derive(Identifier) begin ;
    Iterator<GrammarToken> statementContentTokensIt = statementContentTokens.iterator();
    String firstTokenText = statementContentTokensIt.next().text;
    statementContentTokensIt.next();
    String lastTokenText = statementContentTokensIt.next().text;

    if (firstTokenText.equals(PREFIX_DERIVED_TERMINAL_GRAMMAR_STATEMENT)
        && lastTokenText.equals(KW_BEGIN)) {
      setDerivedTerminalGrammarBeginningState(statementContentTokens);
      return;
    }
    // 其他情况不合法
    String properGrammar =
        String.format(
            "%s %s(Identifier) %s %s",
            PREFIX_DERIVED_TERMINAL_GRAMMAR_STATEMENT, KW_DERIVE, KW_BEGIN, KW_SEMI);
    throwSetStateException(
        "special grammar statement is not legal, proper grammar like this : " + properGrammar,
        statementContentTokens);
  }

  private void setDerivedTerminalGrammarBeginningState(
      LinkedList<GrammarToken> statementContentTokens) {
    String midTokenText = statementContentTokens.get(1).text;
    if (midTokenText.startsWith(KW_DERIVE) && midTokenText.length() > KW_DERIVE.length() + 2) {
      // 去掉()
      String rootTerminalGrammar =
          midTokenText.substring(KW_DERIVE.length() + 1, midTokenText.length() - 1);
      rootTerminalGrammar = GrammarCharset.formatEscapeChar2Char(rootTerminalGrammar, KW_DERIVE);
      RootTerminalGrammarMapDetail rootTerminalGrammarMapDetail =
          languageGrammar.getRootTerminalGrammarMap(rootTerminalGrammar);
      if (null == rootTerminalGrammarMapDetail) {
        throw new AstRuntimeException("the maximum of RootKeyWord grammar is one");
      }
      this.rootTerminalGrammarMapDetail = rootTerminalGrammarMapDetail;
      state = State.DERIVED_TERMINAL;
      return;
    }
    String properGrammar =
        String.format(
            "%s %s(Identifier) %s %s",
            PREFIX_DERIVED_TERMINAL_GRAMMAR_STATEMENT, KW_DERIVE, KW_BEGIN, KW_SEMI);
    throwSetStateException(
        "special grammar statement is not legal, proper grammar like this : " + properGrammar,
        statementContentTokens);
  }

  private void doSetStateFor2ContentTokens(LinkedList<GrammarToken> statementContentTokens) {
    String firstTokenText = statementContentTokens.getFirst().text;
    String lastTokenText = statementContentTokens.getLast().text;
    // 标识语句
    // @startGrammar 设置开始语法，该标识语句只能放在Normal状态下都可以，也不影响状态的改变
    if (firstTokenText.equals(PREFIX_START_GRAMMAR_STATEMENT)) {
      if (state != State.NORMAL) {
        throw new AstRuntimeException(
            String.format(
                "'%s %s %s' should be at root context.",
                PREFIX_START_GRAMMAR_STATEMENT, lastTokenText, KW_SEMI));
      }
      languageGrammar.updateStartGrammarName(lastTokenText);
      return;
    }
    // begining状态管理
    if (lastTokenText.equals(KW_BEGIN)) {
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
        case PREFIX_DERIVED_TERMINAL_GRAMMAR_STATEMENT:
          throwSetStateException(
              "setting keyword begining statement is at least 3 tokens ", statementContentTokens);
          break;
        default:
      }
      return;
    }
    // end状态管理
    if (lastTokenText.equals(KW_END)) {
      state = State.NORMAL;
      return;
    }
    // 其他情况不合法
    String properGrammar =
        String.format(
            "%s %s %s,%s [%s,%s] %s,%s [%s,%s] %s,%s [%s,%s] %s",
            // @startGrammar xx ;
            PREFIX_START_GRAMMAR_STATEMENT,
            "grammarName",
            KW_SEMI,
            // @NonterminalFragmentGrammar begin|end ;
            PREFIX_NONTERMINAL_GRAMMAR_STATEMENT,
            KW_BEGIN,
            KW_END,
            KW_SEMI,
            // @TerminaGrammar begin|end ;
            PREFIX_TERMINAL_GRAMMAR_STATEMENT,
            KW_BEGIN,
            KW_END,
            KW_SEMI,
            // @TerminalFragmentGrammar begin|end ;
            PREFIX_FRAGMENT_GRAMMAR_STATEMENT,
            KW_BEGIN,
            KW_END,
            KW_SEMI);
    throwSetStateException(
        "special grammar statement is not legal, proper grammar like this : " + properGrammar,
        statementContentTokens);
  }

  private void throwSetStateException(
      String info, LinkedList<GrammarToken> statementContentTokens) {
    GrammarToken lastContentToken = statementContentTokens.getLast();
    throw new AstRuntimeException(
        String.format(
            info + ", error near [%d,%d):%s",
            statementContentTokens.getFirst().start,
            lastContentToken.start + lastContentToken.text.length(),
            getStatementContentTokensInfo(statementContentTokens)));
  }

  private String getStatementContentTokensInfo(LinkedList<GrammarToken> statementContentTokens) {
    StringBuilder statementContentTokensInfo = new StringBuilder();
    for (GrammarToken token : statementContentTokens) {
      statementContentTokensInfo.append(token.text).append(" ");
    }
    if (!statementContentTokensInfo.isEmpty()) {
      statementContentTokensInfo.delete(
          statementContentTokensInfo.length() - 1, statementContentTokensInfo.length());
    }
    return statementContentTokensInfo.toString();
  }

  private boolean isPrefixOfStateStatement(String prefixText) {
    return prefixText.equals(PREFIX_START_GRAMMAR_STATEMENT)
        || prefixText.equals(PREFIX_TERMINAL_GRAMMAR_STATEMENT)
        || prefixText.equals(PREFIX_NONTERMINAL_GRAMMAR_STATEMENT)
        || prefixText.equals(PREFIX_FRAGMENT_GRAMMAR_STATEMENT)
        || prefixText.equals(PREFIX_DERIVED_TERMINAL_GRAMMAR_STATEMENT);
  }

  /**
   * 依据完整文本创建语法节点.
   *
   * @param statementContentTokens 从grammarName到;前的内容，同时没有skiptoken,如Newline : NewlineFragment ->
   *     skip
   */
  private Grammar createGrammarNode(LinkedList<GrammarToken> statementContentTokens) {
    // NORMAL状态不能建立语法，出错
    boolean isWrong = state == State.NORMAL;
    if (statementContentTokens.size() < 3) { // 语句太短可能出错
      if (statementContentTokens.size() == 2) { // 不是空正则，出错
        isWrong = isWrong || !statementContentTokens.getLast().text.equals(KW_COLON);
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
        grammar = new NonterminalGrammar(null);
        break;
      case TERMINAL:
      case DERIVED_TERMINAL:
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
    grammar.name = GrammarCharset.formatEscapeChar2Char(token.text, "init grammar name");
    // Grammar attributes or :
    token = statementContentTokensIt.next(); // GrammarDescriptor or :
    // Grammar attributes
    while (!token.text.equals(KW_COLON)) {
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
    if (!token.text.equals(KW_COLON)) {
      throw new AstRuntimeException(
          String.format(
              "definition of grammar is not legal,expect '%s' but '%s',error near [%d,%d)",
              KW_COLON, token.text, token.start, token.start + token.text.length()));
    }
    // text & action
    setTextAndActionOfGrammarNode(grammar, statementContentTokensIt);
    return grammar;
  }

  private String getStringOfStatementContentTokens(
      LinkedList<GrammarToken> statementContentTokens) {
    StringBuilder strBuilder = new StringBuilder();
    for (GrammarToken statementContentToken : statementContentTokens) {
      strBuilder.append(statementContentToken.text);
      strBuilder.append(" ");
    }
    if (!strBuilder.isEmpty()) {
      strBuilder.delete(strBuilder.length() - 1, strBuilder.length());
    }
    return strBuilder.toString();
  }

  private void setTextAndActionOfGrammarNode(
      Grammar grammar, Iterator<GrammarToken> statementContentTokensIt) {
    while (statementContentTokensIt.hasNext()) {
      GrammarToken token = statementContentTokensIt.next();
      if (token.text.equals(KW_RIGHT_ARROW)) {
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
        if (token.text.equals(KW_SEMI)) {
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
      if (!contentStatementInfo.isEmpty()) {
        contentStatementInfo.delete(
            contentStatementInfo.length() - 1, contentStatementInfo.length());
      }

      throw new AstRuntimeException(
          String.format(
              "postfix of grammar statement is '%s',error near [%d,%d):%s",
              KW_SEMI, startIndex, endIndex, contentStatementInfo));
    }
    return statementContentTokens;
  }

  public enum State {
    NORMAL,
    TERMINAL_FRAGMENT,
    TERMINAL,
    NONTERMINAL,
    DERIVED_TERMINAL
  }
}
