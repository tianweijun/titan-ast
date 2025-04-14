package titan.ast.fa.token;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 将终结符的nfa转为dfa.
 *
 * @author tian wei jun
 */
public class TokenNfa2DfaConverter {

  private final TokenNfa nfa;
  private TokenDfa dfa;
  private final Integer epsilon;

  TokenNfa2DfaConverter(TokenNfa nfa) {
    epsilon = TokenNfa.EPSILON;
    this.nfa = nfa;
  }

  public TokenDfa convert() {
    createDfa();
    return dfa;
  }

  /**
   * 子集法 设字母表只包含两个a和b，我们构造一张计算状态集的转换表: 1.计算所有状态的闭包 2.置第1行第1列为ε-closure({X})求出这一列的Ia,Ib；
   * 3.检查这两个Ia,Ib，看它们是否已在表中的第一列中出现，未曾出现的填入后面的空行的第1列上， 4.求出每行第2，3列上的集合...
   * 重复上述过程，直到所有第2，3列子集全部出现在第一列为止。 5.设置dfa边和终态的权重， 比较终态和边的权重，参照最大权重终边，若大于等于则是收敛字符集，若小于则转移字符集。
   */
  private void createDfa() {
    TokenNfaStateEpsilonClosureGetter nfaStateEpsilonClosureGetter =
        new TokenNfaStateEpsilonClosureGetter();
    TreeSet<TokenNfaState> startNfaStates = nfaStateEpsilonClosureGetter.get(nfa.start);

    dfa = new TokenDfa();
    TokenDfaState startDfaState = new TokenDfaState(startNfaStates);
    dfa.start = startDfaState;

    LinkedHashSet<TokenDfaState> waitToBuildDfaStates = new LinkedHashSet<>();
    LinkedHashSet<TokenDfaState> beBeuildedDfaStates = new LinkedHashSet<>();

    waitToBuildDfaStates.add(startDfaState);
    while (!waitToBuildDfaStates.isEmpty()) {
      TokenDfaState beBuildedDfaState = waitToBuildDfaStates.iterator().next();
      waitToBuildDfaStates.remove(beBuildedDfaState);
      beBeuildedDfaStates.add(beBuildedDfaState);

      HashSet<Integer> charsOfEdges = getCharsOfEdges(beBuildedDfaState.nfaStates);
      for (Integer ch : charsOfEdges) {
        TreeSet<TokenNfaState> movDfaChEpsilonClosure =
            nfaStateEpsilonClosureGetter.move(beBuildedDfaState.nfaStates, ch);
        // 空集，跳过
        if (null == movDfaChEpsilonClosure || movDfaChEpsilonClosure.isEmpty()) {
          continue;
        }
        // 非空
        TokenDfaState findedDfaState =
            findDfaState(movDfaChEpsilonClosure, waitToBuildDfaStates, beBeuildedDfaStates);
        TokenDfaState movDfaChEpsilonClosureDfaState;
        if (null == findedDfaState) { // 原先没有的新状态，压入
          movDfaChEpsilonClosureDfaState = new TokenDfaState(movDfaChEpsilonClosure);
          waitToBuildDfaStates.add(movDfaChEpsilonClosureDfaState);
        } else {
          movDfaChEpsilonClosureDfaState = findedDfaState;
        }
        // 设置边
        beBuildedDfaState.addEdge(ch, movDfaChEpsilonClosureDfaState);
      }
    }
  }

  private HashSet<Integer> getCharsOfEdges(TreeSet<TokenNfaState> nfaStates) {
    HashSet<Integer> charsOfEdges = new HashSet<>();
    for (TokenNfaState nfaState : nfaStates) {
      Set<Integer> chs = nfaState.edges.keySet();
      charsOfEdges.addAll(chs);
    }
    charsOfEdges.remove(epsilon);
    return charsOfEdges;
  }

  private TokenDfaState findDfaState(
      TreeSet<TokenNfaState> nfaEpsilonClosure,
      LinkedHashSet<TokenDfaState> dfaStatesContainer1,
      LinkedHashSet<TokenDfaState> dfaStatesContainer2) {

    TokenDfaState findedTokenDfaState = findDfaState(nfaEpsilonClosure, dfaStatesContainer1);
    if (null != findedTokenDfaState) {
      return findedTokenDfaState;
    }
    findedTokenDfaState = findDfaState(nfaEpsilonClosure, dfaStatesContainer2);
    return findedTokenDfaState;
  }

  private TokenDfaState findDfaState(
      TreeSet<TokenNfaState> nfaEpsilonClosure, LinkedHashSet<TokenDfaState> dfaStates) {
    TokenDfaState finded = null;
    for (TokenDfaState dfaState : dfaStates) {
      TreeSet<TokenNfaState> nfaStatesInDfaState = dfaState.nfaStates;
      if (nfaEpsilonClosure.equals(nfaStatesInDfaState)) {
        finded = dfaState;
        break;
      }
    }
    return finded;
  }
}
