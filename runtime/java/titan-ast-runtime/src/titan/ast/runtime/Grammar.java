package titan.ast.runtime;

/**
 * 语法（文法）.
 *
 * @author tian wei jun
 */
public class Grammar {
  String name = "";
  GrammarType type = GrammarType.TERMINAL;
  GrammarAction action = GrammarAction.TEXT;

  public String getName() {
    return name;
  }

  public GrammarType getType() {
    return type;
  }

  public GrammarAction getAction() {
    return action;
  }

  @Override
  public String toString() {
    return name;
  }
}
