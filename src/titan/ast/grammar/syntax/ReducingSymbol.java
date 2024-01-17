package titan.ast.grammar.syntax;

import java.util.Objects;
import titan.ast.runtime.AstRuntimeException;
import titan.ast.target.AutomataTmpAst;

/**
 * 分析栈存放的文法符号.
 *
 * @author tian wei jun
 */
public class ReducingSymbol implements Cloneable, Comparable<ReducingSymbol> {
  // ast
  public AutomataTmpAst astOfCurrentDfaState = null;
  // 状态
  public SyntaxDfaState currentDfaState = null;
  // token流中的位置
  public int endIndexOfToken = -1;

  public ReducingSymbol() {}

  public ReducingSymbol clone() {
    ReducingSymbol reducingSymbol = null;
    try {
      reducingSymbol = (ReducingSymbol) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AstRuntimeException(e);
    }
    reducingSymbol.currentDfaState = this.currentDfaState;
    reducingSymbol.endIndexOfToken = this.endIndexOfToken;
    reducingSymbol.astOfCurrentDfaState = this.astOfCurrentDfaState.diyClone();
    return reducingSymbol;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReducingSymbol that = (ReducingSymbol) o;
    return endIndexOfToken == that.endIndexOfToken
        && astOfCurrentDfaState.equals(that.astOfCurrentDfaState)
        && currentDfaState.equals(that.currentDfaState);
  }

  @Override
  public int hashCode() {
    return Objects.hash(astOfCurrentDfaState, currentDfaState, endIndexOfToken);
  }

  @Override
  public String toString() {
    return null == astOfCurrentDfaState ? "" : astOfCurrentDfaState.toString();
  }

  @Override
  public int compareTo(ReducingSymbol that) {
    int compare = astOfCurrentDfaState.grammar.compareTo(that.astOfCurrentDfaState.grammar);
    if (0 != compare) {
      return compare;
    }
    return currentDfaState.compareTo(that.currentDfaState);
  }
}
