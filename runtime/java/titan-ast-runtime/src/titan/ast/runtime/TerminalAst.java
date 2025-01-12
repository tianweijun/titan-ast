package titan.ast.runtime;

/**
 * .
 *
 * @author tian wei jun
 */
public class TerminalAst extends Ast {
  public final AstToken token;

  public TerminalAst(AstGrammar grammar, AstToken token) {
    super(grammar);
    this.token = token;
  }

  @Override
  public String toString() {
    return token.text;
  }
}
