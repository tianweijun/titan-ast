package titan.ast.runtime;

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

  public ReducingSymbol cloneForAstAutomata() {
    ReducingSymbol reducingSymbol = new ReducingSymbol();
    reducingSymbol.currentDfaState = this.currentDfaState;
    reducingSymbol.endIndexOfToken = this.endIndexOfToken;
    reducingSymbol.astOfCurrentDfaState = this.astOfCurrentDfaState.cloneForAstAutomata();
    return reducingSymbol;
  }

  @Override
  public int compareTo(ReducingSymbol that) {
    int compare = endIndexOfToken - that.endIndexOfToken;
    if (0 != compare) {
      return compare;
    }

    compare = currentDfaState.index - that.currentDfaState.index;
    if (0 != compare) {
      return compare;
    }

    return astOfCurrentDfaState.grammar.index - that.astOfCurrentDfaState.grammar.index;
  }

  @Override
  public String toString() {
    return null == astOfCurrentDfaState ? "" : astOfCurrentDfaState.toString();
  }
}
