package titan.ast.grammar.regexp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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

  public OrCompositeRegExp(AndCompositeRegExp... andCompositeRegExps) {
    this();
    children = new ArrayList<>(andCompositeRegExps.length);
    Collections.addAll(children, andCompositeRegExps);
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

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (AndCompositeRegExp andCompositeRegExp : children) {
      stringBuilder.append(andCompositeRegExp.toString()).append(" | ");
    }
    if (!stringBuilder.isEmpty()) {
      stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length());
    }
    return String.format("OrCompositeRegExp{%s}", stringBuilder.toString());
  }
}
