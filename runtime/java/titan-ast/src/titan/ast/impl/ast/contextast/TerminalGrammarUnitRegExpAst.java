package titan.ast.impl.ast.contextast;

public class TerminalGrammarUnitRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalGrammarUnitRegExpAst(this);
  }
}