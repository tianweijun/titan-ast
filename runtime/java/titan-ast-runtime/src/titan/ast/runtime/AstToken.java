package titan.ast.runtime;

/**
 * 识别文本（通常为源代码）的token.
 *
 * @author tian wei jun
 */
public class AstToken {
  public int start = 0;
  public String text = "";

  public AstToken(int start) {
    this.start = start;
  }

  public AstToken(int start, String text) {
    this.start = start;
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }
}
