package titan.ast.runtime;

/**
 * 语法（文法）.
 *
 * @author tian wei jun
 */
public class Grammar implements Comparable<Grammar> {
  int index;
  String name = "";
  GrammarType type = GrammarType.TERMINAL;
  GrammarAction action = GrammarAction.TEXT;

  public Grammar(int index) {
    this.index = index;
  }

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

  public AstGrammar toAstGrammar() {
    return new AstGrammar(name, type);
  }

  @Override
  public int compareTo(Grammar that) {
    return this.index - that.index;
  }
}
