package titan.ast.impl.ast.contextast;

public class ProductionRuleAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitProductionRuleAst(this);
  }
}