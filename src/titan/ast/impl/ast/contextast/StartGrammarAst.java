package titan.ast.impl.ast.contextast;

public class StartGrammarAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitStartGrammarAst(this);
  }
}