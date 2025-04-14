package titan.ast.impl.ast.contextast;

public class NonterminalGrammarBlockAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitNonterminalGrammarBlockAst(this);
  }
}