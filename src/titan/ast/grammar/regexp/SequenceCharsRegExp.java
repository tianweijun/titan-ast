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

  public SequenceCharsRegExp(String chars) {
    this();
    this.chars = chars;
  }

  public SequenceCharsRegExp(RepeatTimes repMinTimes, RepeatTimes repMaxTimes, String chars) {
    this();
    this.chars = chars;
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
