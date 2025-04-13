package titan.ast.impl.ast.contextast;

import titan.ast.grammar.regexp.ParenthesisRegExp;

public class ParenthesisUnitRegExpAst extends NonterminalContextAst {

  public ParenthesisRegExp parenthesisRegExp;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitParenthesisUnitRegExpAst(this);
  }
}