package titan.ast.grammar.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.io.GrammarCharset;
import titan.ast.grammar.io.GrammarToken;
import titan.ast.runtime.AstRuntimeException;

/**
 * 表述nfa的文本结构类似一下例子： 'OneLineMacro nfa(0,10) : 0[#]1 1[\r]2 1~[\r]4 2[\401]7 2[\n]3 2~[\n]4 4~[\r]4
 * 4[\r]5 4[\401]8 5~[\n]4 5[\n]6 5[\401]9 6[]10 7[]10 8[]10 9[]10 ;' 将表述nfa的文本设置对应的nfa.
 *
 * @author tian wei jun
 */
public class NfaReg2TokenNfaConverter {

  private static final String KW_NFA_REGEXP_OF_DESCRIPTOR = "nfa";
  GrammarCharset grammarCharset;
  Grammar grammar;
  TokenNfa nfa;
  Iterator<GrammarToken> textIt;
  HashMap<String, TokenNfaState> textStateMap;
  LinkedList<TokenNfaStateCreateDescriptor> nfaStateCreateDescriptors;
  String strStart;
  String strEnd;

  /** 默认构造器初始化字段：grammarCharset、escapeCharGetter. */
  public NfaReg2TokenNfaConverter() {
    AstContext astContext = AstContext.get();
    grammarCharset = astContext.grammarCharset;
  }

  /**
   * 将终结符或者终结符的片段设置对应的nfa.
   *
   * @param grammar 终结符或者终结符的片段
   */
  public void convert(Grammar grammar) {
    this.nfa = grammar.regExp.tokenNfa;
    if (grammar.text.isEmpty()) {
      int epsilon = grammarCharset.getTextEpsilon();
      nfa.start.addEdge(epsilon, nfa.end);
      return;
    }
    this.grammar = grammar;
    textIt = grammar.text.iterator();
    textStateMap = new HashMap<>();
    nfaStateCreateDescriptors = new LinkedList<>();

    build();
  }

  /** 设置起始状态 根据边字符串设置createDescripitors 生成所有状态 按照descripitor构建边. */
  public void build() {
    buildStartEndState(grammar.getNfaRegexpContent());
    buildNfaStateCreateDescriptors();
    mapAllStates();
    buildEdges();
  }

  private void buildEdges() {
    Iterator<TokenNfaStateCreateDescriptor> descriptorsIt = nfaStateCreateDescriptors.iterator();
    while (descriptorsIt.hasNext()) {
      TokenNfaStateCreateDescriptor descriptor = descriptorsIt.next();
      TokenNfaState from = textStateMap.get(descriptor.from);
      TokenNfaState to = textStateMap.get(descriptor.to);
      ArrayList<Integer> chars = getCharsOfEdge(descriptor);
      for (Integer ch : chars) {
        from.addEdge(ch, to);
      }
    }
  }

  private ArrayList<Integer> getCharsOfEdge(TokenNfaStateCreateDescriptor descriptor) {
    ArrayList<Integer> charsBuilder = new ArrayList<Integer>();
    ArrayList<Integer> text = formatChars(descriptor);
    if (descriptor.indexOfRangeFlag.isEmpty()) {
      charsBuilder.addAll(text);
    } else {
      Iterator<Integer> indexOfRangeFlagIt = descriptor.indexOfRangeFlag.iterator();
      Integer indexOfRangeFlag;
      int startIndex = 0;
      int endIndex = 0;
      while (indexOfRangeFlagIt.hasNext()) {
        indexOfRangeFlag = indexOfRangeFlagIt.next();

        endIndex = indexOfRangeFlag - 2;
        buildNormalChars(charsBuilder, text, startIndex, endIndex); // before

        startIndex = indexOfRangeFlag - 1;
        endIndex = indexOfRangeFlag + 1;
        buildRangeChars(charsBuilder, text, startIndex, endIndex); // range

        startIndex = endIndex + 1;
      }
      endIndex = text.size() - 1;
      buildNormalChars(charsBuilder, text, startIndex, endIndex); // last
    }
    if (!descriptor.notChars) {
      return charsBuilder;
    }
    // not chars 1==chars保留该字符，默认全部保留
    int[] notCharsFlags = new int[grammarCharset.countOfChars()];
    for (int ch = 0; ch < notCharsFlags.length; ch++) {
      notCharsFlags[ch] = 1;
    }
    // 取反的不保留
    for (int indexOfChar = 0; indexOfChar < charsBuilder.size(); indexOfChar++) {
      int ch = charsBuilder.get(indexOfChar);
      notCharsFlags[ch] = 0;
    }
    ArrayList<Integer> notCharsStringBuilder = new ArrayList<Integer>();
    for (int ch = 0; ch < notCharsFlags.length; ch++) {
      if (notCharsFlags[ch] == 1) {
        notCharsStringBuilder.add(ch);
      }
    }
    return notCharsStringBuilder;
  }

