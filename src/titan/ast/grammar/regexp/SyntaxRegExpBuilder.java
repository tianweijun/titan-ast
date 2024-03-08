package titan.ast.grammar.regexp;

import titan.ast.grammar.Grammar;

/**
 * 构造正则，但是尚未解决helper和alias的问题,责任转移到产生式的建造者上.
 *
 * @author tian wei jun
 */
public class SyntaxRegExpBuilder extends AbstractRegExpBuilder {

  /**
   * 或关系 别名(忽略，产生式生成阶段在处理).
   *
   * @param nonterminal 非终结符
   */
  @Override
  public void postProcessInitRegExp(Grammar nonterminal) {
    buildOrRelationship(nonterminal.regExp);
  }
}
