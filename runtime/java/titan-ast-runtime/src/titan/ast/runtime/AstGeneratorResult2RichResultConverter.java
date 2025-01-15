package titan.ast.runtime;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import titan.ast.runtime.AstGeneratorResult.AstParseErrorData;
import titan.ast.runtime.AstGeneratorResult.TokenParseErrorData;
import titan.ast.runtime.AstGeneratorResult.TokensResult;
import titan.ast.runtime.LineNumberDetail.LineNumberRange;
import titan.ast.runtime.RichAstGeneratorResult.RichAstParseErrorData;
import titan.ast.runtime.RichAstGeneratorResult.RichAstResult;
import titan.ast.runtime.RichAstGeneratorResult.RichTokenParseErrorData;
import titan.ast.runtime.RichAstGeneratorResult.RichTokensResult;

/**
 * .
 *
 * @author tian wei jun
 */
public class AstGeneratorResult2RichResultConverter {
  private byte newline = '\n';
  RichAstGeneratorResultStringEncoder stringEncoder = new RichAstGeneratorResultStringEncoder();

  public byte getNewline() {
    return newline;
  }

  public void setNewline(byte newline) {
    this.newline = newline;
  }

  public void setCharset(String charsetName) {
    stringEncoder.setCharset(charsetName);
  }

  public void setCharset(Charset charset) {
    stringEncoder.setCharset(charset);
  }

  public Charset getCharset() {
    return stringEncoder.getCharset();
  }

  public RichAstGeneratorResult convert(AstGeneratorResult astGeneratorResult) {
    RichAstResult richAstResult = convert2RichAstResultNoEncodingAst(astGeneratorResult);
    RichTokensResult richTokensResult = convert2RichTokensResult(astGeneratorResult.tokensResult);
    if (richAstResult.isOk()) {
      encodeAstByEncodedTokens(richAstResult.getOkData(), richTokensResult.getOkData().iterator());
    }
    return new RichAstGeneratorResult(richTokensResult, richAstResult);
  }

  private void encodeAstByEncodedTokens(Ast ast, Iterator<Token> encodedTokensIt) {
    for (Ast child : ast.children) {
      encodeAstByEncodedTokens(child, encodedTokensIt);
    }
    if (ast instanceof TerminalAst terminalAst) {
      Token encodedToken = encodedTokensIt.next();
      while (encodedToken.type != TokenType.TEXT) {
        encodedToken = encodedTokensIt.next();
      }
      terminalAst.token.text = encodedToken.text;
    }
  }

  private RichAstResult convert2RichAstResultNoEncodingAst(AstGeneratorResult astGeneratorResult) {
    RichAstResult richAstResult = null;

    switch (astGeneratorResult.astResult.getType()) {
      case OK -> {
        richAstResult = RichAstResult.generateOkResult(astGeneratorResult.astResult.getOkData());
      }
      case AST_PARSE_ERROR -> {
        richAstResult =
            RichAstResult.generateRichAstParseErrorResult(
                convert2RichAstParseErrorData(
                    astGeneratorResult.astResult.getAstParseErrorData(),
                    astGeneratorResult.tokensResult.getOkData()));
      }
      case TOKENS_ERROR -> {
        richAstResult = RichAstResult.generateRichTokensErrorResult();
      }
    }
    return richAstResult;
  }

  private RichAstParseErrorData convert2RichAstParseErrorData(
      AstParseErrorData astParseErrorData, ArrayList<Token> tokens) {
    LineNumberDetail lineNumberDetail = buildLineNumberDetail(tokens);
    LineNumberRange startLineNumberRange =
        lineNumberDetail.getLineNumberRangeDto(astParseErrorData.start);
    LineNumberRange endLineNumberRange =
        lineNumberDetail.getLineNumberRangeDto(astParseErrorData.end - 1);
    int startOffsetInLine =
        stringEncoder.getOffsetInLine(tokens, startLineNumberRange, astParseErrorData.start);
    int endOffsetInLine =
        stringEncoder.getOffsetInLine(tokens, endLineNumberRange, astParseErrorData.end);
    return new RichAstParseErrorData(
        astParseErrorData.start,
        astParseErrorData.end,
        startLineNumberRange.lineNumber,
        startOffsetInLine + 1, // 从1开始计数，所以+1
        endLineNumberRange.lineNumber,
        endOffsetInLine + 1, // 从1开始计数，所以+1
        stringEncoder.encodeString(astParseErrorData.errorText));
  }

