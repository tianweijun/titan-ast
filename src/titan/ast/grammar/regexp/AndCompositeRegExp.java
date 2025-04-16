package titan.ast.grammar.regexp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * .
 *
 * @author tian wei jun
 */
public class AndCompositeRegExp extends RegExp {

  public ArrayList<UnitRegExp> children = new ArrayList<>();

  public String alias = "";

  public AndCompositeRegExp() {
    super(RegExpType.AND_COMPOSITE);
  }

  public AndCompositeRegExp(UnitRegExp... unitRegExps) {
    this();
    children = new ArrayList<>(unitRegExps.length);
    Collections.addAll(children, unitRegExps);
  }

  public void setAlias(String alias) {
    this.alias = alias;
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

    AndCompositeRegExp that = (AndCompositeRegExp) o;
    return children.equals(that.children) && alias.equals(that.alias);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + children.hashCode();
    result = 31 * result + alias.hashCode();
    return result;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (UnitRegExp unitRegExp : children) {
      stringBuilder.append(unitRegExp.toString()).append("  ");
    }
    if (!stringBuilder.isEmpty()) {
      stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
    }
    return stringBuilder.toString();
  }
}
