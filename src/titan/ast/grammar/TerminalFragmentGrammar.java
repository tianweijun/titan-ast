package titan.ast.grammar;

/**
 * TerminalFragmentGrammar.
 *
 * @author tian wei jun
 */
public class TerminalFragmentGrammar extends Grammar {

  public TerminalFragmentGrammar(String name) {
    super(name);
    this.type = GrammarType.TERMINAL_FRAGMENT;
  }
}
