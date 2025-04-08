package titan.ast.grammar.regexp;

import java.util.Arrays;

/**
 * .
 *
 * @author tian wei jun
 */
public class OneCharOptionCharsetRegExp extends RegExp {

  public boolean isNot = false;
  public char[] chars = new char[0];

  public OneCharOptionCharsetRegExp() {
    super(RegExpType.ONE_CHAR_OPTION_CHARSET);
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
    return isNot == that.isNot && Arrays.equals(chars, that.chars);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + Boolean.hashCode(isNot);
    result = 31 * result + Arrays.hashCode(chars);
    return result;
  }
}
