package titan.ast.grammar;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import titan.ast.grammar.io.GrammarToken;
import titan.ast.grammar.io.GrammarTokenType;
import titan.ast.grammar.syntax.AstAutomata;
import titan.ast.grammar.syntax.SyntaxDfa;
import titan.ast.grammar.token.KeyWordAutomata;
import titan.ast.grammar.token.TokenAutomata;
import titan.ast.grammar.token.TokenDfa;
import titan.ast.runtime.AstRuntimeException;
import titan.ast.util.StringUtils;

/**
 * 语法文件所对应的实体，以及其所表示的自动机等.
 *
 * @author tian wei jun
 */
public class LanguageGrammar {

  public String start = "compilationUnit";
  public LinkedHashMap<String, Grammar> terminalFragments = new LinkedHashMap<>();
  public LinkedHashMap<String, Grammar> terminals = new LinkedHashMap<>();
  public LinkedHashMap<String, Grammar> nonterminals = new LinkedHashMap<>();

  public Grammar epsilon;
  public Grammar augmentedNonterminal;

  public TokenDfa tokenDfa;
  public SyntaxDfa astDfa;
  public TokenAutomata tokenAutomata;
  public AstAutomata astAutomata;

  // keyword
  public String rootKeyWord = null;
  public LinkedHashSet<Grammar> keyWords = new LinkedHashSet<>();
  public KeyWordAutomata keyWordAutomata = null;

  public LanguageGrammar() {
    init();
  }

  public void init() {
    initEpsilon();
    initAugmentedNonterminal();
  }

  private void initAugmentedNonterminal() {
    augmentedNonterminal = new NonterminaltGrammar("augmentedNonterminal");
    augmentedNonterminal.type = GrammarType.NONTERMINAL;
    augmentedNonterminal.text.add(new GrammarToken(GrammarTokenType.TEXT, start));
    nonterminals.put(augmentedNonterminal.name, augmentedNonterminal);
  }

  public void updateStart(String start) {
    this.start = start;
    augmentedNonterminal.text.clear();
    augmentedNonterminal.text.add(new GrammarToken(GrammarTokenType.TEXT, start));
  }

  private void initEpsilon() {
    epsilon = new TerminalGrammar("Epsilon");
    epsilon.type = GrammarType.TERMINAL;
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

  public Grammar getStart() {
    return nonterminals.get(start);
  }

  public void updateRootKeyWord(String rootKeyWord) {
    this.rootKeyWord = rootKeyWord;
  }

  public void addKeyWord(Grammar keyWord) {
    if (keyWords.contains(keyWord)) {
      throw new AstRuntimeException(
          String.format("name of grammar '%s' is not unique.", keyWord.name));
    }
    keyWords.add(keyWord);
  }

  public boolean isKeyWordEmpty() {
    return !(StringUtils.isNotBlank(rootKeyWord) && !keyWords.isEmpty());
  }
}
