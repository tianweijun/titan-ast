package titan.ast.impl.ast.contextast;

public class GrammarUnitRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitGrammarUnitRegExpAst(this);
  }
}