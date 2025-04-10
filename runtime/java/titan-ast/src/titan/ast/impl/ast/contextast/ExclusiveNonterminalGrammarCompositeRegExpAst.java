package titan.ast.impl.ast.contextast;

public class ExclusiveNonterminalGrammarCompositeRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitExclusiveNonterminalGrammarCompositeRegExpAst(this);
  }
}