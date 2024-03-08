package titan.ast.grammar.syntax;

import java.util.Objects;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.regexp.RegExp;

/**
 * 产生式.
 *
 * @author tian wei jun
 */
public class ProductionRule implements Comparable<ProductionRule> {

  public Integer id = 0;
  // notNull
  public Grammar grammar = null;
  public String alias = "";
  public RegExp rule = null;
  // 用于收敛产生式
  public SyntaxDfa reducingDfa = null;

  public ProductionRule() {
    id = AstContext.get().resourceGenerator.generateProductionRuleId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductionRule that = (ProductionRule) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return grammar.name + "[" + rule.toString() + "]";
  }

  @Override
  public int compareTo(ProductionRule o) {
    return this.id - o.id;
  }
}
