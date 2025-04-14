package titan.ast.impl.ast.contextast;

public class TerminalGrammarEndAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalGrammarEndAst(this);
  }
}