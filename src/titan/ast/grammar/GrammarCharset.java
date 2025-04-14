package titan.ast.grammar;

import titan.ast.fa.token.TokenNfa;

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

  public static char[] getDisplayingChars(int tchar) {
    if (tchar == TokenNfa.EPSILON) {
      return new char[] {'\\', 'e'};
    }
    int postfixEscapeChar = -1;
    switch (tchar) {
      case 0:
        postfixEscapeChar = '0';
        break;
      case 7:
        postfixEscapeChar = 'a';
        break;
      case 8:
        postfixEscapeChar = 'b';
        break;
      case 9:
        postfixEscapeChar = 't';
        break;
      case 10:
        postfixEscapeChar = 'n';
        break;
      case 11:
        postfixEscapeChar = 'v';
        break;
      case 12:
        postfixEscapeChar = 'f';
        break;
      case 13:
        postfixEscapeChar = 'r';
        break;
      case 34:
        postfixEscapeChar = '"';
        break;
      case 39:
        postfixEscapeChar = '\'';
        break;
      case 63:
        postfixEscapeChar = '?';
        break;
      case 92:
        postfixEscapeChar = '\\';
        break;
      default:
    }
    if (postfixEscapeChar == -1) {
      return new char[] {(char) tchar};
    }
    return new char[] {'\\', (char) postfixEscapeChar};
  }

}
