package titan.ast.impl.ast.contextast;

public class NonterminalGrammarEndAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitNonterminalGrammarEndAst(this);
  }
}