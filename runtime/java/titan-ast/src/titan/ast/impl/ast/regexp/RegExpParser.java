package titan.ast.impl.ast.regexp;

import java.util.Arrays;
import java.util.LinkedList;
import titan.ast.grammar.GrammarAttribute.NfaTerminalGrammarAttribute;
import titan.ast.grammar.PrimaryGrammarContent.NfaPrimaryGrammarContentEdge;
import titan.ast.grammar.regexp.GrammarRegExp;
import titan.ast.grammar.regexp.OneCharOptionCharsetRegExp;
import titan.ast.grammar.regexp.RepeatTimes;
import titan.ast.grammar.regexp.SequenceCharsRegExp;

/**
 * .
 *
 * @author tian wei jun
 */
public class RegExpParser {

  public static final int HEX_LENGTH_OF_TEXT_CHAR = 2;
  public static final int MAX_CHAR = 0xFF;
  private static final String KW_DERIVE = "derive";

  private RegExpParser() {
  }

  public static OneCharOptionCharsetRegExp getOneCharOptionCharsetRegExp(String str) {
    char[] charArray = str.toCharArray();
    StringBuilder stringBuilder = new StringBuilder(str.length());
    int indexOfChar = setCharsForOneCharOptionCharset(stringBuilder, charArray, 0);
    char[] chars = stringBuilder.toString().toCharArray();
    ++indexOfChar;//skip ']'
    RepeatTimes[] repeatTimes = getRepeatTimes(charArray, indexOfChar);
    return new OneCharOptionCharsetRegExp(chars, repeatTimes[0], repeatTimes[1]);
  }

  public static SequenceCharsRegExp getSequenceCharsRegExp(String str) {
    char[] charArray = str.toCharArray();
    StringBuilder stringBuilder = new StringBuilder(str.length());
    int indexOfChar = setCharsForSequenceChars(stringBuilder, charArray, 0);
    String chars = stringBuilder.toString();
    ++indexOfChar;//skip '\''
    RepeatTimes[] repeatTimes = getRepeatTimes(charArray, indexOfChar);
    return new SequenceCharsRegExp(chars, repeatTimes[0], repeatTimes[1]);
  }

  public static GrammarRegExp getGrammarRegExp(String grammarName) {
    return new GrammarRegExp(grammarName);
  }

  // Identifier RepeatTimes
  public static GrammarRegExp getGrammarUnitRegExpRepeatTimes(String str) {
    StringBuilder stringBuilder = new StringBuilder(str.length());
    char[] charArray = str.toCharArray();
    int indexOfChar = 0;
    for (; indexOfChar < charArray.length; indexOfChar++) {
      char ch = charArray[indexOfChar];
      if (isPrefixOfRepeatTimes(ch)) {
        break;
      }
      stringBuilder.append(ch);
    }
    String grammarName = stringBuilder.toString();
    RepeatTimes[] repeatTimes = getRepeatTimes(charArray, indexOfChar);
    return new GrammarRegExp(grammarName, repeatTimes[0], repeatTimes[1]);
  }

  //ParenthesisUnitRegExpSuffixFragment : ')' RepeatTimes? ;
  public static RepeatTimes[] getRepeateTimesByParenthesisUnitRegExpSuffix(String parenthesisUnitRegExpSuffix) {
    char[] charArray = parenthesisUnitRegExpSuffix.toCharArray();
    return getRepeatTimes(charArray, 1);//skip ')'
  }

  private static RepeatTimes[] getRepeatTimes(char[] charArray, int indexOfChar) {
    RepeatTimes repMinTimes = RepeatTimes.getNumberTimes(1);
    RepeatTimes repMaxTimes = RepeatTimes.getNumberTimes(1);
    if (indexOfChar >= charArray.length) {
      return new RepeatTimes[]{
          repMinTimes, repMaxTimes
      };
    }

    char firstCh = charArray[indexOfChar];
    switch (firstCh) {
      case '?' -> {
        repMinTimes = RepeatTimes.getNumberTimes(0);
        repMaxTimes = RepeatTimes.getNumberTimes(1);
      }
      case '*' -> {
        repMinTimes = RepeatTimes.getNumberTimes(0);
        repMaxTimes = RepeatTimes.getInfinityTimes();
      }
      case '+' -> {
        repMinTimes = RepeatTimes.getNumberTimes(1);
        repMaxTimes = RepeatTimes.getInfinityTimes();
      }
      case '{' -> {
        RepeatTimes[] repeatTimes = getRepeatTimesByLeftBrace(charArray, indexOfChar);
        repMinTimes = repeatTimes[0];
        repMaxTimes = repeatTimes[1];
      }
      default -> {
        repMinTimes = RepeatTimes.getNumberTimes(1);
        repMaxTimes = RepeatTimes.getNumberTimes(1);
      }
    }
    return new RepeatTimes[]{
        repMinTimes, repMaxTimes
    };
  }

