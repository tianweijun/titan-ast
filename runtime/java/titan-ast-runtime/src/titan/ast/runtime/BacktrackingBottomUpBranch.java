package titan.ast.runtime;

import java.util.LinkedList;
import java.util.ListIterator;

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
    ListIterator<ReducingSymbol> thisReducingSymbolsIt =
        this.reducingSymbols.listIterator(this.reducingSymbols.size());
    ListIterator<ReducingSymbol> thatReducingSymbolsIt =
        thatReducingSymbols.listIterator(thatReducingSymbols.size());
    while (thisReducingSymbolsIt.hasPrevious()) {
      ReducingSymbol thisReducingSymbol = thisReducingSymbolsIt.previous();
      ReducingSymbol thatReducingSymbol = thatReducingSymbolsIt.previous();
      int reducingSymbolCompare = thisReducingSymbol.compareTo(thatReducingSymbol);
      if (0 != reducingSymbolCompare) {
        return reducingSymbolCompare;
      }
    }
    return 0;
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
}
