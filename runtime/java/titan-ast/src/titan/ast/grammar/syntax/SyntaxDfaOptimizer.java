package titan.ast.grammar.syntax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import titan.ast.AstContext;
import titan.ast.grammar.FaStateType;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;

/**
 * 树形方式最小化syntaxdfa.
 *
 * @author tian wei jun
 */
public class SyntaxDfaOptimizer {

  private SyntaxDfa dfa;
  private Grammar[] inputGrammars;
  private final LinkedList<DfaStatesGroup> willBeFinshedGroups = new LinkedList<>();
  private final LinkedList<DfaStatesGroup> beFinshedGroups = new LinkedList<>();

  public SyntaxDfaOptimizer() {
    initInputGrammars();
  }

  private void initInputGrammars() {
    LanguageGrammar languageGrammar = AstContext.get().languageGrammar;
    LinkedHashMap<String, Grammar> nonterminals = languageGrammar.nonterminals;
    LinkedHashMap<String, Grammar> terminals = languageGrammar.terminals;
    inputGrammars = new Grammar[nonterminals.size() + terminals.size()];
    int indexOfInputGrammar = 0;
    for (Grammar inputGrammar : nonterminals.values()) {
      inputGrammars[indexOfInputGrammar++] = inputGrammar;
    }
    for (Grammar inputGrammar : terminals.values()) {
      inputGrammars[indexOfInputGrammar++] = inputGrammar;
    }
  }

  public SyntaxDfa optimize(SyntaxDfa dfa) {
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
    HashMap<SyntaxDfaState, SyntaxDfaState> oldNewStateMap = buildOldNewStateMap();
    // build edges
    for (DfaStatesGroup group : beFinshedGroups) {
      SyntaxDfaState newState = group.newState;
      SyntaxDfaState oldState = group.reference;
      Map<Grammar, SyntaxDfaState> newEdges = newState.edges;
      Map<Grammar, SyntaxDfaState> oldEdges = oldState.edges;

      oldEdges.forEach(
          (inputGrammar, to) -> {
            newEdges.put(inputGrammar, oldNewStateMap.get(to));
          });
    }
    SyntaxDfaState oldStart = dfa.start;
    dfa.start = oldNewStateMap.get(oldStart);
  }

