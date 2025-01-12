//
// Created by tian wei jun on 2024/10/26.
//

#include "AstGeneratorResult2RichResultConverter.h"
AstGeneratorResult2RichResultConverter::AstGeneratorResult2RichResultConverter()
    : newline('\n') {}
AstGeneratorResult2RichResultConverter::
    ~AstGeneratorResult2RichResultConverter() = default;

void AstGeneratorResult2RichResultConverter::setNewline(byte newline) {
  this->newline = newline;
}
byte AstGeneratorResult2RichResultConverter::getNewline() { return newline; }

RichAstGeneratorResult *AstGeneratorResult2RichResultConverter::convert(
    AstGeneratorResult *astGeneratorResult) {
  RichTokensResult *richTokensResult =
      convert2RichTokensResult(astGeneratorResult->tokensResult);
  LineNumberDetail *lineNumberDetail;
  if (richTokensResult->isOk()) {
    lineNumberDetail = buildLineNumberDetail(richTokensResult->getOkData());
  } else {
    lineNumberDetail = new LineNumberDetail(nullptr, 0);
  }
  RichAstResult *richAstResult =
      convert2RichAstResult(astGeneratorResult->astResult, lineNumberDetail);
  delete astGeneratorResult;
  return new RichAstGeneratorResult(richTokensResult, lineNumberDetail,
                                    richAstResult);
}

RichTokensResult *
AstGeneratorResult2RichResultConverter::convert2RichTokensResult(
    TokensResult *tokensResult) {
  RichTokensResult *richTokensResult = nullptr;
  switch (tokensResult->type) {
  case TokensResultType::OK: {
    richTokensResult =
        RichTokensResult::generateOkResult(tokensResult->getOkData());
    // data move
    break;
  }
  case TokensResultType::TOKEN_PARSE_ERROR: {
    auto tokenParseErrorData = tokensResult->getTokenParseErrorData();
    richTokensResult = RichTokensResult::generateTokenParseErrorResult(
        convert2RichTokenGeneratorErrorData(tokenParseErrorData));
    // data delete
    delete tokenParseErrorData;
    break;
  }
  case TokensResultType::SOURCE_IO_ERROR: {
    richTokensResult = RichTokensResult::generateSourceIoErrorResult(
        tokensResult->getSourceIoErrorData());
    // data move
    break;
  }
  }
  // data is invalid
  tokensResult->data = nullptr;
  return richTokensResult;
}
RichTokenParseErrorData *
AstGeneratorResult2RichResultConverter::convert2RichTokenGeneratorErrorData(
    TokenParseErrorData *tokenParseErrorData) {
  char charNewline = (char)newline;
  // set startLineNumber,lineNumberStartIndex
  int startLineNumber = 1;
  int indexOfBytes = -1;
  int startLineNumberIndex = 0;
  for (Token *token : *tokenParseErrorData->finishedTokens) {
    auto text = token->text.data();
    auto lenOfText = token->text.length();
    for (int indexOfText = 0; indexOfText < lenOfText; indexOfText++) {
      char ch = text[indexOfText];
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
  {
    auto stringText = tokenParseErrorData->errorText;
    auto text = stringText.data();
    auto lenOfText = stringText.length();
    for (int indexOfText = 0; indexOfText < lenOfText; indexOfText++) {
      char ch = text[indexOfText];
      ++indexOfBytes;
      if (ch == charNewline) {
        ++endLineNumber;
        endLineNumberIndex = indexOfBytes + 1;
      }
    }
  }
  auto richTokenParseErrorData = new RichTokenParseErrorData(
      tokenParseErrorData->finishedTokens, tokenParseErrorData->start,
      tokenParseErrorData->end, startLineNumber,
      tokenParseErrorData->start - startLineNumberIndex +
          1, // 用户角度下标从1开始
      endLineNumber,
      tokenParseErrorData->end - endLineNumberIndex + 1, // 用户角度下标从1开始
      tokenParseErrorData->errorText);
  // take token in finishedTokens
  tokenParseErrorData->finishedTokens = nullptr;
  return richTokenParseErrorData;
}

LineNumberDetail *AstGeneratorResult2RichResultConverter::buildLineNumberDetail(
    std::list<Token *> *tokens) {
  char charNewline = (char)newline;
  std::list<LineNumberRange> lineNumberRanges;
  int nextStart = 0;
  int indexOfBytes = -1;
  for (Token *token : *tokens) {
    auto text = token->text.data();
    auto lenOfText = token->text.length();
    for (int indexOfText = 0; indexOfText < lenOfText; indexOfText++) {
      char ch = text[indexOfText];
      ++indexOfBytes;
      if (ch == charNewline) {
        int nextEnd = indexOfBytes + 1;
        lineNumberRanges.push_back(LineNumberRange(nextStart, nextEnd));
        // 更新下一行
        nextStart = nextEnd;
      }
    }
  }
  // 最后一行可以没有换行符，设置这个特殊行
  if (indexOfBytes >= nextStart) {
    int nextEnd = indexOfBytes + 1;
    lineNumberRanges.push_back(LineNumberRange(nextStart, nextEnd));
    // 更新下一行
    nextStart = nextEnd;
  }
  // lineNumberRanges move to array
  auto *lineNumberRangeArray = new LineNumberRange[lineNumberRanges.size()];
  int indexOfLineNumberRange = 0;
  for (const LineNumberRange &lineNumberRange : lineNumberRanges) {
    lineNumberRangeArray[indexOfLineNumberRange++] = lineNumberRange;
  }
  return new LineNumberDetail(lineNumberRangeArray, lineNumberRanges.size());
}
RichAstResult *AstGeneratorResult2RichResultConverter::convert2RichAstResult(
    AstResult *astResult, LineNumberDetail *lineNumberDetail) {
  RichAstResult *richAstResult = nullptr;
  switch (astResult->type) {
  case AstResultType::OK: {
    richAstResult = RichAstResult::generateOkResult(astResult->getOkData());
    // data move
    break;
  }
  case AstResultType::AST_PARSE_ERROR: {
    auto astParseErrorData = astResult->getAstParseErrorData();
    richAstResult = RichAstResult::generateRichAstParseErrorResult(
        convert2RichAstParseErrorData(astResult->getAstParseErrorData(),
                                      lineNumberDetail));
    // delete data
    delete astParseErrorData;
    break;
  }
  case AstResultType::TOKENS_ERROR: {
    richAstResult = RichAstResult::generateRichTokensErrorResult();
    // data is nullptr
    break;
  }
  }
  // data is invalid
  astResult->data = nullptr;
  return richAstResult;
}

RichAstParseErrorData *
AstGeneratorResult2RichResultConverter::convert2RichAstParseErrorData(
    AstParseErrorData *astParseErrorData, LineNumberDetail *lineNumberDetail) {
  LineNumberRangeDto startLineNumberRange =
      lineNumberDetail->getLineNumberRangeDto(astParseErrorData->start);
  LineNumberRangeDto endLineNumberRange =
      lineNumberDetail->getLineNumberRangeDto(astParseErrorData->end-1);
  return new RichAstParseErrorData(
      astParseErrorData->start, astParseErrorData->end,
      startLineNumberRange.lineNumber,
      astParseErrorData->start - startLineNumberRange.start +
          1, // 用户角度下标从1开始
      endLineNumberRange.lineNumber,
      astParseErrorData->end - endLineNumberRange.start +
          1, // 用户角度下标从1开始
      astParseErrorData->errorText);
}
