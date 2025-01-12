package titan.ast.grammar.token;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import titan.ast.AstContext;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.io.GrammarCharset;
import titan.ast.grammar.io.GrammarToken;
import titan.ast.grammar.regexp.RegExp;

/**
 * 表述nfa的文本结构类似一下例子： 'OneLineMacro nfa(0,10) : 0[#]1 1[\r]2 1~[\r]4 2[\401]7 2[\n]3 2~[\n]4 4~[\r]4
 * 4[\r]5 4[\401]8 5~[\n]4 5[\n]6 5[\401]9 6[]10 7[]10 8[]10 9[]10 ;' 将表述nfa的文本设置对应的nfa.
 *
 * @author tian wei jun
 */
public class NfaReg2TokenNfaConverter {

  private static final String KW_NFA_REGEXP_OF_DESCRIPTOR = "nfa";
  Grammar grammar;
  TokenNfa nfa;
  Iterator<GrammarToken> textIt;
  HashMap<String, TokenNfaState> textStateMap;
  LinkedList<TokenNfaStateDescriptor> nfaStateCreateDescriptors;
  String strStart;
  String strEnd;

  /** 默认构造器初始化字段：grammarCharset、escapeCharGetter. */
  public NfaReg2TokenNfaConverter() {
    AstContext astContext = AstContext.get();
  }

