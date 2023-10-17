package titan.ast.grammar.syntax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import titan.ast.grammar.FaStateType;
import titan.ast.grammar.Grammar;
import titan.ast.runtime.AstRuntimeException;

/**
 * 语法的产生式 对应的 非确定有限状态自动机.
 *
 * @author tian wei jun
 */
public class SyntaxNfa implements Cloneable {

  public SyntaxNfaState start = null;
  public SyntaxNfaState end = null;

  // 所代表的正则的first集
  public Set<Grammar> firstGrammars = new HashSet<>();

  /** SyntaxNfa的构造器,默认start、end、不为空. */
  public SyntaxNfa(ProductionRule productionRule) {
    init(productionRule);
  }

  void init(ProductionRule productionRule) {
    start = new SyntaxNfaState(productionRule);
    start.type = FaStateType.NONE.getValue();
    end = new SyntaxNfaState(productionRule);
    end.type = FaStateType.NONE.getValue();
  }

  /**
   * 获得 非确定有限状态自动机的 所有状态.
   *
   * @return 非确定有限状态自动机的 所有状态
   */
  public Set<SyntaxNfaState> getStates() {
    Set<SyntaxNfaState> states = new HashSet<>();
    HashSet<SyntaxNfaState> waitToAddStates = new HashSet<>();
    waitToAddStates.add(this.start);
    waitToAddStates.add(this.end);
    while (!waitToAddStates.isEmpty()) {
      Iterator<SyntaxNfaState> waitToAddStatesIt = waitToAddStates.iterator();
      SyntaxNfaState beAddedState = waitToAddStatesIt.next();
      states.add(beAddedState);
      waitToAddStates.remove(beAddedState);

      for (Set<SyntaxNfaState> chToStates : beAddedState.edges.values()) {
        for (SyntaxNfaState chToState : chToStates) {
          if (!states.contains(chToState)) {
            waitToAddStates.add(chToState);
          }
        }
      }
    }
    return states;
  }

  /**
   * 复制一个自身的副本.
   *
   * @return SyntaxNfa
   */
  public SyntaxNfa clone() {
    SyntaxNfa copy = null;
    try {
      copy = (SyntaxNfa) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AstRuntimeException(e);
    }
    copy.start = null;
    copy.end = null;
    return new SyntaxNfaCloner(copy, this).cloneSyntaxNfa();
  }

  public static class SyntaxNfaCloner {

    SyntaxNfa dest;
    SyntaxNfa source;
    Map<SyntaxNfaState, SyntaxNfaState> sourceDestStateMap;
    Set<SyntaxNfaState> sourceStates;

    SyntaxNfaCloner(SyntaxNfa dest, SyntaxNfa source) {
      this.dest = dest;
      this.source = source;
    }

    /**
     * 找到所有状态，映射新旧状态，clone新旧状态.
     *
     * @return 调用者的副本
     */
    public SyntaxNfa cloneSyntaxNfa() {
      dest.init(source.start.productionRule);
      sourceStates = source.getStates();
      mapSourceDestStates();
      cloneSourceState();

      dest.start = sourceDestStateMap.get(source.start);
      dest.end = sourceDestStateMap.get(source.end);
      return dest;
    }

    private void cloneSourceState() {
      for (SyntaxNfaState sourceState : sourceStates) {
        SyntaxNfaState destState = sourceDestStateMap.get(sourceState);
        destState.type = sourceState.type;
        // edges
        sourceState.edges.forEach(
            (grammar, sourceStateChToStates) -> {
              Set<SyntaxNfaState> destSourceStateChToStates =
                  new HashSet<>(sourceStateChToStates.size());
              for (SyntaxNfaState sourceStateChToState : sourceStateChToStates) {
                destSourceStateChToStates.add(sourceDestStateMap.get(sourceStateChToState));
              }
              destState.edges.put(grammar, destSourceStateChToStates);
            });
      }
    }

    private void mapSourceDestStates() {
      sourceDestStateMap = new HashMap<>(sourceStates.size());
      for (SyntaxNfaState sourceState : sourceStates) {
        SyntaxNfaState destState = new SyntaxNfaState(sourceState.productionRule);
        sourceDestStateMap.put(sourceState, destState);
      }
    }
  }
}
