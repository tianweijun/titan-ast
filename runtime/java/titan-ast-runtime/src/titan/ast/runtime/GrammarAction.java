package titan.ast.runtime;

/**
 * 语法文件中->后面;前面的内容，表示一些特殊操作,比如，在终结符中skip表示生成token时type时skip.
 *
 * @author tian wei jun
 */
public enum GrammarAction {
  TEXT("text"),
  SKIP("skip");
  private final String name;

  GrammarAction(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
