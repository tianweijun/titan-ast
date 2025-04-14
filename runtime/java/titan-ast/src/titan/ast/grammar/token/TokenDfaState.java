package titan.ast.grammar.token;

import java.util.TreeSet;
import titan.ast.AstContext;
import titan.ast.grammar.DfaState;
import titan.ast.grammar.FaStateType;
import titan.ast.grammar.Grammar;

/**
 * token的 确定有限状态自动机的 状态.
 *
 * @author tian wei jun
 */
public class TokenDfaState extends DfaState<Integer, TokenDfaState> {

  // token语法名字
  public Grammar terminal;
  // 每一个不同的terminal的权重必不一样，TokenDfaState的权重相同则terminal必定相同
  public int weight = 0;

  // 状态内容是nfa的集合
  public TreeSet<TokenNfaState> nfaStates;

  public TokenDfaState() {
    super();
    this.id = AstContext.get().resourceGenerator.generateTokenDfaStateId();
  }

  TokenDfaState(TreeSet<TokenNfaState> tokenNfaStates) {
    this();
    this.nfaStates = tokenNfaStates;
  }

  public void addEdge(int ch, TokenDfaState dest) {
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
    String info = "";
    if (FaStateType.isClosingTag(type)) {
      info = terminal.name;
    } else {
      info = "(" + id + ")";
    }
    return info;
  }
}
