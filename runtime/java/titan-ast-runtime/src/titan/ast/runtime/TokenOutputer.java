package titan.ast.runtime;

import java.util.Iterator;
import java.util.List;

public class TokenOutputer {

  private final int maxLengthOfLine = 120;

  public void output(List<Token> tokens) {
    if (tokens == null || tokens.isEmpty()) {
      return;
    }
    Iterator<Token> tokensIt = tokens.iterator();
    StringBuilder stringBuilder = new StringBuilder();
    while (tokensIt.hasNext()) {
      Token token = tokensIt.next();
      stringBuilder.append(token.toString()).append(" ");
      if (stringBuilder.length() >= maxLengthOfLine) {
        Logger.info(
            String.format(
                "TokenOutputer output,token:%s", getDisplayString(stringBuilder.toString())));
        stringBuilder.delete(0, stringBuilder.length());
      }
    }
    if (!stringBuilder.isEmpty()) {
      Logger.info(
          String.format(
              "TokenOutputer output,token:%s", getDisplayString(stringBuilder.toString())));
      stringBuilder.delete(0, stringBuilder.length());
    }
  }

  public String getDisplayString(String str) {
    if (null == str) {
      return "";
    }
    StringBuilder stringBuilder = new StringBuilder();
    char[] chars = str.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      char ch = chars[i];
      stringBuilder.append(getDisplayChars(ch));
    }
    return stringBuilder.toString();
  }

  public char[] getDisplayChars(int tchar) {
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
