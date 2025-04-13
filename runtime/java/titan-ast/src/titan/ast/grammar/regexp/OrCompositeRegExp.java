package titan.ast.grammar.regexp;

import java.util.ArrayList;

/**
 * .
 *
 * @author tian wei jun
 */
public class OrCompositeRegExp extends RegExp {

  public ArrayList<AndCompositeRegExp> children = new ArrayList<>();

  public OrCompositeRegExp() {
    super(RegExpType.OR_COMPOSITE);
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

    OrCompositeRegExp that = (OrCompositeRegExp) o;
    return children.equals(that.children);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + children.hashCode();
    return result;
  }
}
