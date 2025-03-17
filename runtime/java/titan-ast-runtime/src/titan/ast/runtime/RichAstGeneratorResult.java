package titan.ast.runtime;

import java.util.ArrayList;

/**
 * .
 *
 * @author tian wei jun
 */
public class RichAstGeneratorResult {
  public final RichTokensResult richTokensResult;
  public final RichAstResult richAstResult;

  public RichAstGeneratorResult(RichTokensResult richTokensResult, RichAstResult richAstResult) {
    this.richTokensResult = richTokensResult;
    this.richAstResult = richAstResult;
  }

  public boolean isOk() {
    return richTokensResult.isOk() && richAstResult.isOk();
  }

  public ArrayList<Token> getOkTokens() {
    return richTokensResult.getOkData();
  }

  public Ast getOkAst() {
    return richAstResult.getOkData();
  }

  public String getErrorMsg() {
    String errorMsg = "";
    switch (richAstResult.getType()) {
      case AST_PARSE_ERROR -> {
        errorMsg = richAstResult.getRichAstParseErrorData().toString();
      }
      case TOKENS_ERROR -> {
        switch (richTokensResult.getType()) {
          case SOURCE_IO_ERROR -> {
            errorMsg = richTokensResult.getSourceIoErrorData();
          }
          case TOKEN_PARSE_ERROR -> {
            errorMsg = richTokensResult.getRichTokenParseErrorData().toString();
          }
          default -> {
            throw new IllegalStateException("Unexpected value: " + richTokensResult.getType());
          }
        }
      }
    }
    return errorMsg;
  }

  public enum RichTokensResultType {
    OK,
    TOKEN_PARSE_ERROR,
    SOURCE_IO_ERROR
  }

  public enum RichAstResultType {
    OK,
    AST_PARSE_ERROR,
    TOKENS_ERROR
  }

  public static class RichTokensResult {
    private final RichTokensResultType type;
    private final Object data;

    RichTokensResult(RichTokensResultType type, Object data) {
      this.type = type;
      this.data = data;
    }

    public static RichTokensResult generateOkResult(ArrayList<Token> data) {
      return new RichTokensResult(RichTokensResultType.OK, data);
    }

    public static RichTokensResult generateSourceIoErrorResult(String data) {
      return new RichTokensResult(RichTokensResultType.SOURCE_IO_ERROR, data);
    }

    public static RichTokensResult generateTokenParseErrorResult(RichTokenParseErrorData data) {
      return new RichTokensResult(RichTokensResultType.TOKEN_PARSE_ERROR, data);
    }

    public RichTokensResultType getType() {
      return type;
    }

    public boolean isOk() {
      return type == RichTokensResultType.OK;
    }

    public ArrayList<Token> getOkData() {
      return (ArrayList<Token>) data;
    }

    public String getSourceIoErrorData() {
      return (String) data;
    }

    public RichTokenParseErrorData getRichTokenParseErrorData() {
      return (RichTokenParseErrorData) data;
    }
  }

  public static class RichTokenParseErrorData {
    public final ArrayList<Token> finishedTokens;
    public final int startLineNumber;
    public final int startOffsetInLine;
    public final int endLineNumber;
    public final int endOffsetInLine;
    public final String errorText;

    public RichTokenParseErrorData(
        ArrayList<Token> finishedTokens,
        int startLineNumber,
        int startOffsetInLine,
        int endLineNumber,
        int endOffsetInLine,
        String errorText) {
      this.finishedTokens = finishedTokens;
      this.startLineNumber = startLineNumber;
      this.startOffsetInLine = startOffsetInLine;
      this.endLineNumber = endLineNumber;
      this.endOffsetInLine = endOffsetInLine;
      this.errorText = errorText;
    }

    @Override
    public String toString() {
      return String.format(
          "generate token error,error near [%d-%d,%d-%d): '%s'",
          startLineNumber, startOffsetInLine, endLineNumber, endOffsetInLine, errorText);
    }
  }

  public static class RichAstResult {
    private final RichAstResultType type;
    private final Object data;

    public RichAstResult(RichAstResultType type, Object data) {
      this.type = type;
      this.data = data;
    }

    public static RichAstResult generateOkResult(Ast data) {
      return new RichAstResult(RichAstResultType.OK, data);
    }

    public static RichAstResult generateRichAstParseErrorResult(RichAstParseErrorData data) {
      return new RichAstResult(RichAstResultType.AST_PARSE_ERROR, data);
    }

    public static RichAstResult generateRichTokensErrorResult() {
      return new RichAstResult(RichAstResultType.TOKENS_ERROR, null);
    }

    public RichAstResultType getType() {
      return type;
    }

    public boolean isOk() {
      return type == RichAstResultType.OK;
    }

    public Ast getOkData() {
      return (Ast) data;
    }

    public RichAstParseErrorData getRichAstParseErrorData() {
      return (RichAstParseErrorData) data;
    }
  }

  public static class RichAstParseErrorData {
    public final int startLineNumber;
    public final int startOffsetInLine;
    public final int endLineNumber;
    public final int endOffsetInLine;
    public final String errorText;

    public RichAstParseErrorData(
        int startLineNumber,
        int startOffsetInLine,
        int endLineNumber,
        int endOffsetInLine,
        String errorText) {
      this.startLineNumber = startLineNumber;
      this.startOffsetInLine = startOffsetInLine;
      this.endLineNumber = endLineNumber;
      this.endOffsetInLine = endOffsetInLine;
      this.errorText = errorText;
    }

    @Override
    public String toString() {
      return String.format(
          "generate ast error,error near [%d-%d,%d-%d): '%s'",
          startLineNumber, startOffsetInLine, endLineNumber, endOffsetInLine, errorText);
    }
  }
}
