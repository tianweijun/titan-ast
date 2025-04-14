package titan.ast.grammar;

import java.util.LinkedHashMap;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.DerivedTerminalGrammarAutomataDetail.RootTerminalGrammarMapDetail;

/**
 * 语法文件所对应的实体，以及其所表示的自动机等.
 *
 * @author tian wei jun
 */
public class LanguageGrammar {

  public String startGrammarName = "compilationUnit";
  public LinkedHashMap<String, TerminalFragmentGrammar> terminalFragments = new LinkedHashMap<>();
  public LinkedHashMap<String, TerminalGrammar> terminals = new LinkedHashMap<>();
  public LinkedHashMap<String, NonterminalGrammar> nonterminals = new LinkedHashMap<>();
  // default grammar
  public TerminalGrammar epsilon = new TerminalGrammar("Epsilon"); // for Nfa
  public TerminalGrammar eof = new TerminalGrammar("Eof");
  // for FollowFilterBacktrackingBottomUpAstAutomata
  public NonterminalGrammar augmentedNonterminal = new NonterminalGrammar("augmentedNonterminal");

  public DerivedTerminalGrammarAutomataDetail derivedTerminalGrammarAutomataDetail =
      new DerivedTerminalGrammarAutomataDetail();

  public LanguageGrammar() {
  }

  public void updateStartGrammarName(String startGrammarName) {
    this.startGrammarName = startGrammarName;
  }

  public void addTerminalFragmentGrammar(TerminalFragmentGrammar grammar) {
    boolean isNotUnique = terminalFragments.containsKey(grammar.name);
    if (isNotUnique) {
      throw new AstRuntimeException(
          String.format("name of grammar '%s' is not unique.", grammar.name));
    }
    terminalFragments.put(grammar.name, grammar);
  }

  public void addTerminalGrammar(TerminalGrammar grammar) {
    boolean isUnique = isUniqueTerminalGrammar(grammar);
    if (!isUnique) {
      throw new AstRuntimeException(
          String.format("name of grammar '%s' is not unique.", grammar.name));
    }
    terminals.put(grammar.name, grammar);
  }

  public boolean isUniqueTerminalGrammar(TerminalGrammar grammar) {
    boolean isNotUnique = terminalFragments.containsKey(grammar.name)
        || terminals.containsKey(grammar.name)
        || epsilon.name.equals(grammar.name)
        || eof.name.equals(grammar.name);
    return !isNotUnique;
  }

  public void addNonterminalGrammar(NonterminalGrammar grammar) {
    boolean isNotUnique = nonterminals.containsKey(grammar.name)
        || augmentedNonterminal.name.equals(grammar.name);
    if (isNotUnique) {
      throw new AstRuntimeException(
          String.format("name of grammar '%s' is not unique.", grammar.name));
    }
    nonterminals.put(grammar.name, grammar);
  }

  public Grammar getStartGrammar() {
    return nonterminals.get(startGrammarName);
  }

  public RootTerminalGrammarMapDetail getRootTerminalGrammarMap(String rootTerminalGrammar) {
    return derivedTerminalGrammarAutomataDetail.getRootTerminalGrammarMap(rootTerminalGrammar);
  }
}
