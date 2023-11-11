package titan.ast.grammar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import titan.ast.AstContext;
import titan.ast.grammar.RegExp.RegExpType;
import titan.ast.grammar.RegExp.RepTimes;
import titan.ast.grammar.io.GrammarCharset;
import titan.ast.grammar.io.GrammarToken;
import titan.ast.runtime.AstRuntimeException;
import titan.ast.util.StringUtils;

/**
 * 嵌套发生的原因，是因为分组，嵌套分组的标志是()。 解析流程： 枚举所有符号 形似于 ~(...)(unit|(unit|(...)))?*+{num,num}?
 * 分组起始开始标志~，其次的标志是([，结束标志是? * + {，对应匹配字符)] num,num}，其他是范围字符。 层序遍历 语法节点：基本正则->内容的正则表达式.
 *
 * @author tian wei jun
 */
public abstract class AbstractRegExpBuilder {

  public Map<String, Grammar> tasks = new HashMap<>();
  public Grammar grammar = null;
  public GrammarCharset grammarCharset = null;
  private Map<String, Grammar> sources = new HashMap<>();
  private Stack<RegExp> regExpStack = new Stack<>();
  private NewRegExpContext newRegExpContext = new NewRegExpContext();
  private char splitCharByTextOfRegExp;

  /** 初始化字段：grammarCharset、splitCharByTextOfRegExp. */
  public AbstractRegExpBuilder() {
    AstContext astContext = AstContext.get();
    grammarCharset = astContext.grammarCharset;
    splitCharByTextOfRegExp = grammarCharset.getGrammarSpace();
  }

  public void addSources(Map<String, Grammar> grammars) {
    sources.putAll(grammars);
  }

  public void addTasks(Map<String, Grammar> grammars) {
    tasks.putAll(grammars);
  }

  /** 构造所有tasks的正则并设置. */
  public void build() {
    for (Grammar grammar : tasks.values()) {
      build(grammar);
    }
  }

  private void build(Grammar grammar) {
    this.grammar = grammar;
    // 创建普通正则
    if (grammar.isNormalRegexpContent()) {
      initRootRegExp(grammar);
      createAndInitRegExps(grammar.regExp);
      postProcessInitRegExp(grammar);
      verifyRegExp(grammar.regExp);
      return;
    }
    // nfaReg不需要创建正则，后面直接建立nfa即可
    if (grammar.isNfaRegexpContent()) {
      grammar.regExp.type = RegExp.RegExpType.NFA;
    }
  }

  private void verifyRegExp(RegExp regExp) {
    // ------repTimes start------
    RepTimes repMinTimes = regExp.repMinTimes;
    RepTimes repMaxTimes = regExp.repMaxTimes;
    if (repMinTimes.lessThan(0)
        || repMaxTimes.lessThan(0)
        || !repMinTimes.lessThanOrEqual(repMaxTimes)) {
      throw new AstRuntimeException(String.format("%s: repMinTimes > repMaxTimes", grammar.name));
    }
    // ------repTimes end------
  }

  public abstract void postProcessInitRegExp(Grammar grammar);

  /**
   * 设置grammar rootRegExp text text之间以空格分隔.
   *
   * @param grammar 当前正在处理的语法
   */
  private void initRootRegExp(Grammar grammar) {
    // regExp
    RegExp rootRegExp = this.grammar.regExp;
    // text
    StringBuilder rootRegExpText = new StringBuilder();
    for (GrammarToken token : grammar.text) {
      rootRegExpText.append(token.text);
      rootRegExpText.append(splitCharByTextOfRegExp);
    }
    rootRegExp.text = rootRegExpText.toString().toCharArray();
    rootRegExp.startOfText = 0;
    rootRegExp.lengthOfText = rootRegExp.text.length;
  }

