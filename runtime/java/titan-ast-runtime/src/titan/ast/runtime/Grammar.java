package titan.ast.runtime;

/**
 * 语法（文法）.
 *
 * @author tian wei jun
 */
public class Grammar implements Comparable<Grammar> {

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

  @Override
  public int compareTo(Grammar that) {
    if (type != that.type) {
      return type.ordinal() - that.type.ordinal();
    }
    return name.compareTo(that.name);
  }
}
