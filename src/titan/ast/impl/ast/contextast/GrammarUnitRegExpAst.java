package titan.ast.impl.ast.contextast;

import titan.ast.grammar.regexp.GrammarRegExp;

public class GrammarUnitRegExpAst extends NonterminalContextAst {

  public GrammarRegExp grammarRegExp;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitGrammarUnitRegExpAst(this);
  }
}