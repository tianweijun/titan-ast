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
    if (GrammarAttribute.isLaziness(attributes)) {
      lookaheadMatchingMode = LookaheadMatchingMode.LAZINESS;
    } else {
      lookaheadMatchingMode = LookaheadMatchingMode.GREEDINESS;
    }
  }

  @Override
  public int compareTo(Grammar that) {
    if (type != that.type) {
      return type.ordinal() - that.type.ordinal();
    }
    return name.compareTo(that.name);
  }
}
