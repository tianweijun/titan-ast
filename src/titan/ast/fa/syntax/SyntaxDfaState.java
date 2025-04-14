package titan.ast.fa.syntax;

import java.util.TreeSet;
import titan.ast.AstContext;
import titan.ast.fa.DfaState;
import titan.ast.fa.FaStateType;
import titan.ast.grammar.Grammar;

/**
 * 语法对应的 确定有限状态自动机 的状态.
 *
 * @author tian wei jun
 */
public class SyntaxDfaState extends DfaState<Grammar, SyntaxDfaState> {

  public TreeSet<ProductionRule> closingProductionRules = new TreeSet<>();
  // 状态内容是nfa的集合
  public TreeSet<SyntaxNfaState> nfaStates = null;

  public SyntaxDfaState() {
    super(AstContext.get().idGenerator.generateSyntaxDfaStateId());
  }

  public SyntaxDfaState(TreeSet<SyntaxNfaState> nfaStates) {
    this();
    this.nfaStates = nfaStates;
  }

  public void addEdge(Grammar ch, SyntaxDfaState dest) {
    edges.put(ch, dest);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("(");
    if (FaStateType.isClosingTag(type)) {
      for (ProductionRule closingProductionRule : closingProductionRules) {
        stringBuilder.append("[").append(closingProductionRule.toString()).append("]");
      }
    } else {
      stringBuilder.append(id);
    }
    stringBuilder.append(")");
    return stringBuilder.toString();
  }
}
