package titan.ast.runtime;

/**
 * 语法（文法）.
 *
 * @author tian wei jun
 */
public class Grammar {
  public int index;
  public String name = "";
  public GrammarType type = GrammarType.TERMINAL;
  public GrammarAction action = GrammarAction.TEXT;

  public Grammar(int index) {
    this.index = index;
  }

  @Override
  public String toString() {
    return name;
  }

  public AstGrammar toAstGrammar() {
    return new AstGrammar(name, type);
  }
}
