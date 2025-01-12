package titan.ast.grammar.regexp;

import titan.ast.grammar.Grammar;

/**
 * 建立grammar的依赖关系.
 *
 * @author tian wei jun
 */
public class TokenRegExpBuilder extends AbstractRegExpBuilder {

  /**
   * 去掉别名 和 处理或关系.
   *
   * @param grammar 已经初始化好的语法
   */
  @Override
  public void postProcessInitRegExp(Grammar grammar) {
    RegExp.deleteAlias(grammar.regExp);
    buildOrRelationship(grammar.regExp);
  }
}
