package titan.ast.impl.ast.contextast;

public class DerivedTerminalGrammarEndAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitDerivedTerminalGrammarEndAst(this);
  }
}