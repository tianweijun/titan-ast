package titan.ast.impl.ast.contextast;

public class TerminalFragmentGrammarBeginningAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalFragmentGrammarBeginningAst(this);
  }
}