package titan.ast.grammar.io;

import java.util.ArrayList;
import titan.ast.runtime.AstRuntimeException;

/**
 * 语法解析器相关的字符集.
 *
 * @author tian wei jun
 */
public class GrammarCharset {
  public char grammarSpace = 32;
  public int grammarNewline = 10;
  public char[] grammarWhitespaces = {32, 9, 13};
  public int textEpsilon = 0x100;
  public int textMax = 0xFF;
  public int hexLengthOfTextChar = 2;
  public int octalLengthOfTextChar = 3;
  public int countOfChars = 0xFF + 1;
  // 语法文件字符集是byte(ISO-8859-1)
  private String grammarCharset = "ISO-8859-1";
  // text表示所要识别文本,其基本组成部分(basicCharset)是byte(ISO-8859-1),
  // 构建TokenNfa过程中（NfaReg2TokenNfaConverter,Reg2TokenNfaConverter）必须要多一个Epsilon，
  // 构建TokenNfa过程中的basicCharset是byte的所有值加上textEpsilon(ISO-8859-1-plus)，
  // 到自动机识别或runtime时，basicCharset是byte(ISO-8859-1)。
  private String textCharset = "ISO-8859-1-plus";

  public int countOfChars() {
    return countOfChars;
  }

  public String getGrammarCharset() {
    return grammarCharset;
  }

  public char getGrammarSpace() {
    return grammarSpace;
  }

  public boolean isGrammarWhitespace(int ch) {
    for (char whitespace : grammarWhitespaces) {
      if (ch == whitespace) {
        return true;
      }
    }
    return false;
  }

