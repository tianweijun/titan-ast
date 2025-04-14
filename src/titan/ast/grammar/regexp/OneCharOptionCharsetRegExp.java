package titan.ast.grammar.regexp;

import java.util.Arrays;

/**
 * .
 *
 * @author tian wei jun
 */
public class OneCharOptionCharsetRegExp extends UnitRegExp {

  public char[] chars = new char[0];

  public OneCharOptionCharsetRegExp() {
    super(RegExpType.ONE_CHAR_OPTION_CHARSET);
  }

  public OneCharOptionCharsetRegExp(char[] chars, RepeatTimes repMinTimes, RepeatTimes repMaxTimes) {
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

    OneCharOptionCharsetRegExp that = (OneCharOptionCharsetRegExp) o;
    return Arrays.equals(chars, that.chars);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + Arrays.hashCode(chars);
    return result;
  }
}
