package titan.ast.impl.ast.contextast;

import titan.ast.grammar.regexp.OrCompositeRegExp;

public class InclusiveOrCompositeRegExpAst extends NonterminalContextAst {

  public OrCompositeRegExp orCompositeRegExp;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitInclusiveOrCompositeRegExpAst(this);
  }
}