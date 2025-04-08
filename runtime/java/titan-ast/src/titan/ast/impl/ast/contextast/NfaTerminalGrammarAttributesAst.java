package titan.ast.impl.ast.contextast;

public class NfaTerminalGrammarAttributesAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitNfaTerminalGrammarAttributesAst(this);
  }
}