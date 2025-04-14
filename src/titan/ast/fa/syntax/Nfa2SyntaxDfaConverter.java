package titan.ast.fa.syntax;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import titan.ast.AstContext;
import titan.ast.fa.FaStateType;
import titan.ast.grammar.Grammar;

/**
 * 将非终结符的nfa转为dfa.
 *
 * @author tian wei jun
 */
public class Nfa2SyntaxDfaConverter {

  private final Grammar epsilon;

  Nfa2SyntaxDfaConverter() {
    epsilon = AstContext.get().languageGrammar.epsilon;
  }

  public SyntaxDfa convert(SyntaxNfa nfa) {
    SyntaxDfa dfa = createDfa(nfa);
    dfa.start.type = FaStateType.appendOpeningTag(dfa.start.type);
    setClosingTagOfStateAndClosingProductionRules(dfa);
    return dfa;
  }

  private void setClosingTagOfStateAndClosingProductionRules(SyntaxDfa dfa) {
    for (SyntaxDfaState dfaState : dfa.getStates()) {
      for (SyntaxNfaState nfaState : dfaState.nfaStates) {
        if (FaStateType.isClosingTag(nfaState.type)) {
          dfaState.closingProductionRules.add(nfaState.productionRule);
          dfaState.type = FaStateType.appendClosingTag(dfaState.type);
        }
      }
    }
  }

  /**
   * 子集法 设字母表只包含两个a和b，我们构造一张计算状态集的转换表: 1.计算所有状态的闭包 2.置第1行第1列为ε-closure({X})求出这一列的Ia,Ib；
   * 3.检查这两个Ia,Ib，看它们是否已在表中的第一列中出现，未曾出现的填入后面的空行的第1列上， 4.求出每行第2，3列上的集合...
   * 重复上述过程，直到所有第2，3列子集全部出现在第一列为止。 5.设置dfa边和终态的权重， 比较终态和边的权重，参照最大权重终边，若大于等于则是收敛字符集，若小于则转移字符集。
   */
  private SyntaxDfa createDfa(SyntaxNfa nfa) {
    SyntaxNfaStateEpsilonClosureGetter nfaStateEpsilonClosureGetter =
        new SyntaxNfaStateEpsilonClosureGetter();
    TreeSet<SyntaxNfaState> startNfaStates = nfaStateEpsilonClosureGetter.get(nfa.start);

    SyntaxDfa dfa = new SyntaxDfa();
    SyntaxDfaState startDfaState = new SyntaxDfaState(startNfaStates);
    dfa.start = startDfaState;

    LinkedHashSet<SyntaxDfaState> waitToBuildDfaStates = new LinkedHashSet<>();
    LinkedHashSet<SyntaxDfaState> beBeuildedDfaStates = new LinkedHashSet<>();

    waitToBuildDfaStates.add(startDfaState);
    while (!waitToBuildDfaStates.isEmpty()) {
      SyntaxDfaState state = waitToBuildDfaStates.iterator().next();
      waitToBuildDfaStates.remove(state);
      beBeuildedDfaStates.add(state);

      HashSet<Grammar> charsOfEdges = getCharsOfEdges(state.nfaStates);
      for (Grammar ch : charsOfEdges) {
        TreeSet<SyntaxNfaState> movDfaChEpsilonClosure =
            nfaStateEpsilonClosureGetter.move(state.nfaStates, ch);
        // 空集，跳过
        if (null == movDfaChEpsilonClosure || movDfaChEpsilonClosure.isEmpty()) {
          continue;
        }
        // 非空
        SyntaxDfaState findedDfaState =
            findDfaState(movDfaChEpsilonClosure, waitToBuildDfaStates, beBeuildedDfaStates);
        SyntaxDfaState movDfaChEpsilonClosureDfaState;
        if (null == findedDfaState) { // 原先没有的新状态，压入
          movDfaChEpsilonClosureDfaState = new SyntaxDfaState(movDfaChEpsilonClosure);
          waitToBuildDfaStates.add(movDfaChEpsilonClosureDfaState);
        } else {
          movDfaChEpsilonClosureDfaState = findedDfaState;
        }
        // 设置边
        state.addEdge(ch, movDfaChEpsilonClosureDfaState);
      }
    }
    return dfa;
  }

  private HashSet<Grammar> getCharsOfEdges(TreeSet<SyntaxNfaState> nfaStates) {
    HashSet<Grammar> charsOfEdges = new HashSet<>();
    for (SyntaxNfaState nfaState : nfaStates) {
      Set<Grammar> chs = nfaState.edges.keySet();
      charsOfEdges.addAll(chs);
    }
    charsOfEdges.remove(epsilon);
    return charsOfEdges;
  }

  private SyntaxDfaState findDfaState(
      TreeSet<SyntaxNfaState> nfaEpsilonClosure,
      LinkedHashSet<SyntaxDfaState> dfaStatesContainer1,
      LinkedHashSet<SyntaxDfaState> dfaStatesContainer2) {

    SyntaxDfaState findedDfaState = findDfaState(nfaEpsilonClosure, dfaStatesContainer1);
    if (null != findedDfaState) {
      return findedDfaState;
    }
    findedDfaState = findDfaState(nfaEpsilonClosure, dfaStatesContainer2);
    return findedDfaState;
  }

  private SyntaxDfaState findDfaState(
      TreeSet<SyntaxNfaState> nfaEpsilonClosure, LinkedHashSet<SyntaxDfaState> dfaStates) {
    SyntaxDfaState finded = null;
    for (SyntaxDfaState dfaState : dfaStates) {
      TreeSet<SyntaxNfaState> nfaStatesInDfaState = dfaState.nfaStates;
      if (nfaEpsilonClosure.equals(nfaStatesInDfaState)) {
        finded = dfaState;
        break;
      }
    }
    return finded;
  }
}
