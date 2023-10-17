package titan.ast.runtime;

/**
 * 产生式.
 *
 * @author tian wei jun
 */
public class ProductionRule {
  // notNull
  public Grammar grammar = null;
  public String alias = "";
  // 用于收敛产生式
  public SyntaxDfa reducingDfa = null;
}
