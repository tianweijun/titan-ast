package titan.ast.grammar;

/**
 * 语法文件中->后面;前面的内容，表示一些特殊操作,比如，在终结符中skip表示生成token时type时skip.
 *
 * @author tian wei jun
 */
public enum TerminalGrammarAction {
  TEXT("text"),
  SKIP("skip");
  private final String name;

  TerminalGrammarAction(String name) {
    this.name = name;
  }

  /**
   * 根据文本获取对应的GrammarAction类型.
   *
   * @param name GrammarAction的name
   * @return GrammarAction
   */
  public static TerminalGrammarAction getActionByString(String name) {
    TerminalGrammarAction res = TEXT;
    for (TerminalGrammarAction action : TerminalGrammarAction.values()) {
      if (action.name.equalsIgnoreCase(name)) {
        res = action;
        break;
      }
    }
    return res;
  }

  public static titan.ast.runtime.GrammarAction toRuntimeAction(TerminalGrammarAction action) {
    titan.ast.runtime.GrammarAction runtimeAction = titan.ast.runtime.GrammarAction.SKIP;
    switch (action) {
      case TEXT:
        runtimeAction = titan.ast.runtime.GrammarAction.TEXT;
        break;
      case SKIP:
        runtimeAction = titan.ast.runtime.GrammarAction.SKIP;
        break;
    }
    return runtimeAction;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
