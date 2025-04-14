package titan.ast.impl.ast.contextast;

public class TerminalFragmentGrammarBlockAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalFragmentGrammarBlockAst(this);
  }
}