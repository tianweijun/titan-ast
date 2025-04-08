package titan.ast.grammar.regexp;

import java.util.Arrays;

/**
 * .
 *
 * @author tian wei jun
 */
public class SequenceCharsRegExp extends RegExp {

  public char[] chars = new char[0];

  public SequenceCharsRegExp() {
    super(RegExpType.SEQUENCE_CHARS);
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
    return Arrays.equals(chars, that.chars);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + Arrays.hashCode(chars);
    return result;
  }
}