  /**
   * 设置regexp树形text结构，包含一些辅助正则.
   *
   * @param rootRegExp 语法的直接正则
   */
  private void createAndInitRegExps(RegExp rootRegExp) {
    rootRegExp.type = RegExpType.COMPOSITE;

    char[] text = rootRegExp.text;
    // empty regexp
    if (null == text || text.length <= 0) {
      RegExp.createEmptyRegExp(rootRegExp);
      return;
    }

    this.regExpStack.clear();
    regExpStack.push(rootRegExp);

    // 设置regexp树形text结构的序列，包含一些辅助正则
    int indexOfText = 0;
    while (indexOfText < text.length) {
      indexOfText = createAndInitRegExp(text, indexOfText);
    }
    regExpStack.pop();
  }

  /**
   * ( ->parent [' ->unit regexp | -> helper ?*+{ ? -> number+matchingPattern ~ ->isNot # -> alias
   * other -> grammar # -> alias
   *
   * <p>辅助正则：| -> helper # -> alias.
   *
   * @param text 正则文本
   * @param indexOfText 正则文本的索引
   * @return 处理过后 新的 正则文本的索引
   */
  private int createAndInitRegExp(char[] text, int indexOfText) {
    char tchar = text[indexOfText];
    if (splitCharByTextOfRegExp == tchar) { // skip splitCharByTextOfRegExp
      ++indexOfText;
      return indexOfText;
    }
    switch (tchar) {
      case '(':
        indexOfText = createAndInitCompositeRegExp(text, indexOfText);
        break;
      case '[':
        indexOfText = createAndInitOneCharOptionCharsetUnitRegExp(text, indexOfText);
        break;
      case '\'':
        indexOfText = createAndInitSequenceCharsUnitRegExp(text, indexOfText);
        break;
      case '~':
        // ~ ->isNot
        newRegExpContext.isNotForNewRexExp = true;
        ++indexOfText;
        break;
      case '|':
        indexOfText = createAndInitHelperOrUnitRegExp(text, indexOfText);
        break;
      case '#':
        indexOfText = createAndInitAliasUnitRegExp(text, indexOfText);
        break;
      default:
        // other -> grammar
        indexOfText = createAndInitGrammarUnitRegExp(text, indexOfText);
        break;
    }
    return indexOfText;
  }

  private int createAndInitHelperOrUnitRegExp(char[] text, int indexOfText) {
    // | -> helper
    RegExp regExp = new RegExp(regExpStack.peek(), newRegExpContext.isNotForNewRexExp);
    newRegExpContext.init();
    regExp.text = text;
    regExp.startOfText = indexOfText;
    regExp.type = RegExp.RegExpType.UNIT;
    regExp.unitType = RegExp.RegExpUnitType.HELPER_OR;
    ++indexOfText;
    regExp.lengthOfText = 1;
    return indexOfText;
  }

  private int createAndInitSequenceCharsUnitRegExp(char[] text, int indexOfText) {
    int startIndexOfText = indexOfText;
    // [' ->unit regexp
    RegExp charsRegExpUnit = new RegExp(regExpStack.peek(), newRegExpContext.isNotForNewRexExp);
    newRegExpContext.init();
    charsRegExpUnit.text = text;
    charsRegExpUnit.startOfText = indexOfText;
    regExpStack.push(charsRegExpUnit);
    charsRegExpUnit.type = RegExp.RegExpType.UNIT;
    charsRegExpUnit.setUnitType(RegExp.RegExpUnitType.SEQUENCE_CHARS);
    charsRegExpUnit.relationshipOfChars = RelationshipQualifier.AND;
    ++indexOfText;
    indexOfText = setSetsOfBaseUnitRegExp(text, indexOfText);
    indexOfText = expectOneChar('\'', text, indexOfText);
    indexOfText = setRepTimes(charsRegExpUnit, text, indexOfText);
    charsRegExpUnit.lengthOfText = indexOfText - charsRegExpUnit.startOfText;
    regExpStack.pop();
    if (charsRegExpUnit.sets.isEmpty()) {
      throw new AstRuntimeException(
          String.format(
              "%s:content is empty in regexp,error near %s",
              grammar.name, new String(text, startIndexOfText, text.length - startIndexOfText)));
    }
    return indexOfText;
  }

