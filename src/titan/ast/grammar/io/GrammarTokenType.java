package titan.ast.grammar.io;

/**
 * 语法文件所对应的token的类型.
 *
 * @author tian wei jun
 */
public enum GrammarTokenType {
  WORD_SPACE,
  TEXT,
  NEWLINE,
  COMMENTS_TEXT;

  /**
   * 判断是不是需要略过的GrammarToken类型.
   *
   * @param type GrammarToken的类型
   * @return 是不是需要被忽略的GrammarToken的类型, 是的话返回true，否则false
   */
  public static boolean isSkip(GrammarTokenType type) {
    boolean isSkip = false;
    switch (type) {
      case WORD_SPACE:
      case NEWLINE:
      case COMMENTS_TEXT:
        isSkip = true;
        break;
      default:
    }
    return isSkip;
  }

  /**
   * 判断是不是分隔符的GrammarToken类型.
   *
   * @param type GrammarToken的类型
   * @return 是不是分隔符的GrammarToken的类型, 是的话返回true，否则false
   */
  public static boolean isTokenSeparator(GrammarTokenType type) {
    boolean isSeparator = false;
    switch (type) {
      case WORD_SPACE:
      case NEWLINE:
        isSeparator = true;
        break;
      default:
    }
    return isSeparator;
  }
}
