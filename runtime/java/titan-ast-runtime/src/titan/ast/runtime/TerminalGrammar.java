package titan.ast.runtime;
/**
 * TerminalGrammar.
 *
 * @author tian wei jun
 */
public class TerminalGrammar extends Grammar {

  public LookaheadMatchingMode lookaheadMatchingMode = LookaheadMatchingMode.GREEDINESS;

  public TerminalGrammar(int index) {
    super(index);
    this.type = GrammarType.TERMINAL;
  }
}
