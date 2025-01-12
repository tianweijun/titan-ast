package titan.ast.grammar.io;

/**
 * 语法文件中的token.
 *
 * @author tian wei jun
 */
public class GrammarToken {

  public GrammarTokenType type = GrammarTokenType.TEXT;
  public String text = "";
  public int start = -1;

  public GrammarToken(GrammarTokenType type) {
    this.type = type;
    this.text = type.name();
  }

  public GrammarToken(GrammarTokenType type, String text) {
    this.type = type;
    this.text = text;
  }

  @Override
  public String toString() {
    return null == text ? "" : text;
  }
}
