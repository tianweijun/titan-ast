package titan.ast.grammar.ambiguity;

/**
 * .
 *
 * @author tian wei jun
 */
public class GrammarAmbiguousJudgeResult {
  // public boolean isAmbiguous = false;

  @Override
  public String toString() {
    return "ambiguity of grammars is unknown. be careful of precedence, associativity and uniqueness properties.";
  }
}
