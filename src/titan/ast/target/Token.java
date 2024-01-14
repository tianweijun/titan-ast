package titan.ast.target;

import titan.ast.grammar.Grammar;

/**
 * 识别文本（通常为源代码）的token.
 *
 * @author tian wei jun
 */
public class Token implements Comparable<Token> {

  public int start = 0;
  public String text = "";
  public Grammar terminal = null;
  public TokenType type = TokenType.TEXT;

  public Token(int start) {
    this.start = start;
  }

  @Override
  public String toString() {
    return terminal.name + "[" + text + "]";
  }

  @Override
  public int compareTo(Token that) {
    int compare = terminal.compareTo(that.terminal);
    if (0 != compare) {
      return compare;
    }
    if (start != that.start) {
      return start - that.start;
    }
    if (type != that.type) {
      return type.ordinal() - that.type.ordinal();
    }
    return text.compareTo(that.text);
  }
}
