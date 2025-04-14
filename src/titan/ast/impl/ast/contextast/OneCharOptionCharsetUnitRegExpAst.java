package titan.ast.impl.ast.contextast;

import titan.ast.grammar.regexp.OneCharOptionCharsetRegExp;

public class OneCharOptionCharsetUnitRegExpAst extends NonterminalContextAst {
   public OneCharOptionCharsetRegExp oneCharOptionCharsetRegExp;
  @Override
  public void accept(Visitor visitor) {
    visitor.visitOneCharOptionCharsetUnitRegExpAst(this);
  }
}