  public char[] getDisplayingChars(int tchar) {
    if (tchar == getTextEpsilon()) {
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

  public String getDisplayingString(String str) {
    if (null == str) {
      return "";
    }
    StringBuilder stringBuilder = new StringBuilder();
    char[] chars = str.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      char ch = chars[i];
      stringBuilder.append(getDisplayingChars(ch));
    }
    return stringBuilder.toString();
  }

  public String getDisplayingString(int[] chars) {
    if (null == chars || chars.length <= 0) {
      return "";
    }
    return getDisplayingString(chars, 0, chars.length);
  }

  public String getDisplayingString(ArrayList<Integer> chars) {
    if (null == chars || chars.isEmpty()) {
      return "";
    }
    int[] charsArr = new int[chars.size()];
    int indexOfCharArr = 0;
    for (int ch : chars) {
      charsArr[indexOfCharArr++] = ch;
    }
    return getDisplayingString(charsArr, 0, charsArr.length);
  }

  public String getDisplayingString(int[] chars, int startIndex, int count) {
    if (null == chars || chars.length <= 0) {
      return "";
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = startIndex; i < count; i++) {
      int ch = chars[i];
      stringBuilder.append(getDisplayingChars(ch));
    }
    return stringBuilder.toString();
  }

  public int getIntByEscapeChar(char ch) {
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

  public int getIntByEscapeChar(int ch) {
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

  public int getTextMax() {
    return textMax;
  }

  public int getTextEpsilon() {
    return textEpsilon;
  }

  /**
   * normal chars.
   *
   * @return dfa所有可能的字符
   */
  public int[] getTokenDfaChars() {
    int[] chars = new int[textMax + 1];
    // normal chars
    for (int indexOfChar = 0; indexOfChar <= textMax; indexOfChar++) {
      chars[indexOfChar] = indexOfChar;
    }
    return chars;
  }

  public int getIntByHexDigitChar(char tchar) {
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

  public int getIntByHexDigitChar(int tchar) {
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

  public int getIntByDigitChar(char tchar) {
    return tchar - '0';
  }

  public int getIntByDigitChar(int tchar) {
    return tchar - '0';
  }

  public int getIntByOctalDigitChar(char tchar) {
    return tchar - '0';
  }

  public int getIntByOctalDigitChar(int tchar) {
    return tchar - '0';
  }

  public boolean isHexDigitChar(char tchar) {
    return isDigitChar(tchar) || (tchar >= 'a' && tchar <= 'f') || (tchar >= 'A' && tchar <= 'F');
  }

  public boolean isHexDigitChar(int tchar) {
    return isDigitChar(tchar) || (tchar >= 'a' && tchar <= 'f') || (tchar >= 'A' && tchar <= 'F');
  }

  public boolean isOctalDigitChar(char tchar) {
    return tchar >= '0' && tchar <= '7';
  }

  public boolean isOctalDigitChar(int tchar) {
    return tchar >= '0' && tchar <= '7';
  }

  public boolean isDigitChar(char tchar) {
    return tchar >= '0' && tchar <= '9';
  }

  public boolean isDigitChar(int tchar) {
    return tchar >= '0' && tchar <= '9';
  }

  public boolean isGrammarNewLine(int tchar) {
    return tchar == grammarNewline;
  }

  public String getTextCharset() {
    return textCharset;
  }

  /**
   * \\已经被识别，\\后的text必须要能正确表示一个转义字符，将转义字符转为其对应的真正字符，设置到stringBuilder容器.
   *
   * @param text 正则文本
   * @param indexOfText 正则文本的 索引
   * @return 处理后 新的 正则文本的 索引
   * @params tringBuilder 接收转义字符其对应的真正字符的 容器
   */
  public int formatEscapeChar2CharAndSet(
      char[] text, int indexOfText, StringBuilder stringBuilder) {
    // \ddd
    if (isOctalDigitChar(text[indexOfText])) {
      char[] octalNumberChars = new char[octalLengthOfTextChar];
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
        if (vchar > textMax) {
          throw new AstRuntimeException(
              String.format("not a EscapeChar,error in '%s'", new String(text)));
        }
        stringBuilder.append((char) vchar);
        indexOfText = indexOfText + sizeOfOctalNumbers;
        return indexOfText;
      }
    }
    // charset EscapeChar
    int intEscapeChar = this.getIntByEscapeChar(text[indexOfText]);
    if (intEscapeChar >= 0) {
      stringBuilder.append((char) intEscapeChar);
      ++indexOfText;
      return indexOfText;
    }

    // \xhh \Xhh
    if ('x' == text[indexOfText] || 'X' == text[indexOfText]) {
      char[] hexNumberChars = new char[hexLengthOfTextChar];
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
        if (vchar > textMax) {
          throw new AstRuntimeException(
              String.format("not a EscapeChar,error in '%s'", new String(text)));
        }
        stringBuilder.append((char) vchar);
        indexOfText = indexOfText + sizeOfHexNumbers + 1; // 1==x or X
        return indexOfText;
      }
    }
    return indexOfText;
  }

  public int formatEscapeChar2CharAndSet(
      int[] text, int indexOfText, ArrayList<Integer> charsBuilder) {
    // \ddd
    if (isOctalDigitChar(text[indexOfText])) {
      int[] octalNumberChars = new int[octalLengthOfTextChar];
      int indexOfOctalNumber = 0;
      int indexOfOctalCharText = indexOfText;
      while (indexOfOctalCharText < text.length && indexOfOctalNumber < octalNumberChars.length) {
        int octalChar = text[indexOfOctalCharText++];
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
        if (vchar > textMax) {
          throw new AstRuntimeException(
              String.format("not a EscapeChar,error in '%s'", getDisplayingString(text)));
        }
        charsBuilder.add(vchar);
        indexOfText = indexOfText + sizeOfOctalNumbers;
        return indexOfText;
      }
    }
    // charset EscapeChar
    int intEscapeChar = this.getIntByEscapeChar(text[indexOfText]);
    if (intEscapeChar >= 0) {
      charsBuilder.add(intEscapeChar);
      ++indexOfText;
      return indexOfText;
    }

    // \xhh \Xhh
    if ('x' == text[indexOfText] || 'X' == text[indexOfText]) {
      int[] hexNumberChars = new int[hexLengthOfTextChar];
      int indexOfHexNumber = 0;
      int indexOfHexCharText = indexOfText + 1;
      while (indexOfHexCharText < text.length && indexOfHexNumber < hexNumberChars.length) {
        int hexChar = text[indexOfHexCharText++];
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
        if (vchar > textMax) {
          throw new AstRuntimeException(
              String.format("not a EscapeChar,error in '%s'", getDisplayingString(text)));
        }
        charsBuilder.add(vchar);
        indexOfText = indexOfText + sizeOfHexNumbers + 1; // 1==x or X
        return indexOfText;
      }
    }
    return indexOfText;
  }

  /**
   * 将转义字符设置成其对应的字符.
   *
   * @param str
   * @return
   */
  public String formatEscapeChar2Char(String str) {
    StringBuilder stringBuilder = new StringBuilder();
    char[] text = str.toCharArray();
    int indexOfText = 0;
    while (indexOfText < text.length) {
      char tchar = text[indexOfText];
      if ('\\' == tchar) { // 转义字符 基本单元正则内容中\后面必须跟一个能正确表示转义的字符
        ++indexOfText;
        if (indexOfText >= text.length) {
          throw new AstRuntimeException(String.format("'%s'expect escape char", str));
        }
        int newIndexByEscapeChar = formatEscapeChar2CharAndSet(text, indexOfText, stringBuilder);
        if (newIndexByEscapeChar <= indexOfText) {
          throw new AstRuntimeException(String.format("'%s'expect escape char", str));
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
