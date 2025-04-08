package titan.ast.impl.ast.contextast;

public class NfaTerminalGrammarAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitNfaTerminalGrammarAst(this);
  }
}