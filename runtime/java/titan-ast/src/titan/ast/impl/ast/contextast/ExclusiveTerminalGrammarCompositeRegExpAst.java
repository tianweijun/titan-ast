package titan.ast.impl.ast.contextast;

public class ExclusiveTerminalGrammarCompositeRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitExclusiveTerminalGrammarCompositeRegExpAst(this);
  }
}