package titan.ast.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * ast自动机所识别的token流.
 *
 * @author tian wei jun
 */
public class TokenReducingSymbolInputStream {
  public int nextReadIndex = 0;
  public ArrayList<Token> tokenReducingSymbols;

  public TokenReducingSymbolInputStream(List<Token> sourceTokens) {
    setTokenReducingSymbols(sourceTokens);
  }

  private void setTokenReducingSymbols(List<Token> sourceTokens) {
    ArrayList<Token> textTokens = new ArrayList<>(sourceTokens.size());
    for (Token token : sourceTokens) {
      if (token.type == TokenType.TEXT) {
        textTokens.add(token);
      }
    }
    this.tokenReducingSymbols = new ArrayList<>(textTokens.size());
    tokenReducingSymbols.addAll(textTokens);
  }

  public Token read() {
    return tokenReducingSymbols.get(nextReadIndex++);
  }

  public boolean hasNext() {
    return nextReadIndex >= 0 && nextReadIndex < tokenReducingSymbols.size();
  }

  public boolean hasReadAll() {
    return nextReadIndex >= tokenReducingSymbols.size();
  }
}
