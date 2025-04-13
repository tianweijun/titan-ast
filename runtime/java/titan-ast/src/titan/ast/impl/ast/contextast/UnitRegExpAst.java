package titan.ast.impl.ast.contextast;

import titan.ast.grammar.regexp.UnitRegExp;

public class UnitRegExpAst extends NonterminalContextAst {

  public UnitRegExp unitRegExp;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitUnitRegExpAst(this);
  }
}