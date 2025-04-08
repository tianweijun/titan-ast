package titan.ast.grammar.regexp;


/**
 * .
 *
 * @author tian wei jun
 */
public class ParenthesisRegExp extends RegExp {

  public RegExp child = EmptyRegExp.EMPTY_REG_EXP;

  public ParenthesisRegExp() {
    super(RegExpType.PARENTHESIS);
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

    ParenthesisRegExp that = (ParenthesisRegExp) o;
    return child.equals(that.child);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + child.hashCode();
    return result;
  }
}
