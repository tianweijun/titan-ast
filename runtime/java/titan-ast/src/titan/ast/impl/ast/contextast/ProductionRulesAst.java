package titan.ast.impl.ast.contextast;

public class ProductionRulesAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitProductionRulesAst(this);
  }
}