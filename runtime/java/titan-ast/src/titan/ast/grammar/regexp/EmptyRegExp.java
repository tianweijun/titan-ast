package titan.ast.grammar.regexp;

/**
 * .
 *
 * @author tian wei jun
 */
public class EmptyRegExp extends RegExp{

  private EmptyRegExp() {
    super(RegExpType.EMPTY);
  }

  public static final EmptyRegExp EMPTY_REG_EXP = new EmptyRegExp();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    RegExp regExp = (RegExp) o;
    return type == regExp.type;
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }
}