  //'{' naturalNumber? ',' naturalNumber? '}'
  private static RepeatTimes[] getRepeatTimesByLeftBrace(char[] charArray, int indexOfChar) {
    StringBuilder repMinTimesStr = new StringBuilder(charArray.length - indexOfChar);
    for (indexOfChar += 1; indexOfChar < charArray.length; indexOfChar++) {
      char ch = charArray[indexOfChar];
      if (ch == ',') {
        break;
      }
      repMinTimesStr.append(ch);
    }
    StringBuilder repMaxTimesStr = new StringBuilder(charArray.length - indexOfChar);
    for (indexOfChar += 1; indexOfChar < charArray.length; indexOfChar++) {
      char ch = charArray[indexOfChar];
      if (ch == '}') {
        break;
      }
      repMaxTimesStr.append(ch);
    }
    RepeatTimes repMinTimes = RepeatTimes.getInfinityTimes();
    RepeatTimes repMaxTimes = RepeatTimes.getInfinityTimes();
    if (!repMinTimesStr.isEmpty()) {
      repMinTimes.setTimes(Integer.parseInt(repMinTimesStr.toString(), 10));
    }
    if (!repMaxTimesStr.isEmpty()) {
      repMaxTimes.setTimes(Integer.parseInt(repMaxTimesStr.toString(), 10));
    }
    return new RepeatTimes[]{
        repMinTimes, repMaxTimes
    };
  }

  private static boolean isPrefixOfRepeatTimes(char ch) {
    return switch (ch) {
      case '?', '*', '+', '{' -> true;
      default -> false;
    };
  }

  public static int setCharsForOneCharOptionCharset(
      StringBuilder charsBuilder, char[] charArray, int indexOfChar) {
    OneCharOptionCharsetRegExpCreationDescriptor creationDescriptor =
        new OneCharOptionCharsetRegExpCreationDescriptor();
    StringBuilder stringBuilder = new StringBuilder(charArray.length - indexOfChar);
    char ch = charArray[indexOfChar];
    if (ch == '~') {
      creationDescriptor.isNot = true;
      ++indexOfChar;//charArray[indexOfChar]=='['
    }
    ++indexOfChar;//skip '['
    for (; indexOfChar < charArray.length; ) {
      ch = charArray[indexOfChar];
      if (ch == '\\') {
        indexOfChar = setEscapeCharForOneCharOptionCharset(stringBuilder, charArray, indexOfChar);
        continue;
      }
      if (ch == ']') {
        break;
      }
      if (ch == '-') {
        creationDescriptor.indexsOfRangeFlag.add(stringBuilder.length() - 1);
      }
      stringBuilder.append(ch);
      ++indexOfChar;
    }
    creationDescriptor.originalChars = stringBuilder.toString().toCharArray();
    charsBuilder.append(creationDescriptor.getChars());
    return indexOfChar;
  }

  // '\'' CharForSequenceChars* '\'' RepeatTimes?
  public static int setCharsForSequenceChars(StringBuilder charsBuilder, char[] charArray, int indexOfChar) {
    StringBuilder stringBuilder = new StringBuilder(charArray.length - indexOfChar);
    ++indexOfChar; // skip'
    while (indexOfChar < charArray.length) {
      char ch = charArray[indexOfChar];
      if (ch == '\\') {
        indexOfChar = setEscapeCharForSequenceChars(stringBuilder, charArray, indexOfChar);
        continue;
      }
      if (ch == '\'') {
        break;
      }
      stringBuilder.append(ch);
      ++indexOfChar;
    }
    charsBuilder.append(stringBuilder.toString());
    return indexOfChar;
  }

  private static int setEscapeCharForOneCharOptionCharset(StringBuilder stringBuilder, char[] charArray,
      int indexOfChar) {
    ++indexOfChar;//skip '\\'
    char ch = charArray[indexOfChar];
    if (ch == 'x' || ch == 'X') {
      return setHexadecimalEscapeChar(charArray, indexOfChar, stringBuilder);
    }
    char escapeChar = ch;
    switch (ch) {
      case '-', ']', '\\' -> {
      }
      //0\\abfnrtvs
      case '0' -> {
        escapeChar = 0;
      }
      case 'a' -> {
        escapeChar = 7;
      }
      case 'b' -> {
        escapeChar = 8;
      }
      case 'f' -> {
        escapeChar = 12;
      }
      case 'n' -> {
        escapeChar = 10;
      }
      case 'r' -> {
        escapeChar = 13;
      }
      case 't' -> {
        escapeChar = 9;
      }
      case 'v' -> {
        escapeChar = 11;
      }
      case 's' -> {
        escapeChar = ' ';
      }
    }
    stringBuilder.append((char) (escapeChar & 0xFF));
    ++indexOfChar;
    return indexOfChar;
  }


