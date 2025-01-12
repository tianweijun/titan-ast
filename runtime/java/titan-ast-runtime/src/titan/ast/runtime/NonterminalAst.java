package titan.ast.runtime;

/**
 * .
 *
 * @author tian wei jun
 */
public class NonterminalAst extends Ast {
  public final String alias;

  public NonterminalAst(AstGrammar grammar, String alias) {
    super(grammar);
    this.alias = alias;
  }

  @Override
  public String toString() {
    String displayString = "";
    if (StringUtils.isNotBlank(alias)) {
      displayString = grammar.name + "[" + alias + "]";
    } else {
      displayString = grammar.name;
    }
    return displayString;
  }
}
