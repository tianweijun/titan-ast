package titan.ast.impl.ast.contextast;

public class ParenthesisNonterminalGrammarUnitRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitParenthesisNonterminalGrammarUnitRegExpAst(this);
  }
}