package titan.ast.fa;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import titan.ast.AstContext;

/**
 * 非确定有限状态自动机的 状态.
 *
 * @author tian wei jun
 */
public class NfaState<C, S> implements Comparable<NfaState<C, S>> {

  public final int id;
  public int type = FaStateType.NONE.getValue();
  public Map<C, Set<S>> edges = new HashMap<>();

  public NfaState(int id) {
    this.id = id;
  }

  @Override
  public int compareTo(NfaState<C, S> o) {
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

    NfaState<?, ?> nfaState = (NfaState<?, ?>) o;

    return id == nfaState.id;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
