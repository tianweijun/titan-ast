package titan.ast.runtime;

/**
 * 识别文本（通常为源代码）的token.
 *
 * @author tian wei jun
 */
public class Token {
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
}