  private static int setEscapeCharForSequenceChars(StringBuilder stringBuilder, char[] charArray, int indexOfChar) {
    ++indexOfChar;//skip '\\'
    char ch = charArray[indexOfChar];
    if (ch == 'x' || ch == 'X') {
      return setHexadecimalEscapeChar(charArray, indexOfChar, stringBuilder);
    }
    char escapeChar = ch;
    switch (ch) {
      case '\'', '\\' -> {
      }
      //0\\abfnrtvs
      case '0' -> {
        escapeChar = 0;
      }
      case 'a' -> {
        escapeChar = 7;
      }
      case 'b' -> {
        escapeChar = 8;
      }
      case 'f' -> {
        escapeChar = 12;
      }
      case 'n' -> {
        escapeChar = 10;
      }
      case 'r' -> {
        escapeChar = 13;
      }
      case 't' -> {
        escapeChar = 9;
      }
      case 'v' -> {
        escapeChar = 11;
      }
      case 's' -> {
        escapeChar = ' ';
      }
    }
    stringBuilder.append((char) (escapeChar & 0xFF));
    ++indexOfChar;
    return indexOfChar;
  }

  private static int setHexadecimalEscapeChar(char[] charArray, int indexOfChar, StringBuilder stringBuilder) {
    char[] hexNumberChars = new char[HEX_LENGTH_OF_TEXT_CHAR];
    int indexOfHexNumber = 0;
    int indexOfHexCharText = indexOfChar + 1;
    while (indexOfHexCharText < charArray.length && indexOfHexNumber < hexNumberChars.length) {
      char hexChar = charArray[indexOfHexCharText++];
      if (isHexDigitChar(hexChar)) {
        hexNumberChars[indexOfHexNumber++] = hexChar;
      } else {
        break;
      }
    }
    int sizeOfHexNumbers = indexOfHexNumber; // indexOfHexNumber==size of hexNumbers
    int vchar = 0;
    int multiples = 1;
    indexOfHexNumber = sizeOfHexNumbers - 1;
    while (indexOfHexNumber >= 0) {
      vchar += getIntByHexDigitChar(hexNumberChars[indexOfHexNumber]) * multiples;
      multiples *= 16;
      --indexOfHexNumber;
    }
    stringBuilder.append((char) (vchar & 0xFF));
    return indexOfChar + sizeOfHexNumbers + 1;
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

  public static boolean isHexDigitChar(char tchar) {
    return isDigitChar(tchar) || (tchar >= 'a' && tchar <= 'f') || (tchar >= 'A' && tchar <= 'F');
  }

  public static boolean isDigitChar(char tchar) {
    return tchar >= '0' && tchar <= '9';
  }

  // '#'  IdentifierFragment ;
  public static String getAliasByAndCompositeRegExpAlias(String nonformatAias) {
    return nonformatAias.substring(1);
  }

  //'nfa' '(' IdentifierFragment ',' IdentifierFragment  ')' ;
  public static NfaTerminalGrammarAttribute getNfaTerminalGrammarAttribute(String str) {
    String start = "";
    String end = "";
    char[] charArray = str.toCharArray();
    int indexOfCharArray = 0;
    while (indexOfCharArray < charArray.length) {
      char ch = charArray[indexOfCharArray];
      if (ch == '(') {
        break;
      }
      ++indexOfCharArray;
    }
    ++indexOfCharArray;//skip '('
    StringBuilder stringBuilder = new StringBuilder(charArray.length);
    while (indexOfCharArray < charArray.length) {
      char ch = charArray[indexOfCharArray];
      if (ch == ',') {
        break;
      }
      stringBuilder.append(ch);
      ++indexOfCharArray;
    }
    start = stringBuilder.toString();
    ++indexOfCharArray;//skip ','
    stringBuilder.delete(0, stringBuilder.length());
    while (indexOfCharArray < charArray.length) {
      char ch = charArray[indexOfCharArray];
      if (ch == ')') {
        break;
      }
      stringBuilder.append(ch);
      ++indexOfCharArray;
    }
    end = stringBuilder.toString();
    return new NfaTerminalGrammarAttribute(start, end);
  }

  /*
  IdentifierFragment '\'' CharForSequenceChars* '\'' IdentifierFragment
    | IdentifierFragment '~'? '[' CharForOneCharOptionCharset* ']' IdentifierFragment
   */
  public static NfaPrimaryGrammarContentEdge getNfaEdge(String str) {
    char[] charArray = str.toCharArray();
    //from
    boolean isSequenceChars = false;
    boolean isOneCharOptionCharset = false;
    StringBuilder stringBuilder = new StringBuilder(charArray.length);
    int indexOfChar = 0;
    while (indexOfChar < charArray.length) {
      char ch = charArray[indexOfChar];
      if (ch == '\'') {
        isSequenceChars = true;
        break;
      }
      if (ch == '[' || ch == '~') {
        isOneCharOptionCharset = true;
        break;
      }
      stringBuilder.append(ch);
      ++indexOfChar;
    }
    String from = stringBuilder.toString();
    //chars
    stringBuilder.delete(0, stringBuilder.length());
    if (isSequenceChars) {
      indexOfChar = setCharsForSequenceChars(stringBuilder, charArray, indexOfChar);
    }
    if (isOneCharOptionCharset) {
      indexOfChar = setCharsForOneCharOptionCharset(stringBuilder, charArray, indexOfChar);
    }
    char[] chars = stringBuilder.toString().toCharArray();
    //to
    ++indexOfChar;//skip '\'' or ']'
    String to = new String(charArray, indexOfChar, charArray.length - indexOfChar);
    return new NfaPrimaryGrammarContentEdge(from, to, chars);
  }

  // 'derive' '(' IdentifierFragment ')'
  public static String getRootTerminalGrammarNameByDerivedTerminalGrammarAttribute(
      String derivedTerminalGrammarAttribute) {
    return derivedTerminalGrammarAttribute.substring(KW_DERIVE.length() + 1, derivedTerminalGrammarAttribute.length() - 1);
  }

  private static class OneCharOptionCharsetRegExpCreationDescriptor {

    public boolean isNot = false;
    public char[] originalChars = new char[0];
    public boolean[] chars;
    LinkedList<Integer> indexsOfRangeFlag = new LinkedList<>();

    public char[] getChars() {
      chars = new boolean[MAX_CHAR + 1];
      Arrays.fill(chars, false);
      //处理[]中的chars
      buildCharsRegExpUnitOptionChars();
      //处理~
      if (isNot) {
        for (int i = 0; i < chars.length; i++) {
          boolean isExistCh = chars[i];
          chars[i] = !isExistCh;
        }
      }
      // count
      int countOfChars = 0;
      for (boolean isExistCh : chars) {
        if (isExistCh) {
          ++countOfChars;
        }
      }
      char[] retChars = new char[countOfChars];
      int indexOfRetChars = 0;
      for (int ch = 0; ch < chars.length; ch++) {
        boolean isExistCh = chars[ch];
        if (isExistCh) {
          retChars[indexOfRetChars++] = (char) ch;
        }
      }
      return retChars;
    }

    /**
     * 处理-
     */
    private void buildCharsRegExpUnitOptionChars() {
      if (indexsOfRangeFlag.isEmpty()) {
        buildCharsRegExpUnitOptionCharSet(0, originalChars.length - 1);
      } else {
        int start = 0;
        int end = 0;
        for (Integer indexOfRangeFlag : indexsOfRangeFlag) {
          end = indexOfRangeFlag - 2;
          buildCharsRegExpUnitOptionCharSet(start, end); // before

          start = indexOfRangeFlag - 1;
          end = indexOfRangeFlag + 1;
          buildCharsRegExpUnitOptionRangeSet(start, end); // range

          start = end + 1;
        }

        end = originalChars.length - 1;
        buildCharsRegExpUnitOptionCharSet(start, end); // last
      }
    }

    private void buildCharsRegExpUnitOptionRangeSet(
        int startIndexOfChar, int endIndexOfChar) {
      if (startIndexOfChar >= 0
          && endIndexOfChar < originalChars.length
          && startIndexOfChar + 2 == endIndexOfChar) {
        char minChar = originalChars[startIndexOfChar];
        char maxChar = originalChars[endIndexOfChar];
        for (int ch = minChar; ch <= maxChar; ch++) {
          chars[ch] = true;
        }
      }
    }

    private void buildCharsRegExpUnitOptionCharSet(
        int startIndexOfChar, int endIndexOfChar) {
      if (startIndexOfChar >= 0
          && endIndexOfChar < originalChars.length
          && startIndexOfChar <= endIndexOfChar) {

        for (int i = startIndexOfChar; i <= endIndexOfChar; i++) {
          int ch = originalChars[i];
          chars[ch] = true;
        }
      }
    }

  }
}
