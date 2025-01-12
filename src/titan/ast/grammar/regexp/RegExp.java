package titan.ast.grammar.regexp;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.io.GrammarCharset;
import titan.ast.grammar.io.GrammarCharset.GetEscapeCharFuncResult;
import titan.ast.grammar.syntax.SyntaxNfa;
import titan.ast.grammar.token.TokenNfa;

/**
 * 语法文件中所描述语法的文本形式.
 *
 * @author tian wei jun
 */
public class RegExp {

  public RegExp parent = null;

  public RegExpType type = RegExpType.COMPOSITE;

  public boolean isNot = false;

  // text
  public char[] text = null;
  public int startOfText = 0;
  public int lengthOfText = 0;

  // -----------COMPOSITE  start----------
  public LinkedList<RegExp> children = new LinkedList<>();
  public RelationshipQualifier relationshipOfChildren = RelationshipQualifier.AND;
  // -----------COMPOSITE  end----------

  // -----------UNIT  start----------
  public RegExpUnitType unitType = RegExpUnitType.EMPTY;
  public LinkedList<RegExpCharSet> sets = new LinkedList<>();
  // -----------UNIT  end------------

  public RepTimes repMinTimes = new RepTimes(RepTimesType.NUMBER, 1);
  public RepTimes repMaxTimes = new RepTimes(RepTimesType.NUMBER, 1);
  public MatchingPattern matchingPattern = MatchingPattern.UNBACKTRACKING_GREEDINESS;

  // TokenNFA
  public TokenNfa tokenNfa = null;
  // SyntaxNFA
  public SyntaxNfa syntaxNfa = null;

  public RegExp() {}

  public RegExp(RegExp parent) {
    this(parent, false);
  }

  /**
   * 带参数初始化，parent会添加自身.
   *
   * @param parent 上一级正则
   * @param isNot 是否含有非运算
   */
  public RegExp(RegExp parent, boolean isNot) {
    this.parent = parent;
    this.isNot = isNot;
    if (parent != null) {
      parent.children.add(this);
    }
  }

  public static RegExp createEmptyRegExp(RegExp parent) {
    RegExp emptyUnitRegExp = new RegExp();
    emptyUnitRegExp.type = RegExpType.UNIT;
    emptyUnitRegExp.unitType = RegExpUnitType.EMPTY;
    if (null != parent) {
      emptyUnitRegExp.parent = parent;
      parent.children.add(emptyUnitRegExp);
    }
    return emptyUnitRegExp;
  }

  public static GetEscapeCharFuncResult getEscapeChar(char[] text, int indexOfText) {
    if (indexOfText >= text.length || indexOfText < 0) {
      return GetEscapeCharFuncResult.fail();
    }
    GetEscapeCharFuncResult escapeChar = GrammarCharset.getEscapeChar(text, indexOfText);
    if (escapeChar.isOk) {
      return escapeChar;
    }
    int intByRegExpEscapeChar = RegExp.getIntByRegExpEscapeChar(text[indexOfText]);
    if (intByRegExpEscapeChar >= 0) {
      return GetEscapeCharFuncResult.ok((char) (intByRegExpEscapeChar & 0xFF), indexOfText + 1);
    }

    return GetEscapeCharFuncResult.fail();
  }

  /**
   * \\已经被识别，\\后的text必须要能正确表示一个转义字符，将转义字符转为其对应的真正字符，设置到sb容器.
   *
   * @param text 正则文本
   * @param indexOfText 正则文本的 索引
   * @param stringBuilder 接收转义字符其对应的真正字符的 容器
   * @return 处理后 新的 正则文本的 索引
   */
  public static int formatEscapeChar2CharAndSet(
      char[] text, int indexOfText, StringBuilder stringBuilder) {
    GetEscapeCharFuncResult escapeChar = getEscapeChar(text, indexOfText);
    if (!escapeChar.isOk) {
      return indexOfText;
    }
    stringBuilder.append(escapeChar.ch);
    return escapeChar.nextIndex;
  }

