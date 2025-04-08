package titan.ast.impl.ast.contextast;

public class RegExpTerminalGrammarAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitRegExpTerminalGrammarAst(this);
  }
}