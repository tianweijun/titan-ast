package titan.ast.runtime;
/**
 * TerminalGrammar.
 *
 * @author tian wei jun
 */
public class TerminalGrammar extends Grammar {
  public LookaheadMatchingMode lookaheadMatchingMode = LookaheadMatchingMode.GREEDINESS;

  public TerminalGrammar() {
    this.type = GrammarType.TERMINAL;
  }
}
