package titan.ast.impl.ast.contextast;

public class DerivedTerminalGrammarBeginningAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitDerivedTerminalGrammarBeginningAst(this);
  }
}