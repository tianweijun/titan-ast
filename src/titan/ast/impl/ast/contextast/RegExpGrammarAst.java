package titan.ast.impl.ast.contextast;

import titan.ast.grammar.PrimaryGrammarContent.RegExpPrimaryGrammarContent;

public class RegExpGrammarAst extends NonterminalContextAst {

  public RegExpPrimaryGrammarContent regExpPrimaryGrammarContent;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitRegExpGrammarAst(this);
  }
}