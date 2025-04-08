package titan.ast.impl.ast.contextast;

public class TerminalGrammarAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalGrammarAst(this);
  }
}