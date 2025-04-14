package titan.ast.impl.ast.contextast;

public class TerminalFragmentGrammarEndAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalFragmentGrammarEndAst(this);
  }
}