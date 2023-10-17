package titan.ast.grammar;

/**
 * TerminalGrammar.
 *
 * @author tian wei jun
 */
public class TerminalGrammar extends Grammar {
  public LookaheadMatchingMode lookaheadMatchingMode = LookaheadMatchingMode.GREEDINESS;

  public TerminalGrammar(String name) {
    super(name);
    this.type = GrammarType.TERMINAL;
  }

  public void setLookaheadMatchingMode() {
    if (GrammarAttribute.isAcceptWhenFirstArriveAtTerminalState(attributes)) {
      lookaheadMatchingMode = LookaheadMatchingMode.ACCEPT_WHEN_FIRST_ARRIVE_AT_TERMINAL_STATE;
    } else if (GrammarAttribute.isLaziness(attributes)) {
      lookaheadMatchingMode = LookaheadMatchingMode.LAZINESS;
    } else {
      lookaheadMatchingMode = LookaheadMatchingMode.GREEDINESS;
    }
  }
}
