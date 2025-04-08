package titan.ast.impl.ast.contextast;

public class IdentifierAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitIdentifierAst(this);
  }
}