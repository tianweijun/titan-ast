package titan.ast.grammar;

import titan.ast.fa.token.TokenNfa;
import titan.ast.fa.token.TokenNfable;

/**
 * TerminalFragmentGrammar.
 *
 * @author tian wei jun
 */
public class TerminalFragmentGrammar extends Grammar implements TokenNfable {

  public TokenNfa tokenNfa = null;

  public TerminalFragmentGrammar(String name) {
    super(GrammarType.TERMINAL_FRAGMENT, name);
  }

  @Override
  public TokenNfa getTokenNfa() {
    return tokenNfa;
  }

  @Override
  public void setTokenNfa(TokenNfa tokenNfa) {
    this.tokenNfa = tokenNfa;
  }
}
