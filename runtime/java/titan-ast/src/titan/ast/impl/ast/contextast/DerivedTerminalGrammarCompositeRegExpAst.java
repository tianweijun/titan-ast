package titan.ast.impl.ast.contextast;

public class DerivedTerminalGrammarCompositeRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitDerivedTerminalGrammarCompositeRegExpAst(this);
  }
}