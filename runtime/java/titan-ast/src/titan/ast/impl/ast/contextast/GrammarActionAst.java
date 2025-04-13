package titan.ast.impl.ast.contextast;

import titan.ast.grammar.GrammarAction;

public class GrammarActionAst extends NonterminalContextAst {

  public GrammarAction grammarAction;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitGrammarActionAst(this);
  }
}