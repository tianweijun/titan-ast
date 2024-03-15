package titan.ast.runtime;

/**
 * .
 *
 * @author tian wei jun
 */
public class AstGrammar {
  public String name = "";
  public GrammarType type = GrammarType.TERMINAL;

  public AstGrammar(String name, GrammarType type) {
    this.name = name;
    this.type = type;
  }
}
