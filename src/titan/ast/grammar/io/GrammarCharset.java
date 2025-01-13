package titan.ast.grammar.io;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import titan.ast.AstRuntimeException;

/**
 * 语法解析器相关的字符集.
 *
 * @author tian wei jun
 */
public class GrammarCharset {
  public static final int EPSILON = 0x100;
  public static final int MAX_CHAR = 0xFF;
  public static final int HEX_LENGTH_OF_TEXT_CHAR = 2;
  public static final int OCTAL_LENGTH_OF_TEXT_CHAR = 3;
  // 文本字符集个数，不包括epsilon
  public static final int COUNT_OF_CHARS = 0xFF + 1;
  // 语法文件字符集是byte(ISO-8859-1)
  public static final Charset CHARSET = StandardCharsets.ISO_8859_1;

  // text表示所要识别文本,其基本组成部分(basicCharset)是byte(ISO-8859-1),
  // 构建TokenNfa过程中（NfaReg2TokenNfaConverter,Reg2TokenNfaConverter）必须要多一个Epsilon，
  // 构建TokenNfa过程中的basicCharset是byte的所有值加上textEpsilon(ISO-8859-1-plus)，
  // 到自动机识别或runtime时，basicCharset是byte(ISO-8859-1)。
  // private final Charset textCharset = StandardCharsets.ISO_8859_1;
  public static final char NEW_LINE = '\n';
  public static final char[] WORD_SPACES = {' ', '\r', '\t'};
  public static final char SPACE = ' ';
  public static final char SINGLE_QUOTE = '\'';
  public static final char VERTICAL_BAR = '|';
  public static final char LEFT_PAREN = '(';
  public static final char RIGHT_PAREN = ')';
  public static final char COMMA = ',';
  public static final char BACK_SLASH = '\\';
  public static final char LEFT_BRACKET = '[';
  public static final char RIGHT_BRACKET = ']';
  public static final char TILDE = '~';
  public static final char MINUS_SIGN = '-';

  private GrammarCharset() {}

  public static boolean isWordSpace(char ch) {
    for (char whitespace : WORD_SPACES) {
      if (ch == whitespace) {
        return true;
      }
    }
    return false;
  }