  private int createAndInitOneCharOptionCharsetUnitRegExp(char[] text, int indexOfText) {
    int startIndexOfText = indexOfText;
    // [' ->unit regexp
    RegExp charsRegExpUnit = new RegExp(regExpStack.peek(), newRegExpContext.isNotForNewRexExp);
    newRegExpContext.init();
    charsRegExpUnit.text = text;
    charsRegExpUnit.startOfText = indexOfText;
    regExpStack.push(charsRegExpUnit);
    charsRegExpUnit.type = RegExp.RegExpType.UNIT;
    charsRegExpUnit.setUnitType(RegExp.RegExpUnitType.ONE_CHAR_OPTION_CHARSET);
    charsRegExpUnit.relationshipOfChars = RelationshipQualifier.OR;
    ++indexOfText;
    indexOfText = setSetsOfBaseUnitRegExp(text, indexOfText);
    indexOfText = expectOneChar(']', text, indexOfText);
    indexOfText = setRepTimes(charsRegExpUnit, text, indexOfText);
    charsRegExpUnit.lengthOfText = indexOfText - charsRegExpUnit.startOfText;
    regExpStack.pop();
    if (charsRegExpUnit.sets.isEmpty()) {
      throw new AstRuntimeException(
          String.format(
              "%s:content is empty in regexp,error near %s",
              grammar.name, new String(text, startIndexOfText, text.length - startIndexOfText)));
    }
    return indexOfText;
  }

  private int createAndInitCompositeRegExp(char[] text, int indexOfText) {
    int startIndexOfText = indexOfText;
    // ( ->parent
    RegExp compositeRegExp = new RegExp(regExpStack.peek(), newRegExpContext.isNotForNewRexExp);
    newRegExpContext.init();
    compositeRegExp.text = text;
    compositeRegExp.startOfText = indexOfText;
    regExpStack.push(compositeRegExp);
    ++indexOfText;
    while (indexOfText < text.length) {
      char tchar = text[indexOfText];
      if (tchar == ')') {
        break;
      }
      indexOfText = createAndInitRegExp(text, indexOfText);
    }
    indexOfText = expectOneChar(')', text, indexOfText);
    indexOfText = setRepTimes(compositeRegExp, text, indexOfText);
    compositeRegExp.lengthOfText = indexOfText - compositeRegExp.startOfText;
    regExpStack.pop();
    if (compositeRegExp.children.isEmpty()) {
      throw new AstRuntimeException(
          String.format(
              "%s:children is empty in composite regexp,error near %s",
              grammar.name, new String(text, startIndexOfText, text.length - startIndexOfText)));
    }
    return indexOfText;
  }

  private int setRepTimes(RegExp regExp, char[] text, int indexOfText) {
    if (indexOfText < text.length) {
      switch (text[indexOfText]) {
        case '?':
          // ?*+{ ?  ->  number+matchingPattern
          regExp.repMinTimes.setTimes(0);
          regExp.repMaxTimes.setTimes(1);
          ++indexOfText;
          indexOfText = setMatchingPattern(regExp, text, indexOfText);
          break;
        case '*':
          // ?*+{ ?  ->  number+matchingPattern
          regExp.repMinTimes.setTimes(0);
          regExp.repMaxTimes.setInfinity();
          ++indexOfText;
          indexOfText = setMatchingPattern(regExp, text, indexOfText);
          break;
        case '+':
          // ?*+{ ?  ->  number+matchingPattern
          regExp.repMinTimes.setTimes(1);
          regExp.repMaxTimes.setInfinity();
          ++indexOfText;
          indexOfText = setMatchingPattern(regExp, text, indexOfText);
          break;
        case '{':
          // ?*+{ ?  ->  number+matchingPattern
          indexOfText = setRegExpTimesByOpenBrace(regExp, text, indexOfText);
          indexOfText = setMatchingPattern(regExp, text, indexOfText);
          break;
        default:
      }
    }
    return indexOfText;
  }

