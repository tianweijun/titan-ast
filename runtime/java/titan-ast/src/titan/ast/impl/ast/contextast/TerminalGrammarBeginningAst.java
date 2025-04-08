package titan.ast.impl.ast.contextast;

public class TerminalGrammarBeginningAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalGrammarBeginningAst(this);
  }
}