  private RichTokenParseErrorData convert2RichTokenGeneratorErrorData(
      TokenParseErrorData tokenParseErrorData) {
    ArrayList<Token> tokens = new ArrayList<>(tokenParseErrorData.finishedTokens.size() + 1);
    tokens.addAll(tokenParseErrorData.finishedTokens);
    Token token = new Token(tokenParseErrorData.start);
    token.text = tokenParseErrorData.errorText;
    tokens.add(token);

    LineNumberDetail lineNumberDetail = buildLineNumberDetail(tokens);

    LineNumberRange startLineNumberRange =
        lineNumberDetail.getLineNumberRangeDto(tokenParseErrorData.start);
    LineNumberRange endLineNumberRange =
        lineNumberDetail.getLineNumberRangeDto(tokenParseErrorData.end - 1);
    int startOffsetInLine =
        stringEncoder.getOffsetInLine(tokens, startLineNumberRange, tokenParseErrorData.start);
    int endOffsetInLine =
        stringEncoder.getOffsetInLine(tokens, endLineNumberRange, tokenParseErrorData.end);
    return new RichTokenParseErrorData(
        stringEncoder.encodeTokens(tokenParseErrorData.finishedTokens),
        tokenParseErrorData.start,
        tokenParseErrorData.end,
        startLineNumberRange.lineNumber,
        startOffsetInLine + 1, // 从1开始计数，所以+1
        endLineNumberRange.lineNumber,
        endOffsetInLine + 1, // 从1开始计数，所以+1
        stringEncoder.encodeString(tokenParseErrorData.errorText));
  }

  private RichTokensResult convert2RichTokensResult(TokensResult tokensResult) {
    RichTokensResult richTokensResult = null;
    switch (tokensResult.getType()) {
      case OK -> {
        richTokensResult =
            RichTokensResult.generateOkResult(stringEncoder.encodeTokens(tokensResult.getOkData()));
      }
      case TOKEN_PARSE_ERROR -> {
        richTokensResult =
            RichTokensResult.generateTokenParseErrorResult(
                convert2RichTokenGeneratorErrorData(tokensResult.getTokenParseErrorData()));
      }
      case SOURCE_IO_ERROR -> {
        richTokensResult =
            RichTokensResult.generateSourceIoErrorResult(tokensResult.getSourceIoErrorData());
      }
    }
    return richTokensResult;
  }

  private LineNumberDetail buildLineNumberDetail(List<Token> tokens) {
    ArrayList<LineNumberRange> lineNumberRanges = new ArrayList<LineNumberRange>();
    int indexOfStartToken = 0;
    int start = 0;
    int indexOfBytes = -1;
    int indexOfToken = 0;
    for (; indexOfToken < tokens.size(); indexOfToken++) {
      Token token = tokens.get(indexOfToken);
      byte[] bytes = token.text.getBytes(AstGeneratorResult.DEFAULT_CHARSET);
      for (int indexOfTokenBytes = 0; indexOfTokenBytes < bytes.length; indexOfTokenBytes++) {
        ++indexOfBytes;
        if (bytes[indexOfTokenBytes] == newline) {
          int end = indexOfBytes + 1;
          int indexOfEndToken = indexOfToken;
          lineNumberRanges.add(
              new LineNumberRange(
                  start, end, lineNumberRanges.size() + 1, indexOfStartToken, indexOfEndToken));
          // 更新下一行
          start = end;
          if (indexOfTokenBytes + 1 < bytes.length) {
            indexOfStartToken = indexOfEndToken;
          } else {
            indexOfStartToken = indexOfEndToken + 1;
          }
        }
      }
    }
    // 最后一行可以没有换行符，设置这个特殊行
    if (indexOfBytes >= start) {
      int end = indexOfBytes + 1;
      lineNumberRanges.add(
          new LineNumberRange(
              start, end, lineNumberRanges.size() + 1, indexOfStartToken, indexOfToken));
      // 更新下一行
      start = end;
    }
    // lineNumberRanges move to array
    LineNumberRange[] lineNumberRangeArray = new LineNumberRange[lineNumberRanges.size()];
    int indexOfLineNumberRange = 0;
    for (LineNumberRange lineNumberRange : lineNumberRanges) {
      lineNumberRangeArray[indexOfLineNumberRange++] = lineNumberRange;
    }
    return new LineNumberDetail(lineNumberRangeArray);
  }
}
