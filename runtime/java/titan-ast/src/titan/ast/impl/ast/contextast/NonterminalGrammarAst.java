package titan.ast.impl.ast.contextast;

public class NonterminalGrammarAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitNonterminalGrammarAst(this);
  }
}