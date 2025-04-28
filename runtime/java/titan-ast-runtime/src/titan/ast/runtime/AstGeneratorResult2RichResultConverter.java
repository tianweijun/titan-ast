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
class AstGeneratorResult2RichResultConverter {
  RichAstGeneratorResultStringEncoder stringEncoder = new RichAstGeneratorResultStringEncoder();
  private byte newline = '\n';

  byte getNewline() {
    return newline;
  }

  void setNewline(byte newline) {
    this.newline = newline;
  }

  Charset getCharset() {
    return stringEncoder.getCharset();
  }

  void setCharset(String charsetName) {
    stringEncoder.setCharset(charsetName);
  }

  void setCharset(Charset charset) {
    stringEncoder.setCharset(charset);
  }

  RichAstGeneratorResult convert(AstGeneratorResult astGeneratorResult) {
    RichTokensResult richTokensResult =
        convert2RichTokensResultNoEncodingTokens(astGeneratorResult.tokensResult);
    RichAstResult richAstResult = convert2RichAstResultNoEncodingAst(astGeneratorResult);
    encode(richTokensResult, richAstResult);
    return new RichAstGeneratorResult(richTokensResult, richAstResult);
  }

  private void encode(RichTokensResult richTokensResult, RichAstResult richAstResult) {
    switch (richAstResult.getType()) {
      case OK -> {
        ArrayList<Token> tokens = richTokensResult.getOkData();
        stringEncoder.encodeTokens(tokens);
        encodeAstByEncodedTokens(richAstResult.getOkData(), tokens.iterator());
      }
      case AST_PARSE_ERROR -> {
        stringEncoder.encodeTokens(richTokensResult.getOkData());
      }
      case TOKENS_ERROR -> {}
    }
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
      terminalAst.token.start = encodedToken.start;
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
        lineNumberDetail.getLineNumberRange(astParseErrorData.start);
    LineNumberRange endLineNumberRange =
        lineNumberDetail.getLineNumberRange(astParseErrorData.end - 1);
    int startOffsetInLine =
        stringEncoder.getOffsetInLine(tokens, startLineNumberRange, astParseErrorData.start);
    int endOffsetInLine =
        stringEncoder.getOffsetInLine(tokens, endLineNumberRange, astParseErrorData.end);
    return new RichAstParseErrorData(
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
    Token errorToken = new Token(tokenParseErrorData.start);
    errorToken.text = tokenParseErrorData.errorText;
    tokens.add(errorToken);

    LineNumberDetail lineNumberDetail = buildLineNumberDetail(tokens);

    LineNumberRange startLineNumberRange =
        lineNumberDetail.getLineNumberRange(tokenParseErrorData.start);
    LineNumberRange endLineNumberRange =
        lineNumberDetail.getLineNumberRange(tokenParseErrorData.end - 1);
    int startOffsetInLine =
        stringEncoder.getOffsetInLine(tokens, startLineNumberRange, tokenParseErrorData.start);
    int endOffsetInLine =
        stringEncoder.getOffsetInLine(tokens, endLineNumberRange, tokenParseErrorData.end);

    return new RichTokenParseErrorData(
        stringEncoder.encodeTokens(tokenParseErrorData.finishedTokens),
        startLineNumberRange.lineNumber,
        startOffsetInLine + 1, // 从1开始计数，所以+1
        endLineNumberRange.lineNumber,
        endOffsetInLine + 1, // 从1开始计数，所以+1
        stringEncoder.encodeString(errorToken.text));
  }

  private RichTokensResult convert2RichTokensResultNoEncodingTokens(TokensResult tokensResult) {
    RichTokensResult richTokensResult = null;
    switch (tokensResult.getType()) {
      case OK -> {
        richTokensResult = RichTokensResult.generateOkResult(tokensResult.getOkData());
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
