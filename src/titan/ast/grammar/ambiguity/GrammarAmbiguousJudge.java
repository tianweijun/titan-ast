package titan.ast.grammar.ambiguity;

import titan.ast.AstContext;

/**
 * 语法二义判断器.
 *
 * @author tian wei jun
 */
public class GrammarAmbiguousJudge {

  /**
   * 前置前提：语法的产生式已构建.
   *
   * @return GrammarAmbiguousJudgeResult 判断结果
   */
  public GrammarAmbiguousJudgeResult isAmbiguous() {
    AstContext astContext = AstContext.get();
    return new GrammarAmbiguousJudgeResult();
  }
}
