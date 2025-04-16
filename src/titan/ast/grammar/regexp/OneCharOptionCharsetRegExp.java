package titan.ast.grammar.regexp;

import java.util.*;

/**
 * .
 *
 * @author tian wei jun
 */
public class OneCharOptionCharsetRegExp extends UnitRegExp {

  public List<OptionChar> chars;

  public OneCharOptionCharsetRegExp() {
    super(RegExpType.ONE_CHAR_OPTION_CHARSET);
  }

  public OneCharOptionCharsetRegExp(
      RepeatTimes repMinTimes, RepeatTimes repMaxTimes, List<OptionChar> chars) {
    this();
    setRepeatTimes(repMinTimes, repMaxTimes);
    this.chars = chars;
  }

  public OneCharOptionCharsetRegExp(OptionChar... optionChars) {
    this();
    chars = new ArrayList<>(optionChars.length);
    Collections.addAll(chars, optionChars);
  }

  public OneCharOptionCharsetRegExp(
      RepeatTimes repMinTimes, RepeatTimes repMaxTimes, OptionChar... optionChars) {
    this();
    setRepeatTimes(repMinTimes, repMaxTimes);
    chars = new ArrayList<>(optionChars.length);
    Collections.addAll(chars, optionChars);
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
    for (OptionChar ch : chars) {
      stringBuilder.append(ch.toString()).append("  ");
    }
    if (!stringBuilder.isEmpty()) {
      stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
    }
    return String.format(
        "[%s]{%s,%s}", stringBuilder.toString(), repMinTimes.toString(), repMaxTimes.toString());
  }

  public static class OptionChar {
    public final int min;
    public final int max;

    public OptionChar(int min) {
      this.min = min;
      this.max = min;
    }

    public OptionChar(int min, int max) {
      this.min = min;
      this.max = max;
    }

    public OptionChar(char min) {
      this.min = min & 0xFF;
      this.max = min & 0xFF;
    }

    public OptionChar(char min, char max) {
      this.min = min & 0xFF;
      this.max = max & 0xFF;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      OptionChar that = (OptionChar) o;
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
