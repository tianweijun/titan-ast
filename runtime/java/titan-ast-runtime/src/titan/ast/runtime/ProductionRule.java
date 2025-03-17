package titan.ast.runtime;

/**
 * 产生式.
 *
 * @author tian wei jun
 */
class ProductionRule {
  // notNull
  Grammar grammar = null;
  String alias = "";
  // 用于收敛产生式
  SyntaxDfa reducingDfa = null;
}
