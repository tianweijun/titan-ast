package titan.ast.impl.ast.contextast;

public class DerivedTerminalGrammarBlockAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitDerivedTerminalGrammarBlockAst(this);
  }
}