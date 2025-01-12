package titan.ast.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 语法对应的 确定有限状态自动机 的状态.
 *
 * @author tian wei jun
 */
public class SyntaxDfaState {
  public int index = 0;
  public int type = FaStateType.NONE.getValue();
  // 转移
  public Map<Grammar, SyntaxDfaState> edges = new HashMap<>();
  public ArrayList<ProductionRule> closingProductionRules = new ArrayList<>();

  public SyntaxDfaState(int index) {
    this.index = index;
  }
}
