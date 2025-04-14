package titan.ast.impl.ast.contextast;

import titan.ast.grammar.PrimaryGrammarContent;

public class TerminalGrammarAst extends NonterminalContextAst {

  public PrimaryGrammarContent primaryGrammarContent;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalGrammarAst(this);
  }
}