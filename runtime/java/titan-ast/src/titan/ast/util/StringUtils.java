package titan.ast.util;

/**
 * StringUtils.
 *
 * @author tian wei jun
 */
public class StringUtils {

  public static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }

  public static boolean isNotEmpty(String str) {
    return !StringUtils.isEmpty(str);
  }

  /**
   * 判断字符串是不是空白符.
   *
   * @param str 字符串
   * @return 字符串是不是空白符,是的话返回true否则false
   */
  public static boolean isBlank(String str) {
    int strLen;
    if (str == null || (strLen = str.length()) == 0) {
      return true;
    }
    for (int indexOfStr = 0; indexOfStr < strLen; indexOfStr++) {
      char ch = str.charAt(indexOfStr);
      if (!Character.isWhitespace(ch)) {
        return false;
      }
    }
    return true;
  }

  public static boolean isNotBlank(String str) {
    return !StringUtils.isBlank(str);
  }
}