  /**
   * 正则特殊转义：~ 正则重复次数特殊符转义：? * + { } 正则组特殊字符：() [] ''. 默认已有\\，根据后一个字符ch找到对应真正的字符.
   *
   * @param ch '\\ch' 表示一个转义字符
   * @return '\\ch'对应的 对应真正的字符，-1表示'\\ch'不是转义字符
   */
  public static int getIntByRegExpEscapeChar(char ch) {
    int res = -1;
    switch (ch) {
      case 's':
        res = GrammarCharset.SPACE;
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

  /**
   * 去掉别名.
   *
   * @param regExp 正则
   */
  public static void deleteAlias(RegExp regExp) {
    switch (regExp.type) {
      case UNIT:
        if (regExp.unitType == RegExp.RegExpUnitType.HELPER_ALIAS) {
          regExp.parent.children.remove(regExp);
          regExp.parent = null;
        }
        break;
      case COMPOSITE:
        Iterator<RegExp> childrenIt = regExp.children.iterator();
        while (childrenIt.hasNext()) {
          RegExp child = childrenIt.next();
          switch (child.type) {
            case COMPOSITE:
              deleteAlias(child);
              break;
            case UNIT:
              if (child.unitType == RegExp.RegExpUnitType.HELPER_ALIAS) {
                childrenIt.remove();
                child.parent = null;
              }
              break;
            default:
          }
        }
        break;
      default:
    }
  }

  public void setUnitType(RegExpUnitType unitType) {
    this.type = RegExpType.UNIT;
    this.unitType = unitType;
  }

  public boolean isEmpty() {
    return type == RegExpType.UNIT && unitType == RegExpUnitType.EMPTY;
  }

  public boolean isHelperOrRegExpUnit() {
    return type == RegExpType.UNIT && unitType == RegExpUnitType.HELPER_OR;
  }

  public boolean isHelperAliasRegExpUnit() {
    return type == RegExpType.UNIT && unitType == RegExpUnitType.HELPER_ALIAS;
  }

  /**
   * 忽略的字段：parent、text、startOfText、lengthOfText、tokenNFA、syntaxNFA.
   *
   * @param o 被比较是否相等的对象
   * @return 是否相等，是的话为true否则false
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegExp regExp = (RegExp) o;
    return isNot == regExp.isNot
        && type == regExp.type
        && repMinTimes.equals(regExp.repMinTimes)
        && repMaxTimes.equals(regExp.repMaxTimes)
        && matchingPattern == regExp.matchingPattern
        && children.equals(regExp.children)
        && relationshipOfChildren == regExp.relationshipOfChildren
        && unitType == regExp.unitType
        && sets.equals(regExp.sets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        type,
        isNot,
        repMinTimes,
        repMaxTimes,
        matchingPattern,
        children,
        relationshipOfChildren,
        unitType,
        sets);
  }

  @Override
  public String toString() {
    if (text == null || text.length <= 0) {
      return "";
    }
    return new String(text, startOfText, lengthOfText);
  }

  public enum RepTimesType {
    NUMBER,
    INFINITY
  }

  public enum RegExpType {
    COMPOSITE,
    UNIT,
    NFA
  }

  public enum RegExpUnitType {
    EMPTY,
    GRAMMAR,
    SEQUENCE_CHARS,
    ONE_CHAR_OPTION_CHARSET,
    //
    HELPER_OR,
    HELPER_ALIAS
  }

  public enum RegExpCharSetType {
    // ONE_CHAR_OPTION_CHARSET
    ONE_CHAR_OPTION_RANGE,
    ONE_CHAR_OPTION_CHARS,
    // SEQUENCE_CHARS
    SEQUENCE_CHARS,
    // GRAMMAR
    GRAMMAR,
    HELPER_ALIAS
  }

  public enum MatchingPattern {
    UNBACKTRACKING_GREEDINESS
  }

  public static class RepTimes implements Cloneable {

    public RepTimesType type; // num
    public int times;

    public RepTimes(RepTimesType type, int times) {
      this.type = type;
      this.times = times;
    }

    public void setTimes(RepTimes repTimes) {
      this.type = repTimes.type;
      this.times = repTimes.times;
    }

    public void setTimes(int times) {
      this.type = RepTimesType.NUMBER;
      this.times = times;
    }

    public void setInfinity() {
      this.type = RepTimesType.INFINITY;
      this.times = 0;
    }

    public boolean isZeroTimes() {
      return type == RepTimesType.NUMBER && 0 == times;
    }

    public boolean isInfinityTimes() {
      return type == RepTimesType.INFINITY;
    }

    public boolean lessThan(int otherRepTimes) {
      if (this.isInfinityTimes()) { // INFINITY>=any
        return false;
      }
      return this.times < otherRepTimes;
    }

    public boolean lessThanOrEqual(RepTimes otherRepTimes) {
      if (otherRepTimes.isInfinityTimes()) { // any<=INFINITY
        return true;
      }
      if (this.isInfinityTimes()) { // INFINITY>=any
        return false;
      }
      return this.times <= otherRepTimes.times;
    }

    public boolean isNumberTimesAndGreatThanOrEqual(int otherTimes) {
      return type == RepTimesType.NUMBER && this.times >= otherTimes;
    }

    public boolean isNumberTimesAndEqual(int otherTimes) {
      return type == RepTimesType.NUMBER && this.times == otherTimes;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      RepTimes repTimes = (RepTimes) o;

      if (times != repTimes.times) {
        return false;
      }
      return type == repTimes.type;
    }

    @Override
    public int hashCode() {
      int result = type != null ? type.hashCode() : 0;
      result = 31 * result + times;
      return result;
    }

    public RepTimes clone() {
      RepTimes copy = null;
      try {
        copy = (RepTimes) super.clone();
      } catch (CloneNotSupportedException e) {
        throw new AstRuntimeException(e);
      }
      copy.type = this.type;
      copy.times = this.times;
      return copy;
    }
  }

  public static class RegExpCharSet implements Cloneable {

    // -----------sets start----------
    public RegExpCharSetType type = RegExpCharSetType.SEQUENCE_CHARS;
    // type==ONE_CHAR_OPTION_RANGE
    public char minChar = 0;
    public char maxChar = 0;
    // type==ONE_CHAR_OPTION_CHARS || type==SEQUENCE_CHARS || HELPER_ALIAS
    public char[] chars = null;
    // type==grammar
    public Grammar grammar = null;

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      RegExpCharSet set = (RegExpCharSet) o;

      if (minChar != set.minChar) {
        return false;
      }
      if (maxChar != set.maxChar) {
        return false;
      }
      if (type != set.type) {
        return false;
      }
      if (!Arrays.equals(chars, set.chars)) {
        return false;
      }
      return Objects.equals(grammar, set.grammar);
    }

    @Override
    public int hashCode() {
      int result = type.hashCode();
      result = 31 * result + (int) minChar;
      result = 31 * result + (int) maxChar;
      result = 31 * result + Arrays.hashCode(chars);
      result = 31 * result + (grammar != null ? grammar.hashCode() : 0);
      return result;
    }

    /**
     * grammar字段是浅拷贝,就是grammar字段是浅拷贝.
     *
     * @return 和自己一样的对象
     */
    public RegExpCharSet clone() {
      RegExpCharSet copy = null;
      try {
        copy = (RegExpCharSet) super.clone();
      } catch (CloneNotSupportedException e) {
        throw new AstRuntimeException(e);
      }
      copy.type = this.type;
      copy.minChar = this.minChar;
      copy.maxChar = this.maxChar;
      if (this.chars != null) {
        copy.chars = new char[this.chars.length];
        System.arraycopy(this.chars, 0, copy.chars, 0, this.chars.length);
      }
      copy.grammar = this.grammar;
      return copy;
    }
  }
}
