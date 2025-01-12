package titan.ast.grammar.regexp;

/**
 * 正则文本和自身的一些关系运算的类型.
 *
 * @author tian wei jun
 */
public enum RelationshipQualifier {
  NOT("~"),
  OR("|"),
  AND("");
  private final String value;

  RelationshipQualifier(String value) {
    this.value = value;
  }
}
