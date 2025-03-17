package titan.ast.runtime;

import java.util.ArrayList;
import titan.ast.runtime.AstGeneratorResult.AstParseErrorData;

/**
 * ast自动机所识别的token流.
 *
 * @author tian wei jun
 */
class TokenReducingSymbolInputStream {
  int nextReadIndex = 0;
  ArrayList<Token> tokenReducingSymbols;
  ArrayList<Token> sourceTokens;

  TokenReducingSymbolInputStream(ArrayList<Token> sourceTokens) {
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

  Token read() {
    return tokenReducingSymbols.get(nextReadIndex++);
  }

  boolean hasNext() {
    return nextReadIndex >= 0 && nextReadIndex < tokenReducingSymbols.size();
  }

  boolean hasReadAll() {
    return nextReadIndex >= tokenReducingSymbols.size();
  }

  AstParseErrorData getAstParseErrorData(
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