  /**
   * 量词后接?表示贪婪，后接+表示非贪婪，默认独占.
   *
   * @param regExp 设置匹配模式的正则
   * @param text 正则文本
   * @param indexOfText 正则文本的索引
   * @return 处理过后 新的 正则文本的索引
   */
  private int setMatchingPattern(RegExp regExp, char[] text, int indexOfText) {
    if (indexOfText < text.length) {
      char c = text[indexOfText];
      if (c == '?') {
        regExp.matchingPattern = RegExp.MatchingPattern.UNBACKTRACKING_GREEDINESS;
        ++indexOfText;
      } else if (c == '+') {
        regExp.matchingPattern = RegExp.MatchingPattern.UNBACKTRACKING_GREEDINESS;
        ++indexOfText;
      } else {
        regExp.matchingPattern = RegExp.MatchingPattern.UNBACKTRACKING_GREEDINESS;
      }
    }
    return indexOfText;
  }

  private int createAndInitAliasUnitRegExp(char[] text, int indexOfText) {
    int startIndexOfText = indexOfText;
    ++indexOfText; // skip #
    RegExp aliasRegExp = new RegExp(regExpStack.peek(), newRegExpContext.isNotForNewRexExp);
    newRegExpContext.init();
    aliasRegExp.text = text;
    aliasRegExp.startOfText = indexOfText;
    StringBuilder stringBuilder = new StringBuilder();
    while (indexOfText < text.length) {
      char tchar = text[indexOfText];
      if (tchar == splitCharByTextOfRegExp) { // 后缀为分隔符
        ++indexOfText;
        break;
      }
      stringBuilder.append(tchar);
      ++indexOfText;
    }
    if (stringBuilder.length() <= 0) {
      throw new AstRuntimeException(
          String.format(
              "%s:alias is empty,error near %s",
              grammar.name, new String(text, startIndexOfText, text.length - startIndexOfText)));
    }
    aliasRegExp.setUnitType(RegExp.RegExpUnitType.HELPER_ALIAS);
    RegExp.RegExpCharSet aliasRegExpSet = new RegExp.RegExpCharSet();
    aliasRegExpSet.type = RegExp.RegExpCharSetType.HELPER_ALIAS;
    aliasRegExpSet.chars =
        grammarCharset.formatEscapeChar2Char(stringBuilder.toString()).toCharArray();
    aliasRegExp.sets.add(aliasRegExpSet);
    aliasRegExp.lengthOfText = indexOfText - aliasRegExp.startOfText;
    return indexOfText;
  }

  private int createAndInitGrammarUnitRegExp(char[] text, int indexOfText) {
    int startIndexOfText = indexOfText;
    RegExp grammarRegExp = new RegExp(regExpStack.peek(), newRegExpContext.isNotForNewRexExp);
    newRegExpContext.init();
    grammarRegExp.text = text;
    grammarRegExp.startOfText = indexOfText;

    StringBuilder stringBuilder = new StringBuilder();
    while (indexOfText < text.length) {
      char tchar = text[indexOfText];
      if (tchar == splitCharByTextOfRegExp) {
        ++indexOfText;
        break;
      }
      if (isPostfixOfRegExp(tchar)) {
        break;
      }
      stringBuilder.append(tchar);
      ++indexOfText;
    }
    String grammarName = stringBuilder.toString();
    if (StringUtils.isBlank(grammarName)) {
      throw new AstRuntimeException(
          String.format(
              "%s:grammarName is empty,error near %s",
              grammar.name, new String(text, startIndexOfText, text.length - startIndexOfText)));
    }
    grammarName = grammarCharset.formatEscapeChar2Char(grammarName);
    Grammar grammar = sources.get(grammarName);
    if (null == grammar) {
      throw new AstRuntimeException(
          String.format(
              "%s:%s is not exits",
              this.grammar.name,
              new String(text, startIndexOfText, text.length - startIndexOfText)));
    }
    grammarRegExp.setUnitType(RegExp.RegExpUnitType.GRAMMAR);
    RegExp.RegExpCharSet grammarRegExpSet = new RegExp.RegExpCharSet();
    grammarRegExpSet.type = RegExp.RegExpCharSetType.GRAMMAR;
    grammarRegExpSet.grammar = grammar;
    grammarRegExp.sets.add(grammarRegExpSet);
    indexOfText = setRepTimes(grammarRegExp, text, indexOfText);
    grammarRegExp.lengthOfText = indexOfText - grammarRegExp.startOfText;
    return indexOfText;
  }

