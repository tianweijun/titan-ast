package titan.ast.impl.ast.contextast;

import titan.ast.grammar.GrammarAttribute;

public class GrammarAttributeAst extends NonterminalContextAst {

  public GrammarAttribute grammarAttribute;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitGrammarAttributeAst(this);
  }
}