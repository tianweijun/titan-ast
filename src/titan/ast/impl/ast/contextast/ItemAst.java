package titan.ast.impl.ast.contextast;

public class ItemAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitItemAst(this);
  }
}