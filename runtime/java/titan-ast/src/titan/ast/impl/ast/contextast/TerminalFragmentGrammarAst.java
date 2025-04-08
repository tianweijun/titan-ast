package titan.ast.impl.ast.contextast;

public class TerminalFragmentGrammarAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalFragmentGrammarAst(this);
  }
}