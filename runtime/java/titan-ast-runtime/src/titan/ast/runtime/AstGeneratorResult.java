package titan.ast.runtime;

import java.util.List;

/**
 * .
 *
 * @author tian wei jun
 */
public class AstGeneratorResult {
  public final TokensResult tokensResult;
  public final AstResult astResult;

  public AstGeneratorResult(TokensResult tokensResult, AstResult astResult) {
    this.tokensResult = tokensResult;
    this.astResult = astResult;
  }

  public boolean isOk() {
    return tokensResult.isOk() && astResult.isOk();
  }

  public List<Token> getOkTokens() {
    return tokensResult.getOkData();
  }

  public Ast getOkAst() {
    return astResult.getOkData();
  }

  public String getErrorMsg() {
    String errorMsg = "";
    switch (astResult.getType()) {
      case AST_PARSE_ERROR -> {
        errorMsg = astResult.getAstParseErrorData().toString();
      }
      case TOKENS_ERROR -> {
        switch (tokensResult.getType()) {
          case SOURCE_IO_ERROR -> {
            errorMsg = tokensResult.getSourceIoErrorData();
          }
          case TOKEN_PARSE_ERROR -> {
            errorMsg = tokensResult.getTokenParseErrorData().toString();
          }
          default -> {
            throw new IllegalStateException("Unexpected value: " + tokensResult.getType());
          }
        }
      }
    }
    return errorMsg;
  }

  public static class TokensResult {
    private final TokensResultType type;
    private final Object data;

    TokensResult(TokensResultType type, Object data) {
      this.type = type;
      this.data = data;
    }

    public TokensResultType getType() {
      return type;
    }

    public boolean isOk() {
      return type == TokensResultType.OK;
    }

    public static TokensResult generateOkResult(List<Token> data) {
      return new TokensResult(TokensResultType.OK, data);
    }

    public List<Token> getOkData() {
      return (List<Token>) data;
    }

    public static TokensResult generateSourceIoErrorResult(String data) {
      return new TokensResult(TokensResultType.SOURCE_IO_ERROR, data);
    }

    public String getSourceIoErrorData() {
      return (String) data;
    }

    public static TokensResult generateTokenParseErrorResult(TokenParseErrorData data) {
      return new TokensResult(TokensResultType.TOKEN_PARSE_ERROR, data);
    }

    public TokenParseErrorData getTokenParseErrorData() {
      return (TokenParseErrorData) data;
    }
  }

  public enum TokensResultType {
    OK,
    TOKEN_PARSE_ERROR,
    SOURCE_IO_ERROR
  }

  public static class TokenParseErrorData {
    public final List<Token> finishedTokens;
    public final int start;
    public final int end;
    public final String errorText;

    public TokenParseErrorData(List<Token> finishedTokens, int start, int end, String errorText) {
      this.finishedTokens = finishedTokens;
      this.start = start;
      this.end = end;
      this.errorText = errorText;
    }

    @Override
    public String toString() {
      return String.format("generate token error,error near [%d,%d): '%s'", start, end, errorText);
    }
  }

  public static class AstResult {
    private final AstResultType type;
    private final Object data;

    public AstResult(AstResultType type, Object data) {
      this.type = type;
      this.data = data;
    }

    public AstResultType getType() {
      return type;
    }

    public boolean isOk() {
      return type == AstResultType.OK;
    }

    public static AstResult generateOkResult(Ast data) {
      return new AstResult(AstResultType.OK, data);
    }

    public Ast getOkData() {
      return (Ast) data;
    }

    public static AstResult generateAstParseErrorResult(AstParseErrorData data) {
      return new AstResult(AstResultType.AST_PARSE_ERROR, data);
    }

    public AstParseErrorData getAstParseErrorData() {
      return (AstParseErrorData) data;
    }

    public static AstResult generateTokensErrorResult() {
      return new AstResult(AstResultType.TOKENS_ERROR, null);
    }
  }

  public enum AstResultType {
    OK,
    AST_PARSE_ERROR,
    TOKENS_ERROR
  }

  public static class AstParseErrorData {
    public final int start;
    public final int end;
    public final String errorText;

    public AstParseErrorData(int start, int end, String errorText) {
      this.start = start;
      this.end = end;
      this.errorText = errorText;
    }

    @Override
    public String toString() {
      return String.format("generate ast error,error near [%d,%d): '%s'", start, end, errorText);
    }
  }
}
