package titan.ast.impl.ast.contextast;

public class IdentifierAst extends NonterminalContextAst {

  public String identifierStr;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitIdentifierAst(this);
  }
}