package titan.ast.grammar.regexp;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 *
 * @author tian wei jun
 */
public class CompositeRegExp extends RegExp {

  public List<RegExp> children = new ArrayList<>();
  public RelationshipQualifier relationshipOfChildren = RelationshipQualifier.AND;


  public CompositeRegExp() {
    super(RegExpType.COMPOSITE);
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

    CompositeRegExp that = (CompositeRegExp) o;
    return children.equals(that.children) && relationshipOfChildren == that.relationshipOfChildren;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + children.hashCode();
    result = 31 * result + relationshipOfChildren.hashCode();
    return result;
  }

  public enum RelationshipQualifier {
    OR("|"),
    AND("");
    private final String value;

    RelationshipQualifier(String value) {
      this.value = value;
    }
  }
}
