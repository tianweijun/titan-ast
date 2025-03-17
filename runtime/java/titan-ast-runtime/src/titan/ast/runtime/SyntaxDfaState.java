package titan.ast.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 语法对应的 确定有限状态自动机 的状态.
 *
 * @author tian wei jun
 */
class SyntaxDfaState {
  int index = 0;
  int type = FaStateType.NONE.getValue();
  // 转移
  Map<Grammar, SyntaxDfaState> edges = new HashMap<>();
  ArrayList<ProductionRule> closingProductionRules = new ArrayList<>();

  SyntaxDfaState(int index) {
    this.index = index;
  }
}
