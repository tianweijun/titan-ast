package titan.ast.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * token的 确定有限状态自动机的 状态.
 *
 * @author tian wei jun
 */
public class TokenDfaState {
  public int type = FaStateType.NONE.getValue();
  public int weight = 0;
  // token语法名字
  public Grammar terminal = null;
  // 转移
  public Map<Integer, TokenDfaState> edges = new HashMap<>();
}
