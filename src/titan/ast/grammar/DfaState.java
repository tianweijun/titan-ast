package titan.ast.grammar;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 确定有限状态自动机 状态.
 *
 * @author tian wei jun
 */
public class DfaState<C, S> implements Comparable<DfaState<C, S>> {

  public int id = 0;
  public int type = FaStateType.NONE.getValue();
  // 转移
  public Map<C, S> edges = new LinkedHashMap<>();

  public DfaState() {}

  @Override
  public int compareTo(DfaState<C, S> o) {
    return this.id - o.id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DfaState<?, ?> dfaState = (DfaState<?, ?>) o;

    return id == dfaState.id;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
