package titan.ast.runtime;

import java.util.ArrayList;
import titan.ast.runtime.AstGeneratorResult.AstParseErrorData;

/**
 * ast自动机所识别的token流.
 *
 * @author tian wei jun
 */
public class TokenReducingSymbolInputStream {
  public int nextReadIndex = 0;
  public ArrayList<Token> tokenReducingSymbols;
  public ArrayList<Token> sourceTokens;

  public TokenReducingSymbolInputStream(ArrayList<Token> sourceTokens) {
    this.sourceTokens = sourceTokens;
    setTokenReducingSymbols();
  }

  private void setTokenReducingSymbols() {
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

  public AstParseErrorData getAstParseErrorData(
      int startIndexOfTokenReducingSymbols, int endIndexOfTokenReducingSymbols) {
    Token startToken = tokenReducingSymbols.get(startIndexOfTokenReducingSymbols);
    Token endToken = tokenReducingSymbols.get(endIndexOfTokenReducingSymbols);
    int startIndexByte = startToken.start;
    int endIndexByte = endToken.start + endToken.text.length();

    StringBuilder tokenInfo = new StringBuilder(endIndexByte - startIndexByte);
    int indexOfStartSourceToken = 0;
    int indexOfSourceToken = startIndexOfTokenReducingSymbols;
    for (; indexOfSourceToken < sourceTokens.size(); indexOfSourceToken++) {
      Token token = sourceTokens.get(indexOfSourceToken);
      if (token.start == startIndexByte) {
        indexOfStartSourceToken = indexOfSourceToken;
        break;
      }
    }
    for (indexOfSourceToken = indexOfStartSourceToken;
        indexOfSourceToken < sourceTokens.size();
        indexOfSourceToken++) {
      Token token = sourceTokens.get(indexOfSourceToken);
      if (token.start < endIndexByte) {
        tokenInfo.append(token.text);
      } else {
        break;
      }
    }
    return new AstParseErrorData(startIndexByte, endIndexByte, tokenInfo.toString());
  }
}