  public static char[] getDisplayingChars(int tchar) {
    if (tchar == EPSILON) {
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

  public static int getIntByEscapeChar(char ch) {
    int res = -1;
    switch (ch) {
      case '0':
        res = 0;
        break;
      case 'a':
        res = 7;
        break;
      case 'b':
        res = 8;
        break;
      case 't':
        res = 9;
        break;
      case 'n':
        res = 10;
        break;
      case 'v':
        res = 11;
        break;
      case 'f':
        res = 12;
        break;
      case 'r':
        res = 13;
        break;
      case '"':
        res = 34;
        break;
      case '\'':
        res = 39;
        break;
      case '?':
        res = 63;
        break;
      case '\\':
        res = 92;
        break;
      default:
    }
    return res;
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

  public static int getIntByHexDigitChar(char tchar) {
    if (isDigitChar(tchar)) {
      return getIntByDigitChar(tchar);
    }
    if (tchar >= 'a' && tchar <= 'f') {
      return 10 + tchar - 'a';
    }
    if (tchar >= 'A' && tchar <= 'F') {
      return 10 + tchar - 'A';
    }
    return 0;
  }

  public static int getIntByDigitChar(char tchar) {
    return tchar - '0';
  }

  public static int getIntByOctalDigitChar(char tchar) {
    return tchar - '0';
  }

  public static boolean isHexDigitChar(char tchar) {
    return isDigitChar(tchar) || (tchar >= 'a' && tchar <= 'f') || (tchar >= 'A' && tchar <= 'F');
  }

  public static boolean isOctalDigitChar(char tchar) {
    return tchar >= '0' && tchar <= '7';
  }

  public static boolean isDigitChar(char tchar) {
    return tchar >= '0' && tchar <= '9';
  }

  public static boolean isNewLine(char tchar) {
    return tchar == NEW_LINE;
  }

  public static class GetEscapeCharFuncResult {
    public final boolean isOk;
    public final char ch;
    public final int nextIndex;

    private GetEscapeCharFuncResult(boolean isOk, char ch, int nextIndex) {
      this.isOk = isOk;
      this.ch = ch;
      this.nextIndex = nextIndex;
    }

    public static GetEscapeCharFuncResult ok(char ch, int nextIndex) {
      return new GetEscapeCharFuncResult(true, ch, nextIndex);
    }

    public static GetEscapeCharFuncResult fail() {
      return FAIL;
    }

    private static final GetEscapeCharFuncResult FAIL =
        new GetEscapeCharFuncResult(false, '\0', -1);
  }

  public static GetEscapeCharFuncResult getEscapeChar(char[] text, int indexOfText) {
    if (indexOfText >= text.length || indexOfText < 0) {
      return GetEscapeCharFuncResult.fail();
    }
    GetEscapeCharFuncResult hexDigitEscapeChar = getHexDigitEscapeChar(text, indexOfText);
    if (hexDigitEscapeChar.isOk) {
      return hexDigitEscapeChar;
    }
    int intEscapeChar = getIntByEscapeChar(text[indexOfText]);
    if (intEscapeChar >= 0) {
      return GetEscapeCharFuncResult.ok((char) (0xFF & intEscapeChar), indexOfText + 1);
    }
    return getOctalDigitEscapeChar(text, indexOfText);
  }

  private static GetEscapeCharFuncResult getHexDigitEscapeChar(char[] text, int indexOfText) {
    // \xhh \Xhh
    if ('x' == text[indexOfText] || 'X' == text[indexOfText]) {
      char[] hexNumberChars = new char[HEX_LENGTH_OF_TEXT_CHAR];
      int indexOfHexNumber = 0;
      int indexOfHexCharText = indexOfText + 1;
      while (indexOfHexCharText < text.length && indexOfHexNumber < hexNumberChars.length) {
        char hexChar = text[indexOfHexCharText++];
        if (isHexDigitChar(hexChar)) {
          hexNumberChars[indexOfHexNumber++] = hexChar;
        } else {
          break;
        }
      }
      int sizeOfHexNumbers = indexOfHexNumber; // indexOfHexNumber==size of hexNumbers
      if (sizeOfHexNumbers > 0) {
        int vchar = 0;
        int multiples = 1;
        indexOfHexNumber = indexOfHexNumber - 1;
        while (indexOfHexNumber >= 0) {
          vchar += getIntByHexDigitChar(hexNumberChars[indexOfHexNumber]) * multiples;
          multiples *= 16;
          --indexOfHexNumber;
        }
        if (vchar <= MAX_CHAR) {
          return GetEscapeCharFuncResult.ok(
              (char) (vchar & 0xFF), indexOfText + sizeOfHexNumbers + 1); // xhh
        }
      }
    }
    return GetEscapeCharFuncResult.fail();
  }

  private static GetEscapeCharFuncResult getOctalDigitEscapeChar(char[] text, int indexOfText) {
    // \ddd
    if (isOctalDigitChar(text[indexOfText])) {
      char[] octalNumberChars = new char[OCTAL_LENGTH_OF_TEXT_CHAR];
      int indexOfOctalNumber = 0;
      int indexOfOctalCharText = indexOfText;
      while (indexOfOctalCharText < text.length && indexOfOctalNumber < octalNumberChars.length) {
        char octalChar = text[indexOfOctalCharText++];
        if (isOctalDigitChar(octalChar)) {
          octalNumberChars[indexOfOctalNumber++] = octalChar;
        } else {
          break;
        }
      }
      int sizeOfOctalNumbers = indexOfOctalNumber; // indexOfHexNumber==size of hexNumbers
      if (sizeOfOctalNumbers > 0) {
        int vchar = 0;
        int multiples = 1;
        indexOfOctalNumber = indexOfOctalNumber - 1;
        while (indexOfOctalNumber >= 0) {
          vchar += getIntByOctalDigitChar(octalNumberChars[indexOfOctalNumber]) * multiples;
          multiples *= 8;
          --indexOfOctalNumber;
        }
        if (vchar <= MAX_CHAR) {
          return GetEscapeCharFuncResult.ok((char) vchar, indexOfText + sizeOfOctalNumbers);
        }
      }
    }
    return GetEscapeCharFuncResult.fail();
  }

  /**
   * \\已经被识别，\\后的text必须要能正确表示一个转义字符，将转义字符转为其对应的真正字符，设置到stringBuilder容器.
   *
   * @param text 文本
   * @param indexOfText 文本的 索引
   * @return 处理过的文本的下一个索引,如果和@param indexOfText一样则转义字符不合法
   * @params tringBuilder 接收转义字符其对应的真正字符的 容器
   */
  private static int formatEscapeChar2CharAndSet(
      char[] text, int indexOfText, StringBuilder stringBuilder) {
    GetEscapeCharFuncResult escapeChar = getEscapeChar(text, indexOfText);
    if (!escapeChar.isOk) {
      return indexOfText;
    }
    stringBuilder.append(escapeChar.ch);
    return escapeChar.nextIndex;
  }

  /**
   * 将转义字符设置成其对应的字符.
   *
   * @param str str
   * @return return null if parse error
   */
  public static String formatEscapeChar2Char(String str, String errorTipMsgPrefix) {
    StringBuilder stringBuilder = new StringBuilder();
    char[] text = str.toCharArray();
    int indexOfText = 0;
    while (indexOfText < text.length) {
      char tchar = text[indexOfText];
      if (GrammarCharset.BACK_SLASH == tchar) { // 转义字符 基本单元正则内容中\后面必须跟一个能正确表示转义的字符
        ++indexOfText;
        int newIndexByEscapeChar = formatEscapeChar2CharAndSet(text, indexOfText, stringBuilder);
        if (newIndexByEscapeChar <= indexOfText) {
          throw new AstRuntimeException(
              String.format(
                  "%s:format escape char error,error near '%s' in %s",
                  errorTipMsgPrefix, new String(text, 0, indexOfText), str));
        }
        indexOfText = newIndexByEscapeChar;
      } else { // 常规字符
        stringBuilder.append(tchar);
        ++indexOfText;
      }
    }
    return stringBuilder.toString();
  }
}