  /**
   * 将终结符或者终结符的片段设置对应的nfa.
   *
   * @param grammar 终结符或者终结符的片段
   */
  public void convert(Grammar grammar) {
    this.nfa = grammar.regExp.tokenNfa;
    if (grammar.text.isEmpty()) {
      nfa.start.addEdge(GrammarCharset.EPSILON, nfa.end);
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
    buildStartEndState(grammar.getNfaRegexpAttributeToken());
    buildNfaStateDescriptors();
    mapTextsAndStates();
    buildEdges();
  }

  private void buildEdges() {
    for (TokenNfaStateDescriptor descriptor : nfaStateCreateDescriptors) {
      if (descriptor instanceof OneCharOptionCharsetDescriptor oneCharOptionCharsetDescriptor) {
        buildEdgesByOneCharOptionCharsetDescriptor(oneCharOptionCharsetDescriptor);
        continue;
      }
      if (descriptor instanceof SequenceCharsTokenNfaStateDescriptor sequenceCharsDescriptor) {
        buildEdgesBySequenceCharsDescriptor(sequenceCharsDescriptor);
      }
    }
  }

  private void buildEdgesBySequenceCharsDescriptor(
      SequenceCharsTokenNfaStateDescriptor descriptor) {
    TokenNfaState from = textStateMap.get(descriptor.from);
    TokenNfaState to = textStateMap.get(descriptor.to);
    char[] chars = descriptor.getCharsOfEdge();
    if (chars.length < 1) { // from[]to | from ~[\x00-\xFF]to
      from.addEdge(GrammarCharset.EPSILON, to);
    } else {
      TokenNfaState prevState = from;
      TokenNfaState nextState = new TokenNfaState();
      for (char ch : chars) {
        prevState.addEdge((int) ch, nextState);
        prevState = nextState;
        nextState = new TokenNfaState();
      }
      prevState.addEdge(GrammarCharset.EPSILON, to);
    }
  }

  private void buildEdgesByOneCharOptionCharsetDescriptor(
      OneCharOptionCharsetDescriptor descriptor) {
    TokenNfaState from = textStateMap.get(descriptor.from);
    TokenNfaState to = textStateMap.get(descriptor.to);
    char[] chars = descriptor.getCharsOfEdge();
    if (chars.length < 1) { // from[]to | from ~[\x00-\xFF]to
      from.addEdge(GrammarCharset.EPSILON, to);
    } else {
      for (char ch : chars) {
        from.addEdge((int) ch, to);
      }
    }
  }

  private void mapTextsAndStates() {
    HashSet<String> strStates = new HashSet<>();
    Iterator<TokenNfaStateDescriptor> descriptorsIt = nfaStateCreateDescriptors.iterator();
    while (descriptorsIt.hasNext()) {
      TokenNfaStateDescriptor descriptor = descriptorsIt.next();
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

  private void buildNfaStateDescriptors() {
    while (textIt.hasNext()) {
      nfaStateCreateDescriptors.add(
          TokenNfaStateDescriptor.buildTokenNfaStateDescriptor(this, textIt.next()));
    }
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
    expectOneChar(GrammarCharset.LEFT_PAREN, chars, indexOfChar);
    ++indexOfChar;
    // start
    boolean isEndCharRight = false;
    while (indexOfChar < chars.length) {
      if (chars[indexOfChar] == GrammarCharset.COMMA) {
        isEndCharRight = true;
        break;
      }
      stringBuilder.append(chars[indexOfChar]);
      ++indexOfChar;
    }
    if (!isEndCharRight || stringBuilder.length() <= 0) {
      throw new AstRuntimeException(
          String.format("%s:nfa(start,end) is error near %s", grammar.name, token.text));
    }
    this.strStart = GrammarCharset.formatEscapeChar2Char(stringBuilder.toString());
    // end
    ++indexOfChar; // skip,
    isEndCharRight = false;
    stringBuilder.delete(0, stringBuilder.length());
    while (indexOfChar < chars.length) {
      if (chars[indexOfChar] == GrammarCharset.RIGHT_PAREN) {
        isEndCharRight = true;
        break;
      }
      stringBuilder.append(chars[indexOfChar]);
      ++indexOfChar;
    }
    if (!isEndCharRight || stringBuilder.length() <= 0) {
      throw new AstRuntimeException(
          String.format("%s:nfa(start,end) is error near %s", grammar.name, token.text));
    }
    this.strEnd = GrammarCharset.formatEscapeChar2Char(stringBuilder.toString());
    ++indexOfChar; // skip)
  }

  private void expectOneChar(char ch, char[] text, int indexOfText) {
    if (indexOfText >= text.length) {
      throw new AstRuntimeException(
          String.format(
              "%s: expect a char '%c',but there are empty,error at end of %s",
              grammar.name, ch, new String(text)));
    }
    if (text[indexOfText] != ch) {
      throw new AstRuntimeException(
          String.format(
              "%s: expect a char '%c',error near %s",
              grammar.name, ch, new String(text, indexOfText, text.length - indexOfText)));
    }
  }

  private enum TokenNfaStateDescriptorType {
    SEQUENCE_CHARS,
    ONE_CHAR_OPTION_CHARSET,
  }

  private abstract static class TokenNfaStateDescriptor {
    public String from; // 起始状态id
    public String to; // 目的状态id
    TokenNfaStateDescriptorType type;
    char[] chars; // 边
    NfaReg2TokenNfaConverter parent;

    public TokenNfaStateDescriptor(NfaReg2TokenNfaConverter parent) {
      this.parent = parent;
    }

    private static TokenNfaStateDescriptorType getTypeByPrefix(GrammarToken token) {
      for (char ch : token.text.toCharArray()) {
        if (ch == GrammarCharset.LEFT_BRACKET) {
          return TokenNfaStateDescriptorType.ONE_CHAR_OPTION_CHARSET;
        }
        if (ch == GrammarCharset.SINGLE_QUOTE) {
          return TokenNfaStateDescriptorType.SEQUENCE_CHARS;
        }
      }
      return null;
    }

    public static TokenNfaStateDescriptor buildTokenNfaStateDescriptor(
        NfaReg2TokenNfaConverter parent, GrammarToken token) {
      TokenNfaStateDescriptorType typeByPrefix = getTypeByPrefix(token);
      if (typeByPrefix != null) {
        switch (typeByPrefix) {
          case SEQUENCE_CHARS -> {
            return new SequenceCharsTokenNfaStateDescriptor(parent, token);
          }
          case ONE_CHAR_OPTION_CHARSET -> {
            return new OneCharOptionCharsetDescriptor(parent, token);
          }
        }
      }
      throw new AstRuntimeException(
          String.format("%s: edge is wrong in %s", parent.grammar.name, token.text));
    }

    /**
     * 转义字符转为正常字符.
     *
     * @param text
     * @return
     */
    StringBuilder formatChars(char[] text) {
      StringBuilder charsBuilder = new StringBuilder(text.length);
      int indexOfText = 0;
      while (indexOfText < text.length) {
        char tchar = text[indexOfText];
        if (GrammarCharset.BACK_SLASH == tchar) { // 转义字符 基本单元正则内容中\后面必须跟一个能正确表示转义的字符
          ++indexOfText;
          if (indexOfText >= text.length) {
            throw new AstRuntimeException(
                String.format(
                    "%s:expect escape char near %s", parent.grammar.name, new String(text)));
          }
          indexOfText = setEscapeChar(text, indexOfText, charsBuilder);
        } else {
          charsBuilder.append(tchar);
          ++indexOfText;
        }
      }
      return charsBuilder;
    }

    private int setEscapeChar(char[] text, int indexOfText, StringBuilder charsBuilder) {
      int newIndexOfText = RegExp.formatEscapeChar2CharAndSet(text, indexOfText, charsBuilder);
      if (newIndexOfText > indexOfText) {
        return newIndexOfText;
      }
      // other
      throw new AstRuntimeException(
          String.format(
              "%s:expect escape char,error near %s", parent.grammar.name, new String(text)));
    }
  }

  private static class SequenceCharsTokenNfaStateDescriptor extends TokenNfaStateDescriptor {

    public SequenceCharsTokenNfaStateDescriptor(
        NfaReg2TokenNfaConverter parent, GrammarToken token) {
      super(parent);
      setFromCharsTo(token);
    }

    /**
     * 边的结构类似 xx'xx'xx ，将其转为对应的描述符.
     *
     * @param token 一条边的文本
     */
    private void setFromCharsTo(GrammarToken token) {
      char[] charsOfText = token.text.toCharArray();
      int indexOfChar = 0;
      StringBuilder charsBuilder = new StringBuilder();
      boolean isEndTextRight = false;
      // from
      while (indexOfChar < charsOfText.length) {
        char ch = charsOfText[indexOfChar];
        if (ch == GrammarCharset.SINGLE_QUOTE) {
          isEndTextRight = true;
          break;
        }
        charsBuilder.append(ch);
        ++indexOfChar;
      }
      if (!isEndTextRight || charsBuilder.isEmpty()) {
        throw new AstRuntimeException(
            String.format("%s: edge is wrong in %s", parent.grammar.name, token.text));
      }

      this.from = GrammarCharset.formatEscapeChar2Char(charsBuilder.toString());
      // edge chars
      // skip \'
      ++indexOfChar;
      charsBuilder.delete(0, charsBuilder.length());
      isEndTextRight = false;
      while (indexOfChar < charsOfText.length) {
        char ch = charsOfText[indexOfChar];
        if (ch == GrammarCharset.SINGLE_QUOTE) {
          isEndTextRight = true;
          break;
        }
        charsBuilder.append(ch);
        ++indexOfChar;
      }
      if (!isEndTextRight) {
        throw new AstRuntimeException(
            String.format("%s: edge is wrong in %s", parent.grammar.name, token.text));
      }
      char[] charsArr = new char[charsBuilder.length()];
      int indexOfCharArr = 0;
      for (char ch : charsBuilder.toString().toCharArray()) {
        charsArr[indexOfCharArr++] = ch;
      }
      this.chars = charsArr;
      // to
      ++indexOfChar; // skip \'
      charsBuilder.delete(0, charsBuilder.length());
      while (indexOfChar < charsOfText.length) {
        char ch = charsOfText[indexOfChar];
        charsBuilder.append(ch);
        ++indexOfChar;
      }
      if (charsBuilder.isEmpty()) {
        throw new AstRuntimeException(
            String.format("%s: edge is wrong in %s", parent.grammar.name, token.text));
      }
      this.to = GrammarCharset.formatEscapeChar2Char(charsBuilder.toString());
    }

    public char[] getCharsOfEdge() {
      return formatChars(this.chars).toString().toCharArray();
    }

    @Override
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      if (null != from) {
        stringBuilder.append(from);
      }
      if (null != chars) {
        stringBuilder.append("'");
        for (int ch : chars) {
          stringBuilder.append((char) (ch & 0xFF));
        }
        stringBuilder.append("'");
      }
      if (null != to) {
        stringBuilder.append(to);
      }
      return stringBuilder.toString();
    }
  }

  /** 用以描述一条边所需的信息. */
  private static class OneCharOptionCharsetDescriptor extends TokenNfaStateDescriptor {
    public boolean notChars = false; // 是否是非

    public LinkedList<Integer> indexOfRangeFlag = new LinkedList<>(); // -在chars中的index

    public OneCharOptionCharsetDescriptor(NfaReg2TokenNfaConverter parent, GrammarToken token) {
      super(parent);
      setFromCharsTo(token);
    }

    /**
     * 边的结构类似 xx~[xx]xx ，将其转为对应的描述符.
     *
     * @param token 一条边的文本
     */
    private void setFromCharsTo(GrammarToken token) {
      char[] charsOfText = token.text.toCharArray();
      int indexOfChar = 0;
      StringBuilder charsBuilder = new StringBuilder();
      boolean isEndTextRight = false;
      // from
      while (indexOfChar < charsOfText.length) {
        char ch = charsOfText[indexOfChar];
        if (ch == GrammarCharset.TILDE || ch == GrammarCharset.LEFT_BRACKET) {
          isEndTextRight = true;
          break;
        }
        charsBuilder.append(ch);
        ++indexOfChar;
      }
      if (!isEndTextRight || charsBuilder.isEmpty()) {
        throw new AstRuntimeException(
            String.format("%s: edge is wrong in %s", parent.grammar.name, token.text));
      }
      this.from = GrammarCharset.formatEscapeChar2Char(charsBuilder.toString());
      // edge chars
      if (charsOfText[indexOfChar] == GrammarCharset.TILDE) {
        this.notChars = true;
        indexOfChar++;
        parent.expectOneChar(GrammarCharset.LEFT_BRACKET, charsOfText, indexOfChar);
      }
      // skip [
      ++indexOfChar;
      charsBuilder.delete(0, charsBuilder.length());
      isEndTextRight = false;
      while (indexOfChar < charsOfText.length) {
        char ch = charsOfText[indexOfChar];
        if (ch == GrammarCharset.RIGHT_BRACKET) {
          isEndTextRight = true;
          break;
        }
        if (ch == GrammarCharset.MINUS_SIGN) {
          this.indexOfRangeFlag.add(charsBuilder.length());
        }
        charsBuilder.append(ch);
        ++indexOfChar;
      }
      if (!isEndTextRight) {
        throw new AstRuntimeException(
            String.format("%s: edge is wrong in %s", parent.grammar.name, token.text));
      }
      char[] charsArr = new char[charsBuilder.length()];
      int indexOfCharArr = 0;
      for (char ch : charsBuilder.toString().toCharArray()) {
        charsArr[indexOfCharArr++] = ch;
      }
      this.chars = charsArr;
      // to
      ++indexOfChar; // skip ]
      charsBuilder.delete(0, charsBuilder.length());
      while (indexOfChar < charsOfText.length) {
        char ch = charsOfText[indexOfChar];
        charsBuilder.append(ch);
        ++indexOfChar;
      }
      if (charsBuilder.isEmpty()) {
        throw new AstRuntimeException(
            String.format("%s: edge is wrong in %s", parent.grammar.name, token.text));
      }
      this.to = GrammarCharset.formatEscapeChar2Char(charsBuilder.toString());
    }

    private char[] getCharsOfEdge() {
      StringBuilder charsBuilder;
      StringBuilder formatChars = formatChars(this.chars);
      if (this.indexOfRangeFlag.isEmpty()) {
        charsBuilder = formatChars;
      } else {
        charsBuilder = new StringBuilder();
        char[] text = formatChars.toString().toCharArray();
        Iterator<Integer> indexOfRangeFlagIt = this.indexOfRangeFlag.iterator();
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
        endIndex = text.length - 1;
        buildNormalChars(charsBuilder, text, startIndex, endIndex); // last
      }
      char[] charsOfEdge = charsBuilder.toString().toCharArray();
      if (!this.notChars) {
        return charsOfEdge;
      }
      // not chars 1==chars保留该字符，默认全部保留
      int[] notCharsFlags = new int[GrammarCharset.COUNT_OF_CHARS];
      Arrays.fill(notCharsFlags, 1);
      // 取反的不保留
      for (char ch : charsOfEdge) {
        notCharsFlags[ch] = 0;
      }
      char[] notCharsOfEdge = new char[notCharsFlags.length - charsOfEdge.length];
      int indexOfNotChar = 0;
      for (int ch = 0; ch < notCharsFlags.length; ch++) {
        if (notCharsFlags[ch] == 1) {
          notCharsOfEdge[indexOfNotChar++] = (char) ch;
        }
      }
      return notCharsOfEdge;
    }

    private void buildRangeChars(
        StringBuilder charsBuilder, char[] chars, int startIndexOfChar, int endIndexOfChar) {
      if (startIndexOfChar >= 0
          && endIndexOfChar < chars.length
          && startIndexOfChar + 2 == endIndexOfChar) {
        int minChar = chars[startIndexOfChar];
        int maxChar = chars[endIndexOfChar];
        for (int ch = minChar; ch <= maxChar; ch++) {
          charsBuilder.append((char) ch);
        }
      } else {
        StringBuilder errorInfo = new StringBuilder();
        for (int indexOfChars = startIndexOfChar; indexOfChars < chars.length; indexOfChars++) {
          errorInfo.append(chars[indexOfChars]);
        }
        throw new AstRuntimeException(
            String.format(
                "%s:format like this,'from[min-max]to', 'min-max'error near %s",
                parent.grammar.name, errorInfo));
      }
    }

    private void buildNormalChars(
        StringBuilder charsBuilder, char[] chars, int startIndexOfChar, int endIndexOfChar) {
      if (startIndexOfChar >= 0
          && endIndexOfChar < chars.length
          && startIndexOfChar <= endIndexOfChar) {
        int length = endIndexOfChar - startIndexOfChar + 1;
        for (int indexOfChar = startIndexOfChar; indexOfChar < length; indexOfChar++) {
          charsBuilder.append(chars[indexOfChar]);
        }
      }
    }

    @Override
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      if (null != from) {
        stringBuilder.append(from);
      }
      if (notChars) {
        stringBuilder.append('~');
      }
      if (null != chars) {
        stringBuilder.append("[");
        for (int ch : chars) {
          stringBuilder.append((char) (ch & 0xFF));
        }
        stringBuilder.append("]");
      }
      if (null != to) {
        stringBuilder.append(to);
      }
      return stringBuilder.toString();
    }
  }
}
