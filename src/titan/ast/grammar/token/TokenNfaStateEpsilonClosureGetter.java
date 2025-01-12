package titan.ast.grammar.token;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import titan.ast.grammar.io.GrammarCharset;

/**
 * 获取状态的epsilonnClosure集.
 *
 * @author tian wei jun
 */
public class TokenNfaStateEpsilonClosureGetter {

  private final Integer epsilon;

  TokenNfaStateEpsilonClosureGetter() {
    epsilon = GrammarCharset.EPSILON;
  }

  public TreeSet<TokenNfaState> get(TokenNfaState state) {
    return buildEpsilonClosure(state);
  }

  private TreeSet<TokenNfaState> buildEpsilonClosure(TokenNfaState state) {
    TreeSet<TokenNfaState> states = new TreeSet<>();
    states.add(state);
    return buildEpsilonClosure(states);
  }

  private TreeSet<TokenNfaState> buildEpsilonClosure(TreeSet<TokenNfaState> states) {
    TreeSet<TokenNfaState> beBeuildedEpsilonClosure = new TreeSet<>();
    HashSet<TokenNfaState> waitToBuildEpsilonClosure = new HashSet<>();
    waitToBuildEpsilonClosure.addAll(states);

    while (!waitToBuildEpsilonClosure.isEmpty()) {
      Iterator<TokenNfaState> waitToBuildEpsilonClosureIt = waitToBuildEpsilonClosure.iterator();
      TokenNfaState beBuildedState = waitToBuildEpsilonClosureIt.next();
      waitToBuildEpsilonClosure.remove(beBuildedState);
      beBeuildedEpsilonClosure.add(beBuildedState);

      Set<TokenNfaState> beBuildedStateEpsilonToStates = beBuildedState.edges.get(epsilon);
      if (null != beBuildedStateEpsilonToStates) {
        for (TokenNfaState beBuildedStateEpsilonToState : beBuildedStateEpsilonToStates) {
          // 新状态，没被设置
          if (!beBeuildedEpsilonClosure.contains(beBuildedStateEpsilonToState)) {
            waitToBuildEpsilonClosure.add(beBuildedStateEpsilonToState);
          }
        }
      }
    }
    return beBeuildedEpsilonClosure;
  }

  /**
   * 获取状态的move-epsilonnClosure集.
   *
   * @param nfaStates 一开始的集合
   * @param ch 将要吞入的字符
   * @return nfaStates状态们的move-epsilonnClosure集
   */
  public TreeSet<TokenNfaState> move(TreeSet<TokenNfaState> nfaStates, int ch) {
    TreeSet<TokenNfaState> toNfaStates = new TreeSet<>();
    for (TokenNfaState nfaState : nfaStates) {
      Set<TokenNfaState> nfaStateChToStates = nfaState.edges.get(ch);
      if (null != nfaStateChToStates && !nfaStateChToStates.isEmpty()) {
        toNfaStates.addAll(nfaStateChToStates);
      }
    }
    return buildEpsilonClosure(toNfaStates);
  }
}