  private boolean isPostfixOfRegExp(char tchar) {
    if (tchar == splitCharByTextOfRegExp) {
      return true;
    }
    boolean isPostfixOfRegExp = false;
    switch (tchar) {
      case '?': // repeat times
      case '*':
      case '+':
      case '{':
      case ')': // postfix of regexp
      case ']':
      case '\'':
      case '~': // prefix of regexp
      case '(':
      case '[': // case '\'':
        isPostfixOfRegExp = true;
        break;
      default:
    }
    return isPostfixOfRegExp;
  }

  /**
   * {min,max},min和max为空是表示无穷 {}=={,}=={infinity,infinity}=={1,infinity},{num}={num,num}.
   *
   * @param currentRegExp 与设置次数相关的正则
   * @param text 正则文本
   * @param indexOfText 当前正则文本索引的内容是{
   * @return 处理后 新的 正则文本
   */
  private int setRegExpTimesByOpenBrace(RegExp currentRegExp, char[] text, int indexOfText) {
    int startIndexOfText = indexOfText;
    currentRegExp.repMinTimes.setInfinity();
    currentRegExp.repMaxTimes.setInfinity();
    // skip {
    ++indexOfText;
    // -----min times start-------
    StringBuilder minTimes = new StringBuilder();
    char tchar = text[indexOfText];
    while (indexOfText < text.length) {
      if (tchar == ',' || tchar == '}') {
        break;
      }
      minTimes.append(tchar);
      ++indexOfText;
    }
    if (minTimes.length() > 0) {
      currentRegExp.repMinTimes.setTimes(Integer.getInteger(minTimes.toString()));
    }
    // -----min times end-------
    // -----max times start-------
    // repMaxTimes
    if (tchar == '}') {
      if (minTimes.length() > 0) {
        currentRegExp.repMaxTimes.setTimes(currentRegExp.repMinTimes);
      }
      // skip }
      ++indexOfText;
    } else if (tchar == ',') {
      // skip ,
      ++indexOfText;
      tchar = text[indexOfText];
      // max times
      StringBuilder maxTimes = new StringBuilder();
      while (indexOfText < text.length) {
        if (tchar == '}') {
          break;
        }
        maxTimes.append(tchar);
        ++indexOfText;
      }
      if (tchar != '}') {
        throw new AstRuntimeException(
            String.format(
                "%s:{min,max} is not right,error near %s",
                grammar.name, new String(text, startIndexOfText, text.length - startIndexOfText)));
      }
      if (maxTimes.length() > 0) {
        currentRegExp.repMaxTimes.setTimes(Integer.getInteger(maxTimes.toString()));
      }
      // skip }
      ++indexOfText;
    } else {
      throw new AstRuntimeException(
          String.format(
              "%s:{min,max} is not right,error near %s",
              grammar.name, new String(text, startIndexOfText, text.length - startIndexOfText)));
    }
    // -----max times end-------
    return indexOfText;
  }

