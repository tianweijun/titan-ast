package titan.ast.grammar.syntax;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;

/**
 * 获取非终结符状态的epsilonnClosure集.
 *
 * @author tian wei jun
 */
public class SyntaxNfaStateEpsilonClosureGetter {

  private final Grammar epsilon;

  SyntaxNfaStateEpsilonClosureGetter() {
    epsilon = AstContext.get().languageGrammar.epsilon;
  }

  public TreeSet<SyntaxNfaState> get(SyntaxNfaState state) {
    return buildEpsilonClosure(state);
  }

  private TreeSet<SyntaxNfaState> buildEpsilonClosure(SyntaxNfaState state) {
    TreeSet<SyntaxNfaState> states = new TreeSet<>();
    states.add(state);
    return buildEpsilonClosure(states);
  }

  private TreeSet<SyntaxNfaState> buildEpsilonClosure(TreeSet<SyntaxNfaState> states) {
    TreeSet<SyntaxNfaState> beBeuildedEpsilonClosure = new TreeSet<>();
    HashSet<SyntaxNfaState> waitToBuildEpsilonClosure = new HashSet<>();
    waitToBuildEpsilonClosure.addAll(states);

    while (!waitToBuildEpsilonClosure.isEmpty()) {
      Iterator<SyntaxNfaState> waitToBuildEpsilonClosureIt = waitToBuildEpsilonClosure.iterator();
      SyntaxNfaState beBuildedState = waitToBuildEpsilonClosureIt.next();
      waitToBuildEpsilonClosure.remove(beBuildedState);
      beBeuildedEpsilonClosure.add(beBuildedState);

      Set<SyntaxNfaState> beBuildedStateEpsilonToStates = beBuildedState.edges.get(epsilon);
      if (null != beBuildedStateEpsilonToStates) {
        for (SyntaxNfaState beBuildedStateEpsilonToState : beBuildedStateEpsilonToStates) {
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
  public TreeSet<SyntaxNfaState> move(TreeSet<SyntaxNfaState> nfaStates, Grammar ch) {
    TreeSet<SyntaxNfaState> toNfaStates = new TreeSet<>();
    for (SyntaxNfaState nfaState : nfaStates) {
      Set<SyntaxNfaState> nfaStateChToStates = nfaState.edges.get(ch);
      if (null != nfaStateChToStates && !nfaStateChToStates.isEmpty()) {
        toNfaStates.addAll(nfaStateChToStates);
      }
    }
    return buildEpsilonClosure(toNfaStates);
  }
}
