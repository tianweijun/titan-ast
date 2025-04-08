package titan.ast.impl.ast.contextast;

public class DerivedTerminalGrammarAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitDerivedTerminalGrammarAst(this);
  }
}