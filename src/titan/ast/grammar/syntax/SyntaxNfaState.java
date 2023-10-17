package titan.ast.grammar.syntax;

import java.util.HashSet;
import java.util.Set;
import titan.ast.AstContext;
import titan.ast.grammar.FaStateType;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.NfaState;

/**
 * 项目集 等价于 语法的产生式 对应的 非确定有限状态自动机的 状态.
 *
 * @author tian wei jun
 */
public class SyntaxNfaState extends NfaState<Grammar, SyntaxNfaState> {

  public ProductionRule productionRule = null;

  public SyntaxNfaState(ProductionRule productionRule) {
    this.productionRule = productionRule;
    id = AstContext.get().resourceGenerator.generateSyntaxNfaStateId();
  }

  public void addEdge(Grammar grammar, SyntaxNfaState toState) {
    Set<SyntaxNfaState> toStates = edges.get(grammar);
    if (null == toStates) {
      toStates = new HashSet<>();
      edges.put(grammar, toStates);
    }
    toStates.add(toState);
  }

  public void addEdge(Grammar grammar, Set<SyntaxNfaState> toStates) {
    Set<SyntaxNfaState> originStates = edges.get(grammar);
    if (null == originStates) {
      originStates = new HashSet<>();
      edges.put(grammar, originStates);
    }
    originStates.addAll(toStates);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SyntaxNfaState that = (SyntaxNfaState) o;

    return id == that.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("[");
    if (FaStateType.isOpeningTag(type)) {
      stringBuilder.append("-open-");
    }
    if (FaStateType.isClosingTag(type)) {
      stringBuilder.append("-close-");
    }
    stringBuilder.append("]");
    if (null != productionRule) {
      stringBuilder.append(productionRule.toString());
    } else {
      stringBuilder.append("null");
    }
    return stringBuilder.toString();
  }
}
