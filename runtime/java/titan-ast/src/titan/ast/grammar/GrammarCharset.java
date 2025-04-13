package titan.ast.grammar;

/**
 * .
 *
 * @author tian wei jun
 */
public class GrammarCharset {

  public static final int HEX_LENGTH_OF_TEXT_CHAR = 2;
  public static final int MAX_CHAR = 0xFF;
  public static final String KW_DERIVE = "derive";

  private GrammarCharset() {
  }


  /**
   * normal chars.
   *
   * @return dfa所有可能的字符
   */
  public static int[] getChars() {
    int[] chars = new int[MAX_CHAR + 1];
    // normal chars
    for (int indexOfChar = 0; indexOfChar <= MAX_CHAR; indexOfChar++) {
      chars[indexOfChar] = indexOfChar;
    }
    return chars;
  }
}
