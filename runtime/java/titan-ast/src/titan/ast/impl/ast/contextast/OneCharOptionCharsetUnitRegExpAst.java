package titan.ast.impl.ast.contextast;

public class OneCharOptionCharsetUnitRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitOneCharOptionCharsetUnitRegExpAst(this);
  }
}