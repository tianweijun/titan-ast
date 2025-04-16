package titan.ast.grammar.regexp;

import titan.ast.grammar.Grammar;

/**
 * .
 *
 * @author tian wei jun
 */
public class GrammarRegExp extends UnitRegExp {

  public final String grammarName;

  public Grammar grammar;

  public GrammarRegExp(String grammarName) {
    super(RegExpType.GRAMMAR);
    this.grammarName = grammarName;
  }

  public GrammarRegExp(RepeatTimes repMinTimes, RepeatTimes repMaxTimes, String grammarName) {
    this(grammarName);
    setRepeatTimes(repMinTimes, repMaxTimes);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    GrammarRegExp that = (GrammarRegExp) o;
    return grammarName.equals(that.grammarName);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + grammarName.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return String.format("%s{%s,%s}", grammarName, repMinTimes.toString(), repMaxTimes.toString());
  }
}