  /**
   * ''[]中的char.
   *
   * @param text 正则文本
   * @param indexOfText 正则文本的索引
   * @return 处理后 新的 正则文本的索引
   */
  private int setSetsOfBaseUnitRegExp(char[] text, int indexOfText) {
    int startIndexOfText = indexOfText;
    RegExp.RegExpUnitType unitType = regExpStack.peek().unitType;
    CreateSetsOfCharsRegExpUnitDescriptor descriptor = new CreateSetsOfCharsRegExpUnitDescriptor();
    // set chars
    StringBuilder stringBuilder = new StringBuilder();
    boolean shouldBreakBuildingCharSets = false;
    while (indexOfText < text.length) {
      char tchar = text[indexOfText];
      if ('\\' == tchar) { // 转义字符 基本单元正则内容中\后面必须跟一个能正确表示转义的字符
        ++indexOfText;
        if (indexOfText >= text.length) {
          throw new AstRuntimeException(
              String.format(
                  "%s:expect escape char,error near %s",
                  grammar.name,
                  new String(text, startIndexOfText, text.length - startIndexOfText)));
        }
        indexOfText = setEscapeChar(text, indexOfText, stringBuilder);
      } else { // 常规字符
        switch (unitType) { // 结束字符
          case SEQUENCE_CHARS:
            if (tchar == '\'') {
              shouldBreakBuildingCharSets = true;
            }
            break;
          case ONE_CHAR_OPTION_CHARSET:
            if (tchar == ']') {
              shouldBreakBuildingCharSets = true;
            }
            break;
          default:
        }
        if (shouldBreakBuildingCharSets) {
          break;
        }

        if (tchar == '-'
            && RegExp.RegExpUnitType.ONE_CHAR_OPTION_CHARSET == unitType) { // range [xx-xx]
          stringBuilder.append(tchar);
          ++indexOfText;
          descriptor.indexsOfRangeFlag.add(stringBuilder.length() - 1);
        } else {
          stringBuilder.append(tchar);
          ++indexOfText;
        }
      }
    }
    if (!shouldBreakBuildingCharSets) {
      throw new AstRuntimeException(
          String.format(
              "%s:error in '%s',error near %s",
              grammar.name,
              new String(regExpStack.peek().text),
              new String(text, startIndexOfText, text.length - startIndexOfText)));
    }
    descriptor.chars = stringBuilder.toString().toCharArray();
    buildCharsRegExpUnitChars(descriptor);

    return indexOfText;
  }

  private void buildCharsRegExpUnitChars(CreateSetsOfCharsRegExpUnitDescriptor descriptor) {
    char[] chars = descriptor.chars;
    if (null == chars || chars.length <= 0) {
      return;
    }
    RegExp.RegExpUnitType unitType = regExpStack.peek().unitType;
    switch (unitType) {
      case SEQUENCE_CHARS:
        buildCharsRegExpUnitSequenceChars(descriptor);
        break;
      case ONE_CHAR_OPTION_CHARSET:
        buildCharsRegExpUnitOptionChars(descriptor);
        break;
      default:
    }
  }

  /**
   * 处理-.
   *
   * @param descriptor 没有转义字符
   */
  private void buildCharsRegExpUnitOptionChars(CreateSetsOfCharsRegExpUnitDescriptor descriptor) {
    char[] chars = descriptor.chars;
    if (descriptor.indexsOfRangeFlag.isEmpty()) {
      buildCharsRegExpUnitOptionCharSet(chars, 0, chars.length - 1);
    } else {
      int start = 0;
      int end = 0;
      for (Integer indexOfRangeFlag : descriptor.indexsOfRangeFlag) {
        end = indexOfRangeFlag - 2;
        buildCharsRegExpUnitOptionCharSet(chars, start, end); // before

        start = indexOfRangeFlag - 1;
        end = indexOfRangeFlag + 1;
        buildCharsRegExpUnitOptionRangeSet(chars, start, end); // range

        start = end + 1;
      }

      end = chars.length - 1;
      buildCharsRegExpUnitOptionCharSet(chars, start, end); // last
    }
  }

  private void buildCharsRegExpUnitOptionRangeSet(
      char[] chars, int startIndexOfChar, int endIndexOfChar) {
    if (startIndexOfChar >= 0
        && endIndexOfChar < chars.length
        && startIndexOfChar + 2 == endIndexOfChar) {
      RegExp.RegExpCharSet charSet = new RegExp.RegExpCharSet();
      charSet.type = RegExp.RegExpCharSetType.ONE_CHAR_OPTION_RANGE;

      charSet.minChar = chars[startIndexOfChar];
      charSet.maxChar = chars[endIndexOfChar];

      regExpStack.peek().sets.add(charSet);
    }
  }