  private void buildRangeChars(
      ArrayList<Integer> charsBuilder,
      ArrayList<Integer> chars,
      int startIndexOfChar,
      int endIndexOfChar) {
    if (startIndexOfChar >= 0
        && endIndexOfChar < chars.size()
        && startIndexOfChar + 2 == endIndexOfChar) {
      int minChar = chars.get(startIndexOfChar);
      int maxChar = chars.get(endIndexOfChar);
      for (int ch = minChar; ch <= maxChar; ch++) {
        charsBuilder.add(ch);
      }
    } else {
      StringBuilder errorInfo = new StringBuilder("");
      for (int indexOfChars = startIndexOfChar; indexOfChars < chars.size(); indexOfChars++) {
        errorInfo.append(chars.get(indexOfChars));
      }
      throw new AstRuntimeException(
          String.format(
              "%s:format like this,'from[min-max]to', 'min-max'error near %s",
              grammar.name, grammarCharset.getDisplayingString(errorInfo.toString())));
    }
  }

  private void buildNormalChars(
      ArrayList<Integer> charsBuilder,
      ArrayList<Integer> chars,
      int startIndexOfChar,
      int endIndexOfChar) {
    if (startIndexOfChar >= 0
        && endIndexOfChar < chars.size()
        && startIndexOfChar <= endIndexOfChar) {
      int length = endIndexOfChar - startIndexOfChar + 1;
      for (int indexOfChar = startIndexOfChar; indexOfChar < length; indexOfChar++) {
        charsBuilder.add(chars.get(indexOfChar));
      }
    }
  }

  /**
   * 转义字符转为正常字符.
   *
   * @param descriptor 描述正则文本最基础的元素
   * @return
   */
  private ArrayList<Integer> formatChars(TokenNfaStateCreateDescriptor descriptor) {
    ArrayList<Integer> charsBuilder = new ArrayList();
    int[] text = descriptor.chars;
    int indexOfText = 0;
    while (indexOfText < text.length) {
      int tchar = text[indexOfText];
      if ('\\' == tchar) { // 转义字符 基本单元正则内容中\后面必须跟一个能正确表示转义的字符
        ++indexOfText;
        if (indexOfText >= text.length) {
          throw new AstRuntimeException(
              String.format(
                  "%s:expect escape char near %s",
                  grammar.name, grammarCharset.getDisplayingString(text)));
        }
        indexOfText = setEscapeChar(text, indexOfText, charsBuilder);
      } else {
        charsBuilder.add(tchar);
        ++indexOfText;
      }
    }
    return charsBuilder;
  }

  private int setEscapeChar(int[] text, int indexOfText, ArrayList<Integer> charsBuilder) {
    int startIndexOfText = indexOfText;
    int newIndexByNormalEscapeChar =
        grammarCharset.formatEscapeChar2CharAndSet(text, indexOfText, charsBuilder);
    if (newIndexByNormalEscapeChar > indexOfText) {
      return newIndexByNormalEscapeChar;
    }
    // special char for regexp
    int intSpecialChar = grammarCharset.getIntByRegExpEscapeChar((char) text[indexOfText]);
    if (intSpecialChar >= 0) {
      charsBuilder.add(intSpecialChar);
      ++indexOfText;
      return indexOfText;
    }
    // other
    throw new AstRuntimeException(
        String.format(
            "%s:expect escape char,error near %s",
            grammar.name,
            grammarCharset.getDisplayingString(
                text, startIndexOfText, text.length - startIndexOfText)));
  }

  private void mapAllStates() {
    HashSet<String> strStates = new HashSet<>();
    Iterator<TokenNfaStateCreateDescriptor> descriptorsIt = nfaStateCreateDescriptors.iterator();
    while (descriptorsIt.hasNext()) {
      TokenNfaStateCreateDescriptor descriptor = descriptorsIt.next();
      strStates.add(descriptor.from);
      strStates.add(descriptor.to);
    }
    // start end
    strStates.remove(strStart);
    strStates.remove(strEnd);
    textStateMap.put(strStart, nfa.start);
    textStateMap.put(strEnd, nfa.end);
    // other state
    Iterator<String> strStatesIt = strStates.iterator();
    while (strStatesIt.hasNext()) {
      String strState = strStatesIt.next();
      textStateMap.put(strState, new TokenNfaState());
    }
  }

  private void buildNfaStateCreateDescriptors() {
    while (textIt.hasNext()) {
      GrammarToken token = textIt.next();
      buildNfaStateCreateDescriptor(token);
    }
  }

