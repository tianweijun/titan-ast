package titan.ast.impl.ast.contextast;

public class TerminalGrammarCompositeRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalGrammarCompositeRegExpAst(this);
  }
}