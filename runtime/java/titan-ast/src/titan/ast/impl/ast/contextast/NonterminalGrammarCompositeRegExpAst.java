package titan.ast.impl.ast.contextast;

public class NonterminalGrammarCompositeRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitNonterminalGrammarCompositeRegExpAst(this);
  }
}