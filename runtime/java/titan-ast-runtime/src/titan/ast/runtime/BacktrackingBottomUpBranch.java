package titan.ast.runtime;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

/**
 * 自顶向上归约分支.
 *
 * @author tian wei jun
 */
public class BacktrackingBottomUpBranch
    implements Cloneable, Comparable<BacktrackingBottomUpBranch> {

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
      stringBuilder
          .append("-")
          .append(reducingSymbol.astOfCurrentDfaState.grammar.name)
          .append(" ");
    }
    return stringBuilder.toString();
  }

  @Override
  public int compareTo(BacktrackingBottomUpBranch that) {
    LinkedList<ReducingSymbol> thatReducingSymbols = that.reducingSymbols;
    // endIndexOfToken
    int thisEndIndexOfToken = this.reducingSymbols.getLast().endIndexOfToken;
    int thatEndIndexOfToken = thatReducingSymbols.getLast().endIndexOfToken;
    if (thisEndIndexOfToken != thatEndIndexOfToken) {
      return thisEndIndexOfToken - thatEndIndexOfToken;
    }
    // reducingSymbols
    if (this.reducingSymbols.size() != thatReducingSymbols.size()) {
      return this.reducingSymbols.size() - thatReducingSymbols.size();
    }
    Iterator<ReducingSymbol> thisReducingSymbolsIt = this.reducingSymbols.iterator();
    Iterator<ReducingSymbol> thatReducingSymbolsIt = thatReducingSymbols.iterator();
    while (thisReducingSymbolsIt.hasNext()) {
      ReducingSymbol thisReducingSymbol = thisReducingSymbolsIt.next();
      ReducingSymbol thatReducingSymbol = thatReducingSymbolsIt.next();
      int reducingSymbolCompare = thisReducingSymbol.compareTo(thatReducingSymbol);
      if (0 != reducingSymbolCompare) {
        return reducingSymbolCompare;
      }
    }
    return 0;
  }
}
