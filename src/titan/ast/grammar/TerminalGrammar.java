package titan.ast.grammar;

import titan.ast.fa.token.TokenNfa;
import titan.ast.fa.token.TokenNfable;

/**
 * TerminalGrammar.
 *
 * @author tian wei jun
 */
public class TerminalGrammar extends Grammar implements TokenNfable {

  public LookaheadMatchingMode lookaheadMatchingMode = LookaheadMatchingMode.GREEDINESS;
  public TokenNfa tokenNfa = null;

  public TerminalGrammar(String name) {
    super(GrammarType.TERMINAL, name);
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
