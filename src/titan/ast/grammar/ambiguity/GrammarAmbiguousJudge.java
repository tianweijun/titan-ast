package titan.ast.grammar.ambiguity;

import titan.ast.AstContext;

/**
 * 语法二义判断器.
 *
 * @author tian wei jun
 */
public class GrammarAmbiguousJudge {

  /**
   * 语法的text必须填充好.
   *
   * @return GrammarAmbiguousJudgeResult 判断结果
   */
  public GrammarAmbiguousJudgeResult isAmbiguous() {
    AstContext astContext = AstContext.get();
    return new GrammarAmbiguousJudgeResult();
  }
}
