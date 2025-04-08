package titan.ast.grammar.regexp;

/**
 * .
 *
 * @author tian wei jun
 */
public class GrammarRegExp extends RegExp {

  String grammarName = "";

  public GrammarRegExp() {
    super(RegExpType.GRAMMAR);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    GrammarRegExp that = (GrammarRegExp) o;
    return grammarName.equals(that.grammarName);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + grammarName.hashCode();
    return result;
  }
}
