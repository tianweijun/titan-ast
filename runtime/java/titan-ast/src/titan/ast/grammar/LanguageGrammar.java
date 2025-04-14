package titan.ast.grammar;

import java.util.LinkedHashMap;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.DerivedTerminalGrammarAutomataDetail.RootTerminalGrammarMapDetail;
import titan.ast.grammar.io.GrammarToken;
import titan.ast.grammar.io.GrammarTokenType;
import titan.ast.grammar.syntax.AstAutomata;
import titan.ast.grammar.syntax.SyntaxDfa;
import titan.ast.grammar.token.TokenAutomata;
import titan.ast.grammar.token.TokenDfa;

/**
 * 语法文件所对应的实体，以及其所表示的自动机等.
 *
 * @author tian wei jun
 */
public class LanguageGrammar {

  public String startGrammarName = "compilationUnit";
  public LinkedHashMap<String, Grammar> terminalFragments = new LinkedHashMap<>();
  public LinkedHashMap<String, Grammar> terminals = new LinkedHashMap<>();
  public LinkedHashMap<String, Grammar> nonterminals = new LinkedHashMap<>();

  public Grammar epsilon; // for Nfa
  public Grammar eof; // for FollowFilterBacktrackingBottomUpAstAutomata
  public Grammar augmentedNonterminal;

  public TokenDfa tokenDfa;
  public SyntaxDfa astDfa;
  public TokenAutomata tokenAutomata;
  public AstAutomata astAutomata;

  // keyword
  public DerivedTerminalGrammarAutomataDetail derivedTerminalGrammarAutomataDetail =
      new DerivedTerminalGrammarAutomataDetail();

  public LanguageGrammar() {
    init();
  }

  public void init() {
    initEpsilon();
    initEof();
    initAugmentedNonterminal();
  }

  private void initAugmentedNonterminal() {
    augmentedNonterminal = new NonterminalGrammar("augmentedNonterminal");
    augmentedNonterminal.type = GrammarType.NONTERMINAL;
    augmentedNonterminal.text.add(new GrammarToken(GrammarTokenType.TEXT, startGrammarName));
    nonterminals.put(augmentedNonterminal.name, augmentedNonterminal);
  }

  public void updateStartGrammarName(String startGrammarName) {
    this.startGrammarName = startGrammarName;
    augmentedNonterminal.text.clear();
    augmentedNonterminal.text.add(new GrammarToken(GrammarTokenType.TEXT, startGrammarName));
  }

  private void initEpsilon() {
    epsilon = new TerminalGrammar("Epsilon");
    epsilon.type = GrammarType.TERMINAL;
  }

  private void initEof() {
    eof = new TerminalGrammar("Eof");
    eof.type = GrammarType.TERMINAL;
  }

  public void addGrammar(Grammar grammar) {
    if (!isUnique(grammar)) {
      throw new AstRuntimeException(
          String.format("name of grammar '%s' is not unique.", grammar.name));
    }
    switch (grammar.type) {
      case TERMINAL:
        terminals.put(grammar.name, grammar);
        break;
      case NONTERMINAL:
        nonterminals.put(grammar.name, grammar);
        break;
      case TERMINAL_FRAGMENT:
        terminalFragments.put(grammar.name, grammar);
        break;
      default:
    }
  }

  /**
   * fragment和fragment的名字不重复，终结符和终结符、fragment的名字不重复， 非和终结符和非和终结符、终结符的名字不重复.
   *
   * @param grammar 语法
   * @return 参数表示的语法 是不是没有存在过，是的话返回true,否则返回false
   */
  private boolean isUnique(Grammar grammar) {
    // default grammar
    if (grammar.type == GrammarType.TERMINAL && epsilon.name.equals(grammar.name)) {
      return false;
    }
    if (grammar.type == GrammarType.TERMINAL && eof.name.equals(grammar.name)) {
      return false;
    }
    // diy  grammar
    String grammarName = grammar.name;
    boolean isNotUnique = false;
    switch (grammar.type) {
      case TERMINAL_FRAGMENT: // fragment和fragment的名字不重复
        isNotUnique = terminalFragments.containsKey(grammarName);
        break;
      case TERMINAL: // 终结符和终结符、fragment的名字不重复
        isNotUnique = terminalFragments.containsKey(grammarName);
        isNotUnique = isNotUnique || terminals.containsKey(grammarName);
        break;
      case NONTERMINAL: // 非和终结符和非和终结符、终结符的名字不重复
        isNotUnique = terminals.containsKey(grammarName);
        isNotUnique = isNotUnique || nonterminals.containsKey(grammarName);
        break;
      default:
    }
    return !isNotUnique;
  }

  public Grammar getStartGrammar() {
    return nonterminals.get(startGrammarName);
  }

  public void clearTokens() {
    // clear token
    for (Grammar grammar : terminalFragments.values()) {
      grammar.attributes = null;
      grammar.text = null;
    }
    for (Grammar grammar : terminals.values()) {
      grammar.attributes = null;
      grammar.text = null;
    }
    for (Grammar grammar : nonterminals.values()) {
      grammar.attributes = null;
      grammar.text = null;
    }
  }

  public RootTerminalGrammarMapDetail getRootTerminalGrammarMap(String rootTerminalGrammar) {
    return derivedTerminalGrammarAutomataDetail.getRootTerminalGrammarMap(rootTerminalGrammar);
  }
}
