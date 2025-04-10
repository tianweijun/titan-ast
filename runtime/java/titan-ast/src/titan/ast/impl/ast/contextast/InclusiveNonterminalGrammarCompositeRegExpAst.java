package titan.ast.impl.ast.contextast;

public class InclusiveNonterminalGrammarCompositeRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitInclusiveNonterminalGrammarCompositeRegExpAst(this);
  }
}