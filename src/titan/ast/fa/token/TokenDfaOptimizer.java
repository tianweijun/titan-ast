package titan.ast.fa.token;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import titan.ast.fa.FaStateType;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.GrammarCharset;

/**
 * 树形方式最小化tokendfa.
 *
 * @author tian wei jun
 */
public class TokenDfaOptimizer {

  private TokenDfa dfa;
  private final int[] chars;
  private final LinkedList<DfaStatesGroup> willBeFinshedGroups = new LinkedList<>();
  private final LinkedList<DfaStatesGroup> beFinshedGroups = new LinkedList<>();

  public TokenDfaOptimizer() {
    chars = GrammarCharset.getChars();
  }

  public TokenDfa optimize(TokenDfa dfa) {
    this.dfa = dfa;

    int countOfNewDfaStates = -1;
    int countOfOldDfaStates = 0;
    while (countOfNewDfaStates < countOfOldDfaStates) {
      doOptimize();
      countOfOldDfaStates = countOfNewDfaStates;
      countOfNewDfaStates = dfa.getStates().size();
    }
    return dfa;
  }

  /** 删除多余状态 删除死状态 合并等价状态. */
  private void doOptimize() {
    deleteRedundantStates();
    deleteDeadStates();

    buildEquivalentStates();
    buildNewDfaByGroups();
  }

  private void buildNewDfaByGroups() {
    HashMap<TokenDfaState, TokenDfaState> oldNewStateMap = buildOldNewStateMap();
    // build edges
    for (DfaStatesGroup group : beFinshedGroups) {
      TokenDfaState newState = group.newState;
      TokenDfaState oldState = group.reference;
      Map<Integer, TokenDfaState> newEdges = newState.edges;
      Map<Integer, TokenDfaState> oldEdges = oldState.edges;

      oldEdges.forEach(
          (ch, to) -> {
            newEdges.put(ch, oldNewStateMap.get(to));
          });
    }
    TokenDfaState oldStart = dfa.start;
    dfa.start = oldNewStateMap.get(oldStart);
  }

  private HashMap<TokenDfaState, TokenDfaState> buildOldNewStateMap() {
    HashMap<TokenDfaState, TokenDfaState> oldNewStateMap = new HashMap<>();
    for (DfaStatesGroup group : beFinshedGroups) {
      TokenDfaState newState = new TokenDfaState();
      Iterator<TokenDfaState> oldStatesIt = group.equivalentStates.iterator();
      TokenDfaState oldState = null;
      while (oldStatesIt.hasNext()) {
        oldState = oldStatesIt.next();
        oldNewStateMap.put(oldState, newState);
      }
      // clone last one of equivalentStates
      newState.type = oldState.type;
      newState.terminal = oldState.terminal;
      newState.weight = oldState.weight;

      group.newState = newState;
      group.reference = oldState;
    }
    return oldNewStateMap;
  }

  /** 最开始按照终态、非终态分割 按照可能的字符依次分割 按照分割构成新的dfa. */
  private void buildEquivalentStates() {
    beFinshedGroups.clear();
    willBeFinshedGroups.clear();

    divideByClosingState();
    divideByEdges();
  }

  private void divideByEdges() {
    while (!willBeFinshedGroups.isEmpty()) {
      DfaStatesGroup group = willBeFinshedGroups.removeFirst();
      divideByEdges(group);
    }
  }

  private void divideByEdges(DfaStatesGroup group) {
    HashSet<TokenDfaState> equivalentStates = group.equivalentStates;
    if (equivalentStates.size() == 1 || group.indexOfChar == chars.length - 1) {
      group.indexOfChar = chars.length - 1;
      beFinshedGroups.add(group);
      return;
    }
    // 分割边
    int indexOfCharForNewGroup = group.indexOfChar + 1;
    int ch = chars[indexOfCharForNewGroup];
    HashMap<TokenDfaState, HashSet<TokenDfaState>> toFromsMap = new HashMap<>();
    HashSet<TokenDfaState> chToEmptyFroms = new HashSet<>();
    for (TokenDfaState from : equivalentStates) {
      TokenDfaState to = from.edges.get(ch);
      if (null != to) {
        addToFromsMap(toFromsMap, to, from);
      } else {
        chToEmptyFroms.add(from);
      }
    }
    // 重复此操作，直到状态机数量最少时为止
    for (HashSet<TokenDfaState> froms : toFromsMap.values()) {
      DfaStatesGroup fromsStateGroup = new DfaStatesGroup();
      fromsStateGroup.indexOfChar = indexOfCharForNewGroup;
      fromsStateGroup.equivalentStates.addAll(froms);
      willBeFinshedGroups.add(fromsStateGroup);
    }
    if (!chToEmptyFroms.isEmpty()) {
      DfaStatesGroup chToEmptyFromsStateGroup = new DfaStatesGroup();
      chToEmptyFromsStateGroup.indexOfChar = indexOfCharForNewGroup;
      chToEmptyFromsStateGroup.equivalentStates.addAll(chToEmptyFroms);
      willBeFinshedGroups.add(chToEmptyFromsStateGroup);
    }
  }

