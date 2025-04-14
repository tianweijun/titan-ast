package titan.ast.impl.ast.contextast;

import titan.ast.grammar.PrimaryGrammarContent.NfaPrimaryGrammarContent;

public class NfaTerminalGrammarAst extends NonterminalContextAst {

  public NfaPrimaryGrammarContent nfaPrimaryGrammarContent;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitNfaTerminalGrammarAst(this);
  }
}