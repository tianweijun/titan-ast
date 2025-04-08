package titan.ast.grammar;

import titan.ast.fa.token.TokenNfa;

/**
 * TerminalFragmentGrammar.
 *
 * @author tian wei jun
 */
public class TerminalFragmentGrammar extends Grammar {

  public TokenNfa tokenNfa;

  public TerminalFragmentGrammar(String name) {
    super(GrammarType.TERMINAL_FRAGMENT, name);
  }
}