  private void buildCharsRegExpUnitOptionCharSet(
      char[] chars, int startIndexOfChar, int endIndexOfChar) {
    if (startIndexOfChar >= 0
        && endIndexOfChar < chars.length
        && startIndexOfChar <= endIndexOfChar) {
      RegExp.RegExpCharSet charSet = new RegExp.RegExpCharSet();
      charSet.type = RegExp.RegExpCharSetType.ONE_CHAR_OPTION_CHARS;

      int length = endIndexOfChar - startIndexOfChar + 1;
      char[] textChars = new char[length];
      System.arraycopy(chars, startIndexOfChar, textChars, 0, length);
      charSet.chars = textChars;

      regExpStack.peek().sets.add(charSet);
    }
  }

  /**
   * 设置''、[]的真正的字符，将转义字符转为其对应的字符.
   *
   * @param descriptor 没有转义字符
   */
  private void buildCharsRegExpUnitSequenceChars(CreateSetsOfCharsRegExpUnitDescriptor descriptor) {
    char[] chars = descriptor.chars;
    RegExp.RegExpCharSet charSet = new RegExp.RegExpCharSet();
    charSet.type = RegExp.RegExpCharSetType.SEQUENCE_CHARS;

    char[] sequenceChars = new char[chars.length];
    System.arraycopy(chars, 0, sequenceChars, 0, chars.length);
    charSet.chars = sequenceChars;

    regExpStack.peek().sets.add(charSet);
  }

  /**
   * \\已经被识别，\\后的text必须要能正确表示一个转义字符，将转义字符转为其对应的真正字符，设置到sb容器.
   *
   * @param text 正则文本
   * @param indexOfText 正则文本的 索引
   * @param stringBuilder 接收转义字符其对应的真正字符的 容器
   * @return 处理后 新的 正则文本的 索引
   */
  private int setEscapeChar(char[] text, int indexOfText, StringBuilder stringBuilder) {
    int startIndexOfText = indexOfText;
    int newIndexByNormalEscapeChar =
        grammarCharset.formatEscapeChar2CharAndSet(text, indexOfText, stringBuilder);
    if (newIndexByNormalEscapeChar > indexOfText) {
      return newIndexByNormalEscapeChar;
    }
    // special char for regexp
    int intSpecialChar = getIntByRegExpEscapeChar(text[indexOfText]);
    if (intSpecialChar >= 0) {
      stringBuilder.append((char) intSpecialChar);
      ++indexOfText;
      return indexOfText;
    }
    // other
    throw new AstRuntimeException(
        String.format(
            "%s:expect escape char,error in '\\%c',error near %s",
            grammar.name,
            text[indexOfText],
            new String(text, startIndexOfText, text.length - startIndexOfText)));
  }

  /**
   * 正则特殊转义：~ 正则重复次数特殊符转义：? * + { } 正则组特殊字符：() [] ''. 默认已有\\，根据后一个字符ch找到对应真正的字符.
   *
   * @param ch '\\ch' 表示一个转义字符
   * @return '\\ch'对应的 对应真正的字符，-1表示'\\ch'不是转义字符
   */
  private int getIntByRegExpEscapeChar(char ch) {
    int res = -1;
    switch (ch) {
      case 's':
        res = grammarCharset.getGrammarSpace();
        break;
      case '-':
        res = '-';
        break;
      case '~':
        res = '~';
        break;
      case '?':
        res = '?';
        break;
      case '*':
        res = '*';
        break;
      case '+':
        res = '+';
        break;
      case '{':
        res = '{';
        break;
      case '}':
        res = '}';
        break;
      case '(':
        res = '(';
        break;
      case ')':
        res = ')';
        break;
      case '[':
        res = '[';
        break;
      case ']':
        res = ']';
        break;
      case '\'':
        res = '\'';
        break;
      default:
    }
    return res;
  }