  /**
   * 边的结构类似 xx~[xx]xx ，将其转为对应的描述符.
   *
   * @param token 一条边的文本
   */
  private void buildNfaStateCreateDescriptor(GrammarToken token) {
    TokenNfaStateCreateDescriptor descriptor = new TokenNfaStateCreateDescriptor();
    char[] chars = token.text.toCharArray();
    int indexOfChar = 0;
    ArrayList<Integer> charsBuilder = new ArrayList<Integer>();
    boolean isRight = false;
    // from
    while (indexOfChar < chars.length) {
      char ch = chars[indexOfChar];
      if (ch == '~' || ch == '[') {
        isRight = true;
        break;
      }
      charsBuilder.add((int) ch);
      ++indexOfChar;
    }
    if (!isRight || charsBuilder.isEmpty()) {
      throw new AstRuntimeException(
          String.format("%s: edge is wrong in %s", grammar.name, token.text));
    }
    descriptor.from = grammarCharset.getDisplayingString(charsBuilder);
    // edge chars
    charsBuilder.clear();
    isRight = false;
    if (chars[indexOfChar] == '~') {
      descriptor.notChars = true;
      ++indexOfChar;
      expectChar('[', chars, indexOfChar);
    }
    ++indexOfChar; // skip [
    while (indexOfChar < chars.length) {
      char ch = chars[indexOfChar];
      if (ch == ']') {
        isRight = true;
        break;
      }
      if (ch == '-') {
        descriptor.indexOfRangeFlag.add(charsBuilder.size());
      }
      charsBuilder.add((int) ch);
      ++indexOfChar;
    }
    if (!isRight) {
      throw new AstRuntimeException(
          String.format("%s: edge is wrong in %s", grammar.name, token.text));
    }
    if (charsBuilder.isEmpty()) {
      charsBuilder.add(grammarCharset.getTextEpsilon());
    }
    int[] charsArr = new int[charsBuilder.size()];
    int indexOfCharArr = 0;
    for (int ch : charsBuilder) {
      charsArr[indexOfCharArr++] = ch;
    }
    descriptor.chars = charsArr;
    // to
    ++indexOfChar; // skip ]
    charsBuilder.clear();
    while (indexOfChar < chars.length) {
      char ch = chars[indexOfChar];
      charsBuilder.add((int) ch);
      ++indexOfChar;
    }
    if (charsBuilder.isEmpty()) {
      throw new AstRuntimeException(
          String.format("%s: edge is wrong in %s", grammar.name, token.text));
    }
    descriptor.to = grammarCharset.getDisplayingString(charsBuilder);

    nfaStateCreateDescriptors.add(descriptor);
  }

  /**
   * 文本内容格式为nfa(start,end)，将其信息识别并保存.
   *
   * @param token 格式为nfa(start,end)的文本
   */
  private void buildStartEndState(GrammarToken token) {
    char[] chars = token.text.toCharArray();
    StringBuilder stringBuilder = new StringBuilder();
    // satrt
    int indexOfChar = KW_NFA_REGEXP_OF_DESCRIPTOR.length();
    // (
    expectChar('(', chars, indexOfChar);
    ++indexOfChar;
    // start
    boolean isRight = false;
    while (indexOfChar < chars.length) {
      if (chars[indexOfChar] == ',') {
        isRight = true;
        break;
      }
      stringBuilder.append(chars[indexOfChar]);
      ++indexOfChar;
    }
    if (!isRight || stringBuilder.length() <= 0) {
      throw new AstRuntimeException(
          String.format("%s:nfa(start,end) is error near %s", grammar.name, token.text));
    }
    this.strStart = stringBuilder.toString();
    // end
    ++indexOfChar; // skip,
    isRight = false;
    stringBuilder.delete(0, stringBuilder.length());
    while (indexOfChar < chars.length) {
      if (chars[indexOfChar] == ')') {
        isRight = true;
        break;
      }
      stringBuilder.append(chars[indexOfChar]);
      ++indexOfChar;
    }
    if (!isRight || stringBuilder.length() <= 0) {
      throw new AstRuntimeException(
          String.format("%s:nfa(start,end) is error near %s", grammar.name, token.text));
    }
    this.strEnd = stringBuilder.toString();
    ++indexOfChar; // skip)
  }

  private void expectChar(char ch, char[] chars, int indexOfChar) {
    boolean isRight = indexOfChar >= 0 && indexOfChar < chars.length && ch == chars[indexOfChar];
    if (!isRight) {
      throw new AstRuntimeException(
          String.format(
              "%s:expect a '%c' in %s",
              grammar.name, ch, new String(chars, indexOfChar, chars.length - indexOfChar)));
    }
  }

  /** 用以描述一条边所需的信息. */
  public static class TokenNfaStateCreateDescriptor {

    public String from; // 起始状态id
    public String to; // 目的状态id
    public boolean notChars = false; // 是否是非
    public int[] chars; // 字符内容
    public LinkedList<Integer> indexOfRangeFlag = new LinkedList<>(); // -在chars中的index

    @Override
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      if (null != from) {
        stringBuilder.append(from);
      }
      if (notChars) {
        stringBuilder.append('~');
      }
      if (null != chars && chars.length > 0) {
        stringBuilder.append(chars);
      }
      if (null != to) {
        stringBuilder.append(to);
      }
      return stringBuilder.toString();
    }
  }
}
