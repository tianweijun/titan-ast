package titan.ast.impl.ast.contextast;

public class InclusiveTerminalGrammarCompositeRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitInclusiveTerminalGrammarCompositeRegExpAst(this);
  }
}