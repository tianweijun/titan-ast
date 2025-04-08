package titan.ast.impl.ast.contextast;

public class NonterminalGrammarBeginningAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitNonterminalGrammarBeginningAst(this);
  }
}