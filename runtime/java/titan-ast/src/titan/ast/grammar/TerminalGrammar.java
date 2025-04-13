package titan.ast.grammar;

import titan.ast.fa.token.TokenNfa;

/**
 * TerminalGrammar.
 *
 * @author tian wei jun
 */
public class TerminalGrammar extends Grammar {

  public LookaheadMatchingMode lookaheadMatchingMode = LookaheadMatchingMode.GREEDINESS;
  public GrammarAction action = GrammarAction.TEXT;
  public TokenNfa tokenNfa;

  public TerminalGrammar(String name) {
    super(GrammarType.TERMINAL, name);
  }

  @Override
  public int compareTo(Grammar that) {
    if (type != that.type) {
      return type.ordinal() - that.type.ordinal();
    }
    return name.compareTo(that.name);
  }
}
