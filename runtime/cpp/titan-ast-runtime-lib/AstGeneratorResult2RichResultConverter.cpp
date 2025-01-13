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
  RichAstResult *richAstResult = convert2RichAstResult(astGeneratorResult);
  RichTokensResult *richTokensResult =
      convert2RichTokensResult(astGeneratorResult->tokensResult);
  delete astGeneratorResult;
  return new RichAstGeneratorResult(richTokensResult, richAstResult);
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

LineNumberDetail *AstGeneratorResult2RichResultConverter::buildLineNumberDetail(
    std::vector<Token *> *tokens) {
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
    AstGeneratorResult *astGeneratorResult) {
  RichAstResult *richAstResult = nullptr;
  switch (astGeneratorResult->astResult->type) {
  case AstResultType::OK: {
    richAstResult = RichAstResult::generateOkResult(
        astGeneratorResult->astResult->getOkData());
    // data move
    break;
  }
  case AstResultType::AST_PARSE_ERROR: {
    auto astParseErrorData =
        astGeneratorResult->astResult->getAstParseErrorData();
    richAstResult = RichAstResult::generateRichAstParseErrorResult(
        convert2RichAstParseErrorData(
            astGeneratorResult->astResult->getAstParseErrorData(),
            astGeneratorResult->tokensResult->getOkData()));
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
  // data is invalid,been moved
  astGeneratorResult->astResult->data = nullptr;
  return richAstResult;
}

RichTokenParseErrorData *
AstGeneratorResult2RichResultConverter::convert2RichTokenGeneratorErrorData(
    TokenParseErrorData *tokenParseErrorData) {
  std::vector<Token *> tokens(tokenParseErrorData->finishedTokens->size() + 1,
                              nullptr);
  int indexOfToken = 0;
  for (auto token : *tokenParseErrorData->finishedTokens) {
    tokens[indexOfToken++] = token;
  }
  Token errorToken;
  errorToken.start = tokenParseErrorData->start;
  errorToken.text = tokenParseErrorData->errorText;
  tokens[indexOfToken++] = &errorToken;

  LineNumberDetail *lineNumberDetail = buildLineNumberDetail(&tokens);
  LineNumberRangeDto startLineNumberRange =
      lineNumberDetail->getLineNumberRangeDto(tokenParseErrorData->start);
  LineNumberRangeDto endLineNumberRange =
      lineNumberDetail->getLineNumberRangeDto(tokenParseErrorData->end - 1);
  delete lineNumberDetail;
  auto richTokenParseErrorData = new RichTokenParseErrorData(
      tokenParseErrorData->finishedTokens, tokenParseErrorData->start,
      tokenParseErrorData->end, startLineNumberRange.lineNumber,
      tokenParseErrorData->start - startLineNumberRange.start +
          1, // 用户角度下标从1开始
      endLineNumberRange.lineNumber,
      tokenParseErrorData->end - endLineNumberRange.start +
          1, // 用户角度下标从1开始
      tokenParseErrorData->errorText);
  // take token in finishedTokens
  tokenParseErrorData->finishedTokens = nullptr;
  return richTokenParseErrorData;
}

RichAstParseErrorData *
AstGeneratorResult2RichResultConverter::convert2RichAstParseErrorData(
    AstParseErrorData *astParseErrorData, std::vector<Token *> *tokens) {
  LineNumberDetail *lineNumberDetail = buildLineNumberDetail(tokens);
  LineNumberRangeDto startLineNumberRange =
      lineNumberDetail->getLineNumberRangeDto(astParseErrorData->start);
  LineNumberRangeDto endLineNumberRange =
      lineNumberDetail->getLineNumberRangeDto(astParseErrorData->end - 1);
  delete lineNumberDetail;
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
