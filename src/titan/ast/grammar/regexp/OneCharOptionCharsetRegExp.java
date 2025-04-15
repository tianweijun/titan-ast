package titan.ast.grammar.regexp;

import java.util.LinkedList;

/**
 * .
 *
 * @author tian wei jun
 */
public class OneCharOptionCharsetRegExp extends UnitRegExp {

  public LinkedList<OneCharOptionCharsetRegExpChar> chars;

  public OneCharOptionCharsetRegExp() {
    super(RegExpType.ONE_CHAR_OPTION_CHARSET);
  }

  public OneCharOptionCharsetRegExp(
      LinkedList<OneCharOptionCharsetRegExpChar> chars,
      RepeatTimes repMinTimes,
      RepeatTimes repMaxTimes) {
    this();
    this.chars = chars;
    this.repMinTimes.setTimes(repMinTimes);
    this.repMaxTimes.setTimes(repMaxTimes);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    OneCharOptionCharsetRegExp that = (OneCharOptionCharsetRegExp) o;
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
    for (OneCharOptionCharsetRegExpChar ch : chars) {
      stringBuilder.append(ch.toString()).append("  ");
    }
    if (!stringBuilder.isEmpty()) {
      stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
    }
    return String.format(
        "[%s]{%s,%s}", stringBuilder.toString(), repMinTimes.toString(), repMaxTimes.toString());
  }

  public static class OneCharOptionCharsetRegExpChar {
    public final int min;
    public final int max;

    public OneCharOptionCharsetRegExpChar(int min, int max) {
      this.min = min;
      this.max = max;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      OneCharOptionCharsetRegExpChar that = (OneCharOptionCharsetRegExpChar) o;
      return min == that.min && max == that.max;
    }

    @Override
    public int hashCode() {
      int result = min;
      result = 31 * result + max;
      return result;
    }

    @Override
    public String toString() {
      return "["
          + Integer.toHexString(min).toUpperCase()
          + "-"
          + Integer.toHexString(max).toUpperCase()
          + "]";
    }
  }
}
