package titan.ast.grammar.syntax;

import java.util.LinkedList;
import java.util.Objects;
import titan.ast.runtime.AstRuntimeException;

/**
 * 自顶向上归约分支.
 *
 * @author tian wei jun
 */
public class BacktrackingBottomUpBranch implements Cloneable {

  LinkedList<ReducingSymbol> reducingSymbols = new LinkedList<>();

  public BacktrackingBottomUpBranch clone() {
    BacktrackingBottomUpBranch copy = null;
    try {
      copy = (BacktrackingBottomUpBranch) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AstRuntimeException(e);
    }
    copy.reducingSymbols = new LinkedList<>();
    for (ReducingSymbol reducingSymbol : this.reducingSymbols) {
      copy.reducingSymbols.addLast(reducingSymbol.clone());
    }
    return copy;
  }

  // for BacktrackingBottomUpAstAutomata.triedBottomUpBranchs(set)
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BacktrackingBottomUpBranch that = (BacktrackingBottomUpBranch) o;
    return reducingSymbols.equals(that.reducingSymbols);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reducingSymbols);
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (ReducingSymbol reducingSymbol : reducingSymbols) {
      if (null != reducingSymbol.reducedGrammar) {
        stringBuilder.append("-").append(reducingSymbol.reducedGrammar.name);
      }
    }
    return stringBuilder.toString();
  }
}
