package titan.ast.grammar.token;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import titan.ast.grammar.FaStateType;
import titan.ast.runtime.AstRuntimeException;

/**
 * 非确定有限状态自动机.
 *
 * @author tian wei jun
 */
public class TokenNfa implements Cloneable {

  public TokenNfaState start;
  public TokenNfaState end;

  TokenNfa() {
    init();
  }

  void init() {
    start = new TokenNfaState();
    start.type = FaStateType.NONE.getValue();
    end = new TokenNfaState();
    end.type = FaStateType.NONE.getValue();
  }

  /**
   * 获得 非确定有限状态自动机的 所有状态.
   *
   * @return 非确定有限状态自动机的 所有状态
   */
  public Set<TokenNfaState> getStates() {
    HashSet<TokenNfaState> states = new HashSet<>();
    HashSet<TokenNfaState> waitToAddStates = new HashSet<>();
    waitToAddStates.add(this.start);
    waitToAddStates.add(this.end);
    while (!waitToAddStates.isEmpty()) {
      Iterator<TokenNfaState> waitToAddStatesIt = waitToAddStates.iterator();
      TokenNfaState beAddedState = waitToAddStatesIt.next();
      states.add(beAddedState);
      waitToAddStates.remove(beAddedState);

      for (Set<TokenNfaState> chToStates : beAddedState.edges.values()) {
        for (TokenNfaState chToState : chToStates) {
          if (!states.contains(chToState)) {
            waitToAddStates.add(chToState);
          }
        }
      }
    }
    return states;
  }

  public TokenNfa clone() {
    TokenNfa copy = null;
    try {
      copy = (TokenNfa) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AstRuntimeException(e);
    }
    copy.start = null;
    copy.end = null;
    return new TokenNfaCloner(copy, this).cloneTokenNfa();
  }

  public static class TokenNfaCloner {

    TokenNfa dest;
    TokenNfa source;
    Map<TokenNfaState, TokenNfaState> sourceDestStateMap;
    Set<TokenNfaState> sourceStates;

    TokenNfaCloner(TokenNfa dest, TokenNfa source) {
      this.dest = dest;
      this.source = source;
    }

    /**
     * 找到所有状态，映射新旧状态，clone新旧状态.
     *
     * @return 调用者的副本
     */
    public TokenNfa cloneTokenNfa() {
      dest.init();
      sourceStates = source.getStates();
      mapSourceDestStates();
      cloneSourceState();

      dest.start = sourceDestStateMap.get(source.start);
      dest.end = sourceDestStateMap.get(source.end);
      return dest;
    }

    private void cloneSourceState() {
      for (TokenNfaState sourceState : sourceStates) {
        TokenNfaState destState = sourceDestStateMap.get(sourceState);
        destState.type = sourceState.type;
        destState.terminal = sourceState.terminal;
        // edges
        sourceState.edges.forEach(
            (ch, sourceStateChToStates) -> {
              Set<TokenNfaState> destStateChToStates = new HashSet<>(sourceStateChToStates.size());
              for (TokenNfaState sourceStateChToState : sourceStateChToStates) {
                destStateChToStates.add(sourceDestStateMap.get(sourceStateChToState));
              }
              destState.edges.put(ch, destStateChToStates);
            });
      }
    }

    private void mapSourceDestStates() {
      sourceDestStateMap = new HashMap<>(sourceStates.size());
      for (TokenNfaState sourceState : sourceStates) {
        TokenNfaState destState = new TokenNfaState();
        sourceDestStateMap.put(sourceState, destState);
      }
    }
  }
}
