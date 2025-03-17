package titan.ast.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * token的 确定有限状态自动机的 状态.
 *
 * @author tian wei jun
 */
class TokenDfaState {
  int type = FaStateType.NONE.getValue();
  int weight = 0;
  // token语法名字
  Grammar terminal = null;
  // 转移
  Map<Integer, TokenDfaState> edges = new HashMap<>();
}
