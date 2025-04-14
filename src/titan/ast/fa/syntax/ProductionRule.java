package titan.ast.fa.syntax;

import java.util.Objects;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.NonterminalGrammar;
import titan.ast.grammar.regexp.AndCompositeRegExp;
import titan.ast.grammar.regexp.RegExp;

/**
 * 产生式.
 *
 * @author tian wei jun
 */
public class ProductionRule implements Comparable<ProductionRule> {

  public final int id;
  // notNull
  public NonterminalGrammar grammar = null;
  public String alias = "";
  public AndCompositeRegExp rule = null;
  public SyntaxNfa nfa;
  // 用于收敛产生式
  public SyntaxDfa reducingDfa = null;

  public ProductionRule() {
    id = AstContext.get().idGenerator.generateProductionRuleId();
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
    return id == that.id;
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
