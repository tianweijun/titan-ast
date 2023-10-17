package titan.ast.grammar.syntax;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * 语法对应的 确定有限状态自动机.
 *
 * @author tian wei jun
 */
public class SyntaxDfa {

  public SyntaxDfaState start = null;

  /**
   * 获得 syntax的 确定有限状态自动机的 所有状态.
   *
   * @return 确定有限状态自动机的 所有状态
   */
  public LinkedHashSet<SyntaxDfaState> getStates() {
    LinkedHashSet<SyntaxDfaState> states = new LinkedHashSet<>();
    HashSet<SyntaxDfaState> waitToScanStates = new HashSet<>();
    waitToScanStates.add(this.start);
    while (!waitToScanStates.isEmpty()) {
      Iterator<SyntaxDfaState> waitToScanStatesIt = waitToScanStates.iterator();
      SyntaxDfaState beScanedState = waitToScanStatesIt.next();
      waitToScanStates.remove(beScanedState);
      states.add(beScanedState);

      for (SyntaxDfaState chToState : beScanedState.edges.values()) {
        if (!states.contains(chToState)) {
          waitToScanStates.add(chToState);
        }
      }
    }
    return states;
  }
}