  private boolean isRegExpKeywordChar(char ch) {
    boolean isRegExpKeywordChar = false;
    switch (ch) {
      case '-':
      case '~':
      case '?':
        break;
      case '*':
      case '+':
        break;
      case '{':
      case '}':
      case '(':
      case ')':
      case '[':
      case ']':
      case '\'':
        isRegExpKeywordChar = true;
        break;
      default:
    }
    return isRegExpKeywordChar;
  }

  private int expectOneChar(char ch, char[] text, int indexOfText) {
    if (indexOfText >= text.length || text[indexOfText] != ch) {
      throw new AstRuntimeException(
          String.format(
              "%s: expect a char '%c',error near %s",
              grammar.name, ch, new String(text, indexOfText, text.length - indexOfText)));
    }
    return ++indexOfText;
  }

  /**
   * 或关系.
   *
   * @param regExp 正则
   */
  protected void buildOrRelationship(RegExp regExp) {
    if (regExp.type == RegExp.RegExpType.COMPOSITE) {
      for (RegExp child : regExp.children) {
        if (child.type == RegExp.RegExpType.COMPOSITE) {
          buildOrRelationship(child);
        }
      }
      buildOrRelationshipOfChildren(regExp);
    }
  }

  /**
   * 按照|分割成多个孩子们 孩子们组成新的一个孩子作为原来的孩子的替换.
   *
   * @param regExp 正则
   */
  private void buildOrRelationshipOfChildren(RegExp regExp) {
    LinkedList<LinkedList<RegExp>> childRegExps = new LinkedList<>();
    Iterator<RegExp> childrenIt = regExp.children.iterator();
    LinkedList<RegExp> elementrChildern = new LinkedList<>();
    while (childrenIt.hasNext()) {
      RegExp child = childrenIt.next();
      if (child.isHelperOrRegExpUnit()) {
        if (elementrChildern.isEmpty()) {
          RegExp emptyRegExp = RegExp.createEmptyRegExp(null);
          elementrChildern.add(emptyRegExp);
        }
        childRegExps.add(elementrChildern);
        elementrChildern = new LinkedList<>();
      } else {
        elementrChildern.add(child);
      }
    }
    // 无or
    if (childRegExps.isEmpty()) {
      return;
    }
    // 有or
    if (elementrChildern.isEmpty()) {
      RegExp emptyRegExp = RegExp.createEmptyRegExp(null);
      elementrChildern.add(emptyRegExp);
    }
    childRegExps.add(elementrChildern);

    regExp.relationshipOfChildren = RelationshipQualifier.OR;
    LinkedList<RegExp> newChildren = new LinkedList<>();
    for (LinkedList<RegExp> eleChildRegExps : childRegExps) {
      RegExp newChild = new RegExp(regExp);
      // 设置层级关系
      for (RegExp eleChildRegExp : eleChildRegExps) {
        newChild.children.add(eleChildRegExp);
        eleChildRegExp.parent = newChild;
      }
      // 设置text
      RegExp newChildFirstChild = eleChildRegExps.getFirst();
      RegExp newChildLastChild = eleChildRegExps.getLast();
      newChild.text = newChildFirstChild.text;
      newChild.startOfText = newChildFirstChild.startOfText;
      newChild.lengthOfText =
          newChildLastChild.startOfText
              + newChildLastChild.lengthOfText
              - newChildFirstChild.startOfText;
      // 新的or关系正则建立
      newChildren.add(newChild);
    }
    regExp.children = newChildren;
  }

  public static class NewRegExpContext {

    boolean isNotForNewRexExp = false;

    public void init() {
      isNotForNewRexExp = false;
    }
  }

  public static class CreateSetsOfCharsRegExpUnitDescriptor {

    public char[] chars;
    LinkedList<Integer> indexsOfRangeFlag = new LinkedList<>();
  }
}
