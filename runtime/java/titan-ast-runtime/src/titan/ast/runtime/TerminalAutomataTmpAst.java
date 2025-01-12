package titan.ast.runtime;

/**
 * .
 *
 * @author tian wei jun
 */
class TerminalAutomataTmpAst extends AutomataTmpAst {
  final Token token;

  public TerminalAutomataTmpAst(Grammar grammar, Token token) {
    super(grammar);
    this.token = token;
  }

  public TerminalAutomataTmpAst cloneForAstAutomata() {
    return new TerminalAutomataTmpAst(grammar, token);
  }

  @Override
  Ast toAst() {
    return new TerminalAst(
        this.grammar.toAstGrammar(), new AstToken(this.token.start, this.token.text));
  }

  @Override
  public String toString() {
    return token.text;
  }
}
