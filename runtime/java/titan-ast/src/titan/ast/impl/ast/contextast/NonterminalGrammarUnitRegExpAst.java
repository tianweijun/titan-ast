package titan.ast.impl.ast.contextast;

public class NonterminalGrammarUnitRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitNonterminalGrammarUnitRegExpAst(this);
  }
}