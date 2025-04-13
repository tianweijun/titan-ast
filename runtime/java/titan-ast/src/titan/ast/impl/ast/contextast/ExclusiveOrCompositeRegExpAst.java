package titan.ast.impl.ast.contextast;

import titan.ast.grammar.regexp.AndCompositeRegExp;

public class ExclusiveOrCompositeRegExpAst extends NonterminalContextAst {

  public AndCompositeRegExp andCompositeRegExp;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitExclusiveOrCompositeRegExpAst(this);
  }
}