  private void addToFromsMap(
      HashMap<TokenDfaState, HashSet<TokenDfaState>> toFromsMap,
      TokenDfaState to,
      TokenDfaState from) {
    HashSet<TokenDfaState> froms = toFromsMap.get(to);
    if (null == froms) {
      froms = new HashSet<>();
      toFromsMap.put(to, froms);
    }
    froms.add(from);
  }

  private void divideByClosingState() {
    DfaStatesGroup normalStatesGroup = new DfaStatesGroup();
    HashMap<Grammar, HashSet<TokenDfaState>> equivalentClosingStatesMap = new HashMap<>();
    // divide by closing tag
    for (TokenDfaState state : dfa.getStates()) {
      if (FaStateType.isClosingTag(state.type)) {
        addToEquivalentClosingStatesMap(equivalentClosingStatesMap, state);
      } else {
        normalStatesGroup.equivalentStates.add(state);
      }
    }
    // normalTerminals
    willBeFinshedGroups.add(normalStatesGroup);
    // closingStatesGroupOfTerminal
    for (HashSet<TokenDfaState> closingStatesOfTerminal : equivalentClosingStatesMap.values()) {
      DfaStatesGroup closingStatesGroupOfTerminal = new DfaStatesGroup();
      closingStatesGroupOfTerminal.equivalentStates.addAll(closingStatesOfTerminal);
      willBeFinshedGroups.add(closingStatesGroupOfTerminal);
    }
  }

  /**
   * 拥有 相同的收敛终结符 的状态 有可能是等价的.
   *
   * @param equivalentClosingStatesMap 保存终结符 和 closing状态 对应关系的 集合
   * @param closingState 状态
   */
  private void addToEquivalentClosingStatesMap(
      HashMap<Grammar, HashSet<TokenDfaState>> equivalentClosingStatesMap,
      TokenDfaState closingState) {
    Grammar terminal = closingState.terminal;
    HashSet<TokenDfaState> closingStatesOfTerminal = equivalentClosingStatesMap.get(terminal);
    if (null == closingStatesOfTerminal) {
      closingStatesOfTerminal = new HashSet<>();
      equivalentClosingStatesMap.put(terminal, closingStatesOfTerminal);
    }
    closingStatesOfTerminal.add(closingState);
  }

  /** 删除出度为0的非终态节点. */
  private void deleteDeadStates() {
    boolean hasDeleteDeadStates;
    do {
      hasDeleteDeadStates = doDeleteDeadStates();
    } while (hasDeleteDeadStates);
  }

  private boolean doDeleteDeadStates() {
    HashSet<TokenDfaState> deadStates = new HashSet<>();
    LinkedHashSet<TokenDfaState> states = dfa.getStates();
    // find dead states
    for (TokenDfaState state : states) {
      if (!FaStateType.isClosingTag(state.type)) {
        if (state.edges.isEmpty()) {
          deadStates.add(state);
        }
      }
    }
    // delete dead states
    boolean hasDeleteDeadStates = false;
    if (!deadStates.isEmpty()) {
      for (TokenDfaState state : states) {
        Iterator<Map.Entry<Integer, TokenDfaState>> stateEdgesIt =
            state.edges.entrySet().iterator();
        while (stateEdgesIt.hasNext()) {
          Map.Entry<Integer, TokenDfaState> entry = stateEdgesIt.next();
          TokenDfaState toState = entry.getValue();
          if (deadStates.contains(toState)) {
            stateEdgesIt.remove();
            hasDeleteDeadStates = true;
          }
        }
      }
    }
    return hasDeleteDeadStates;
  }

  /** 子集法 决定了没有多余的状态. */
  private void deleteRedundantStates() {}

  public static class DfaStatesGroup {

    int indexOfChar = -1;
    TokenDfaState newState;
    TokenDfaState reference;

    HashSet<TokenDfaState> equivalentStates = new HashSet<>();
  }
}
