package titan.ast.impl.ast.contextast;

public class TerminalGrammarBlockAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalGrammarBlockAst(this);
  }
}