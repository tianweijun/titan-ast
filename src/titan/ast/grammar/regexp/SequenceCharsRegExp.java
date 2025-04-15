package titan.ast.grammar.regexp;

/**
 * .
 *
 * @author tian wei jun
 */
public class SequenceCharsRegExp extends UnitRegExp {

  public String chars = "";

  public SequenceCharsRegExp() {
    super(RegExpType.SEQUENCE_CHARS);
  }

  public SequenceCharsRegExp(String chars, RepeatTimes repMinTimes, RepeatTimes repMaxTimes) {
    this();
    this.chars = chars;
    this.repMinTimes.setTimes(repMinTimes);
    this.repMaxTimes.setTimes(repMaxTimes);
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

    SequenceCharsRegExp that = (SequenceCharsRegExp) o;
    return chars.equals(that.chars);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + chars.hashCode();
    return result;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (char ch : chars.toCharArray()) {
      stringBuilder.append("\\x" + Integer.toHexString(ch).toUpperCase());
    }
    return String.format(
        "'%s'{%s,%s}", stringBuilder.toString(), repMinTimes.toString(), repMaxTimes.toString());
  }
}
