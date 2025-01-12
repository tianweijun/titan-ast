package titan.ast.runtime;

import java.util.ArrayList;
import java.util.List;
import titan.ast.runtime.AstGeneratorResult.AstParseErrorData;
import titan.ast.runtime.AstGeneratorResult.AstResult;
import titan.ast.runtime.AstGeneratorResult.TokenParseErrorData;
import titan.ast.runtime.AstGeneratorResult.TokensResult;
import titan.ast.runtime.LineNumberDetail.LineNumberRange;
import titan.ast.runtime.LineNumberDetail.LineNumberRangeDto;
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

  public byte getNewline() {
    return newline;
  }

  public void setNewline(byte newline) {
    this.newline = newline;
  }

  public RichAstGeneratorResult convert(AstGeneratorResult astGeneratorResult) {
    RichTokensResult richTokensResult = convert2RichTokensResult(astGeneratorResult.tokensResult);
    LineNumberDetail lineNumberDetail;
    if (richTokensResult.isOk()) {
      lineNumberDetail = buildLineNumberDetail(richTokensResult.getOkData());
    } else {
      lineNumberDetail = new LineNumberDetail(new LineNumberRange[0]);
    }
    RichAstResult richAstResult =
        convert2RichAstResult(astGeneratorResult.astResult, lineNumberDetail);
    return new RichAstGeneratorResult(richTokensResult, lineNumberDetail, richAstResult);
  }

  private RichAstResult convert2RichAstResult(
      AstResult astResult, LineNumberDetail lineNumberDetail) {
    RichAstResult richAstResult = null;
    switch (astResult.getType()) {
      case OK -> {
        richAstResult = RichAstResult.generateOkResult(astResult.getOkData());
      }
      case AST_PARSE_ERROR -> {
        richAstResult =
            RichAstResult.generateRichAstParseErrorResult(
                convert2RichAstParseErrorData(astResult.getAstParseErrorData(), lineNumberDetail));
      }
      case TOKENS_ERROR -> {
        richAstResult = RichAstResult.generateRichTokensErrorResult();
      }
    }
    return richAstResult;
  }

  private RichAstParseErrorData convert2RichAstParseErrorData(
      AstParseErrorData astParseErrorData, LineNumberDetail lineNumberDetail) {
    LineNumberRangeDto startLineNumberRange =
        lineNumberDetail.getLineNumberRangeDto(astParseErrorData.start);
    LineNumberRangeDto endLineNumberRange =
        lineNumberDetail.getLineNumberRangeDto(astParseErrorData.end - 1);
    return new RichAstParseErrorData(
        astParseErrorData.start,
        astParseErrorData.end,
        startLineNumberRange.lineNumber,
        astParseErrorData.start - startLineNumberRange.start + 1, // 用户角度下标从1开始
        endLineNumberRange.lineNumber,
        astParseErrorData.end - endLineNumberRange.start + 1, // 用户角度下标从1开始
        astParseErrorData.errorText);
  }

  private RichTokensResult convert2RichTokensResult(TokensResult tokensResult) {
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

  private RichTokenParseErrorData convert2RichTokenGeneratorErrorData(
      TokenParseErrorData tokenParseErrorData) {
    char charNewline = (char) newline;
    // set startLineNumber,lineNumberStartIndex
    int startLineNumber = 1;
    int indexOfBytes = -1;
    int startLineNumberIndex = 0;
    for (Token token : tokenParseErrorData.finishedTokens) {
      for (char ch : token.text.toCharArray()) {
        ++indexOfBytes;
        if (ch == charNewline) {
          ++startLineNumber;
          startLineNumberIndex = indexOfBytes + 1;
        }
      }
    }
    // set endLineNumber,lineNumberEndIndex
    int endLineNumber = startLineNumber;
    int endLineNumberIndex = startLineNumberIndex;
    for (char ch : tokenParseErrorData.errorText.toCharArray()) {
      ++indexOfBytes;
      if (ch == charNewline) {
        ++endLineNumber;
        endLineNumberIndex = indexOfBytes + 1;
      }
    }
    return new RichTokenParseErrorData(
        tokenParseErrorData.finishedTokens,
        tokenParseErrorData.start,
        tokenParseErrorData.end,
        startLineNumber,
        tokenParseErrorData.start - startLineNumberIndex + 1, // 用户角度下标从1开始
        endLineNumber,
        tokenParseErrorData.end - endLineNumberIndex + 1, // 用户角度下标从1开始
        tokenParseErrorData.errorText);
  }

  public LineNumberDetail buildLineNumberDetail(List<Token> tokens) {
    char charNewline = (char) newline;
    ArrayList<LineNumberRange> lineNumberRanges = new ArrayList<LineNumberRange>();
    int nextStart = 0;
    int indexOfBytes = -1;
    for (Token token : tokens) {
      for (char ch : token.text.toCharArray()) {
        ++indexOfBytes;
        if (ch == charNewline) {
          int nextEnd = indexOfBytes + 1;
          lineNumberRanges.add(new LineNumberRange(nextStart, nextEnd));
          // 更新下一行
          nextStart = nextEnd;
        }
      }
    }
    // 最后一行可以没有换行符，设置这个特殊行
    if (indexOfBytes >= nextStart) {
      int nextEnd = indexOfBytes + 1;
      lineNumberRanges.add(new LineNumberRange(nextStart, nextEnd));
      // 更新下一行
      nextStart = nextEnd;
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
