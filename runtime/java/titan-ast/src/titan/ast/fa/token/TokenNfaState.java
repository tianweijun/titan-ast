package titan.ast.fa.token;

import java.util.HashSet;
import java.util.Set;
import titan.ast.AstContext;
import titan.ast.fa.FaStateType;
import titan.ast.fa.NfaState;
import titan.ast.grammar.Grammar;

/**
 * 非确定有限状态自动机的 状态.
 *
 * @author tian wei jun
 */
public class TokenNfaState extends NfaState<Integer, TokenNfaState> {

  public Grammar terminal;

  /**
   * 初始化id、type、edges.
   */
  public TokenNfaState() {
    super(AstContext.get().idGenerator.generateTokenNfaStateId());
  }

  /**
   * 按照 字符->新状态 构造边.
   *
   * @param ch   状态转换的字符
   * @param dest 吞入字符后所到达的新状态
   */
  public void addEdge(Integer ch, TokenNfaState dest) {
    Set<TokenNfaState> chToStates = edges.computeIfAbsent(ch, k -> new HashSet<>());
    chToStates.add(dest);
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
