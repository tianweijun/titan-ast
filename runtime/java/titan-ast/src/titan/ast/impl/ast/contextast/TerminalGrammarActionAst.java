package titan.ast.impl.ast.contextast;

public class TerminalGrammarActionAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalGrammarActionAst(this);
  }
}