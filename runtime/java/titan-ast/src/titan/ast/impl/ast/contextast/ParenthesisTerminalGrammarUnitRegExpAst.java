package titan.ast.impl.ast.contextast;

public class ParenthesisTerminalGrammarUnitRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitParenthesisTerminalGrammarUnitRegExpAst(this);
  }
}