  private HashMap<SyntaxDfaState, SyntaxDfaState> buildOldNewStateMap() {
    HashMap<SyntaxDfaState, SyntaxDfaState> oldNewStateMap = new HashMap<>();
    for (DfaStatesGroup group : beFinshedGroups) {
      SyntaxDfaState newState = new SyntaxDfaState();
      Iterator<SyntaxDfaState> oldStatesIt = group.equivalentStates.iterator();
      SyntaxDfaState oldState = null;
      while (oldStatesIt.hasNext()) {
        oldState = oldStatesIt.next();
        oldNewStateMap.put(oldState, newState);
      }
      // clone last one of equivalentStates
      newState.type = oldState.type;
      newState.closingProductionRules.addAll(oldState.closingProductionRules);

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
    HashSet<SyntaxDfaState> equivalentStates = group.equivalentStates;
    if (equivalentStates.size() == 1 || group.indexOfInputGrammar == inputGrammars.length - 1) {
      group.indexOfInputGrammar = inputGrammars.length - 1;
      beFinshedGroups.add(group);
      return;
    }
    // 分割边
    int indexOfInputGrammarForNewGroup = group.indexOfInputGrammar + 1;
    Grammar inputGrammar = inputGrammars[indexOfInputGrammarForNewGroup];
    HashMap<SyntaxDfaState, HashSet<SyntaxDfaState>> toFromsMap = new HashMap<>();
    HashSet<SyntaxDfaState> chToEmptyFroms = new HashSet<>();
    for (SyntaxDfaState from : equivalentStates) {
      SyntaxDfaState to = from.edges.get(inputGrammar);
      if (null != to) {
        addToFromsMap(toFromsMap, to, from);
      } else {
        chToEmptyFroms.add(from);
      }
    }
    // 重复此操作，直到状态机数量最少时为止
    for (HashSet<SyntaxDfaState> froms : toFromsMap.values()) {
      DfaStatesGroup fromsStateGroup = new DfaStatesGroup();
      fromsStateGroup.indexOfInputGrammar = indexOfInputGrammarForNewGroup;
      fromsStateGroup.equivalentStates.addAll(froms);
      willBeFinshedGroups.add(fromsStateGroup);
    }
    if (!chToEmptyFroms.isEmpty()) {
      DfaStatesGroup chToEmptyFromsStateGroup = new DfaStatesGroup();
      chToEmptyFromsStateGroup.indexOfInputGrammar = indexOfInputGrammarForNewGroup;
      chToEmptyFromsStateGroup.equivalentStates.addAll(chToEmptyFroms);
      willBeFinshedGroups.add(chToEmptyFromsStateGroup);
    }
  }

  private void addToFromsMap(
      HashMap<SyntaxDfaState, HashSet<SyntaxDfaState>> toFromsMap,
      SyntaxDfaState to,
      SyntaxDfaState from) {
    HashSet<SyntaxDfaState> froms = toFromsMap.get(to);
    if (null == froms) {
      froms = new HashSet<>();
      toFromsMap.put(to, froms);
    }
    froms.add(from);
  }

  private void divideByClosingState() {
    DfaStatesGroup normalStatesGroup = new DfaStatesGroup();
    HashMap<TreeSet<ProductionRule>, HashSet<SyntaxDfaState>> equivalentClosingStatesMap =
        new HashMap<>();
    // divide by closing tag
    for (SyntaxDfaState state : dfa.getStates()) {
      if (FaStateType.isClosingTag(state.type)) {
        addToEquivalentClosingStatesMap(equivalentClosingStatesMap, state);
      } else {
        normalStatesGroup.equivalentStates.add(state);
      }
    }
    // normalTerminals
    willBeFinshedGroups.add(normalStatesGroup);
    // closingStatesGroupOfProductionRule
    for (HashSet<SyntaxDfaState> equivalentClosingStates : equivalentClosingStatesMap.values()) {
      DfaStatesGroup closingStatesGroup = new DfaStatesGroup();
      closingStatesGroup.equivalentStates.addAll(equivalentClosingStates);
      willBeFinshedGroups.add(closingStatesGroup);
    }
  }

  /**
   * 拥有 相同的收敛产生式 的状态 有可能是等价的.
   *
   * @param equivalentClosingStatesMap 保存缠身是 和 closing状态 对应关系的 集合
   * @param closingState 状态
   */
  private void addToEquivalentClosingStatesMap(
      HashMap<TreeSet<ProductionRule>, HashSet<SyntaxDfaState>> equivalentClosingStatesMap,
      SyntaxDfaState closingState) {
    HashSet<SyntaxDfaState> equivalentClosingStates =
        equivalentClosingStatesMap.get(closingState.closingProductionRules);
    if (null == equivalentClosingStates) {
      equivalentClosingStates = new HashSet<>();
      equivalentClosingStatesMap.put(closingState.closingProductionRules, equivalentClosingStates);
    }
    equivalentClosingStates.add(closingState);
  }

  /** 删除出度为0的非终态节点. */
  private void deleteDeadStates() {
    boolean hasDeleteDeadStates;
    do {
      hasDeleteDeadStates = doDeleteDeadStates();
    } while (hasDeleteDeadStates);
  }

  private boolean doDeleteDeadStates() {
    HashSet<SyntaxDfaState> deadStates = new HashSet<>();
    LinkedHashSet<SyntaxDfaState> states = dfa.getStates();
    // find dead states
    for (SyntaxDfaState state : states) {
      if (!FaStateType.isClosingTag(state.type)) {
        if (state.edges.isEmpty()) {
          deadStates.add(state);
        }
      }
    }
    // delete dead states
    boolean hasDeleteDeadStates = false;
    if (!deadStates.isEmpty()) {
      for (SyntaxDfaState state : states) {
        Iterator<Entry<Grammar, SyntaxDfaState>> stateEdgesIt = state.edges.entrySet().iterator();
        while (stateEdgesIt.hasNext()) {
          Map.Entry<Grammar, SyntaxDfaState> entry = stateEdgesIt.next();
          SyntaxDfaState toState = entry.getValue();
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

    int indexOfInputGrammar = -1;
    SyntaxDfaState newState = null;
    SyntaxDfaState reference = null;

    HashSet<SyntaxDfaState> equivalentStates = new HashSet<>();
  }
}
