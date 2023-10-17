package titan.ast.grammar;

/**
 * 语法文件中->后面;前面的内容，表示一些特殊操作,比如，在终结符中skip表示生成token时type时skip.
 *
 * @author tian wei jun
 */
public enum GrammarAction {
  TEXT("text"),
  SKIP("skip");
  private String name;

  GrammarAction(String name) {
    this.name = name;
  }

  /**
   * 根据文本获取对应的GrammarAction类型.
   *
   * @param name GrammarAction的name
   * @return GrammarAction
   */
  public static GrammarAction getActionByString(String name) {
    GrammarAction res = TEXT;
    for (GrammarAction action : GrammarAction.values()) {
      if (action.name.equalsIgnoreCase(name)) {
        res = action;
        break;
      }
    }
    return res;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
