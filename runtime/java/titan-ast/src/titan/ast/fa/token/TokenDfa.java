package titan.ast.fa.token;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * token的 确定有限状态自动机.
 *
 * @author tian wei jun
 */
public class TokenDfa {

  public TokenDfaState start;

  /**
   * 获得 token的 确定有限状态自动机的 所有状态.
   *
   * @return 确定有限状态自动机的 所有状态
   */
  public LinkedHashSet<TokenDfaState> getStates() {
    LinkedHashSet<TokenDfaState> states = new LinkedHashSet<>();
    HashSet<TokenDfaState> waitToScanStates = new HashSet<>();

    waitToScanStates.add(this.start);

    while (!waitToScanStates.isEmpty()) {
      Iterator<TokenDfaState> waitToScanStatesIt = waitToScanStates.iterator();
      TokenDfaState beScanedState = waitToScanStatesIt.next();
      waitToScanStates.remove(beScanedState);
      states.add(beScanedState);

      for (TokenDfaState chToState : beScanedState.edges.values()) {
        if (!states.contains(chToState)) {
          waitToScanStates.add(chToState);
        }
      }
    }
    return states;
  }
}
