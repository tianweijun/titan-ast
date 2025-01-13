//
// Created by tian wei jun on 2024/10/25.
//

#include "Result.h"
#include <sstream>

TokensResult::TokensResult(TokensResultType type, void *data)
    : type(type), data(data) {}
TokensResult::~TokensResult() {
  if (data == nullptr) {
    return;
  }
  switch (type) {
  case TokensResultType::OK: {
    auto okData = getOkData();
    for (auto &token : *okData) {
      delete token;
    }
    delete okData;
    break;
  }
  case TokensResultType::TOKEN_PARSE_ERROR: {
    auto tokenParseErrorData = getTokenParseErrorData();
    delete tokenParseErrorData;
    break;
  }
  case TokensResultType::SOURCE_IO_ERROR: {
    auto sourceIoErrorData = getSourceIoErrorData();
    delete sourceIoErrorData;
    break;
  }
  }
}

bool TokensResult::isOk() const { return type == TokensResultType::OK; }

TokensResult *TokensResult::generateOkResult(std::vector<Token *> *data) {
  return new TokensResult(TokensResultType::OK, data);
}
std::vector<Token *> *TokensResult::getOkData() const {
  return static_cast<std::vector<Token *> *>(data);
}

TokensResult *TokensResult::generateSourceIoErrorResult(std::string *data) {
  return new TokensResult(TokensResultType::SOURCE_IO_ERROR, data);
}
std::string *TokensResult::getSourceIoErrorData() const {
  return static_cast<std::string *>(data);
}
TokensResult *
TokensResult::generateTokenParseErrorResult(TokenParseErrorData *data) {
  return new TokensResult(TokensResultType::TOKEN_PARSE_ERROR, data);
}
TokenParseErrorData *TokensResult::getTokenParseErrorData() const {
  return static_cast<TokenParseErrorData *>(data);
}

TokenParseErrorData::TokenParseErrorData(std::vector<Token *> *finishedTokens,
                                         int start, int end,
                                         std::string errorText)
    : finishedTokens(finishedTokens), start(start), end(end),
      errorText(errorText) {}
TokenParseErrorData::~TokenParseErrorData() {
  if (finishedTokens == nullptr) {
    return;
  }
  for (Token *token : *finishedTokens) {
    delete token;
  }
  delete finishedTokens;
}
std::string TokenParseErrorData::toString() {
  std::stringstream ss;
  ss << "generate token error,error near [" << start << "," << end << "): '"
     << errorText << "'";
  return ss.str();
}

AstParseErrorData::AstParseErrorData(int start, int end, std::string errorText)
    : start(start), end(end), errorText(std::move(errorText)) {}
AstParseErrorData::~AstParseErrorData() = default;
std::string AstParseErrorData::toString() {
  std::stringstream ss;
  ss << "generate ast error,error near [" << start << "," << end << "): '"
     << errorText << "'";
  return ss.str();
}

AstResult::AstResult(AstResultType type, const void *data)
    : type(type), data(data) {}
AstResult::~AstResult() {
  if (data == nullptr) {
    return;
  }
  switch (type) {
  case AstResultType::OK: {
    auto ast = getOkData();
    delete ast;
    break;
  }
  case AstResultType::AST_PARSE_ERROR: {
    auto astParseErrorData = getAstParseErrorData();
    delete astParseErrorData;
    break;
  }
  case AstResultType::TOKENS_ERROR:
    break;
  }
}

bool AstResult::isOk() const { return type == AstResultType::OK; }

AstResult *AstResult::generateOkResult(const Ast *data) {
  return new AstResult(AstResultType::OK, data);
}
Ast *AstResult::getOkData() const { return (Ast *)data; }

AstResult *AstResult::generateAstParseErrorResult(AstParseErrorData *data) {
  return new AstResult(AstResultType::AST_PARSE_ERROR, data);
}
AstParseErrorData *AstResult::getAstParseErrorData() const {
  return (AstParseErrorData *)data;
}

AstResult *AstResult::generateTokensErrorResult() {
  return new AstResult(AstResultType::TOKENS_ERROR, nullptr);
}

AstGeneratorResult::AstGeneratorResult(TokensResult *tokensResult,
                                       AstResult *astResult)
    : tokensResult(tokensResult), astResult(astResult) {}
AstGeneratorResult::~AstGeneratorResult() {
  delete tokensResult;
  delete astResult;
}

bool AstGeneratorResult::isOk() const {
  return tokensResult->isOk() && astResult->isOk();
}
std::vector<Token *> *AstGeneratorResult::getOkTokens() const {
  return tokensResult->getOkData();
}
Ast *AstGeneratorResult::getOkAst() const { return astResult->getOkData(); }
std::string AstGeneratorResult::getErrorMsg() const {
  std::string errorMsg;
  switch (astResult->type) {
  case AstResultType::OK:
    break;
  case AstResultType::AST_PARSE_ERROR: {
    errorMsg = astResult->getAstParseErrorData()->toString();
    break;
  }
  case AstResultType::TOKENS_ERROR: {
    switch (tokensResult->type) {
    case TokensResultType::OK:
      break;
    case TokensResultType::TOKEN_PARSE_ERROR:
      errorMsg = tokensResult->getTokenParseErrorData()->toString();
      break;
    case TokensResultType::SOURCE_IO_ERROR:
      errorMsg = *tokensResult->getSourceIoErrorData();
      break;
    }
    break;
  }
  }
  return errorMsg;
}

RichTokenParseErrorData::RichTokenParseErrorData(
    std::vector<Token *> *finishedTokens, int start, int end, int startLineNumber,
    int startOffsetInLine, int endLineNumber, int endOffsetInLine,
    std::string errorText)
    : finishedTokens(finishedTokens), start(start), end(end),
      startLineNumber(startLineNumber), startOffsetInLine(startOffsetInLine),
      endLineNumber(endLineNumber), endOffsetInLine(endOffsetInLine),
      errorText(std::move(errorText)) {}
RichTokenParseErrorData::~RichTokenParseErrorData() {
  for (Token *token : *finishedTokens) {
    delete token;
  }
  delete finishedTokens;
}
std::string RichTokenParseErrorData::toString() {
  std::stringstream ss;
  ss << "generate token error,error near [" << startLineNumber << "-" << startOffsetInLine
     << "," << endLineNumber << "-" << endOffsetInLine << "): '" << errorText << "'";
  return ss.str();
}

RichTokensResult::RichTokensResult(RichTokensResultType type, void *data)
    : type(type), data(data) {}
RichTokensResult::~RichTokensResult() {
  switch (type) {
  case RichTokensResultType::OK: {
    auto okData = getOkData();
    for (auto &token : *okData) {
      delete token;
    }
    delete okData;
    break;
  }
  case RichTokensResultType::TOKEN_PARSE_ERROR: {
    auto tokenParseErrorData = getTokenParseErrorData();
    delete tokenParseErrorData;
    break;
  }
  case RichTokensResultType::SOURCE_IO_ERROR: {
    auto sourceIoErrorData = getSourceIoErrorData();
    delete sourceIoErrorData;
    break;
  }
  }
}

bool RichTokensResult::isOk() const { return type == RichTokensResultType::OK; }

RichTokensResult *RichTokensResult::generateOkResult(std::vector<Token *> *data) {
  return new RichTokensResult(RichTokensResultType::OK, data);
}
std::vector<Token *> *RichTokensResult::getOkData() const {
  return static_cast<std::vector<Token *> *>(data);
}

RichTokensResult *
RichTokensResult::generateSourceIoErrorResult(std::string *data) {
  return new RichTokensResult(RichTokensResultType::SOURCE_IO_ERROR, data);
}
std::string *RichTokensResult::getSourceIoErrorData() const {
  return static_cast<std::string *>(data);
}
RichTokensResult *
RichTokensResult::generateTokenParseErrorResult(RichTokenParseErrorData *data) {
  return new RichTokensResult(RichTokensResultType::TOKEN_PARSE_ERROR, data);
}
RichTokenParseErrorData *RichTokensResult::getTokenParseErrorData() const {
  return static_cast<RichTokenParseErrorData *>(data);
}
LineNumberRange::LineNumberRange() : start(0), end(0) {}
LineNumberRange::LineNumberRange(int start, int end) : start(start), end(end) {}
LineNumberRange::~LineNumberRange() = default;
LineNumberRangeDto::LineNumberRangeDto(bool isOk, int lineNumber, int start,
                                       int end)
    : isOk(isOk), lineNumber(lineNumber), start(start), end(end) {}
LineNumberRangeDto::~LineNumberRangeDto() = default;
LineNumberDetail::LineNumberDetail(LineNumberRange *lineNumberRanges,
                                   int sizeOfLineNumberRanges)
    : lineNumberRanges(lineNumberRanges),
      sizeOfLineNumberRanges(sizeOfLineNumberRanges) {}
LineNumberDetail::~LineNumberDetail() { delete[] lineNumberRanges; }
LineNumberRangeDto LineNumberDetail::getLineNumberRangeDto(int bytePosition) {
  int left = 0;
  int right = sizeOfLineNumberRanges - 1;

  while (left <= right) {
    int mid = left + (right - left) / 2; // 计算中间元素的索引
    LineNumberRange midLineNumberRange = lineNumberRanges[mid];
    if (bytePosition >= midLineNumberRange.start &&
        bytePosition < midLineNumberRange.end) {
      // 找到目标值，返回行号lineNumber=index+1
      return LineNumberRangeDto(true, mid + 1, midLineNumberRange.start,
                                midLineNumberRange.end);
    } else if (bytePosition < midLineNumberRange.start) {
      right = mid - 1; // 目标值在左半部分
    } else {
      left = mid + 1; // 目标值在右半部分
    }
  }
  return LineNumberRangeDto(false, 0, 0, 0);
}

RichAstParseErrorData::RichAstParseErrorData(
    int start, int end, int startLineNumber, int startOffsetInLine,
    int endLineNumber, int endOffsetInLine, std::string errorText)
    : start(start), end(end), startLineNumber(startLineNumber),
      startOffsetInLine(startOffsetInLine), endLineNumber(endLineNumber),
      endOffsetInLine(endOffsetInLine), errorText(std::move(errorText)) {}
RichAstParseErrorData::~RichAstParseErrorData() = default;
std::string RichAstParseErrorData::toString() {
  std::stringstream ss;
  ss << "generate ast error,error near [" << startLineNumber << "-" << startOffsetInLine
     << "," << endLineNumber << "-" << endOffsetInLine << "): '" << errorText << "'";
  return ss.str();
}

RichAstResult::RichAstResult(RichAstResultType type, const void *data)
    : type(type), data(data) {}
RichAstResult::~RichAstResult() {
  switch (type) {
  case RichAstResultType::OK: {
    auto ast = getOkData();
    delete ast;
    break;
  }
  case RichAstResultType::AST_PARSE_ERROR: {
    auto astParseErrorData = getAstParseErrorData();
    delete astParseErrorData;
    break;
  }
  case RichAstResultType::TOKENS_ERROR:
    break;
  }
}

bool RichAstResult::isOk() const { return type == RichAstResultType::OK; }

RichAstResult *RichAstResult::generateOkResult(const Ast *data) {
  return new RichAstResult(RichAstResultType::OK, data);
}
Ast *RichAstResult::getOkData() const { return (Ast *)data; }

RichAstResult *
RichAstResult::generateRichAstParseErrorResult(RichAstParseErrorData *data) {
  return new RichAstResult(RichAstResultType::AST_PARSE_ERROR, data);
}
RichAstParseErrorData *RichAstResult::getAstParseErrorData() const {
  return (RichAstParseErrorData *)data;
}

RichAstResult *RichAstResult::generateRichTokensErrorResult() {
  return new RichAstResult(RichAstResultType::TOKENS_ERROR, nullptr);
}
// RichAstGeneratorResult
RichAstGeneratorResult::RichAstGeneratorResult(
    const RichTokensResult *richTokensResult,
    const RichAstResult *richAstResult)
    : richTokensResult(richTokensResult),
      richAstResult(richAstResult) {}
RichAstGeneratorResult::~RichAstGeneratorResult() {
  delete richTokensResult;
  delete richAstResult;
}

bool RichAstGeneratorResult::isOk() const {
  return richTokensResult->isOk() && richAstResult->isOk();
}
std::vector<Token *> *RichAstGeneratorResult::getOkTokens() const {
  return richTokensResult->getOkData();
}
Ast *RichAstGeneratorResult::getOkAst() const {
  return richAstResult->getOkData();
}
std::string RichAstGeneratorResult::getErrorMsg() const {
  std::string errorMsg;
  switch (richAstResult->type) {
  case RichAstResultType::OK:
    break;
  case RichAstResultType::AST_PARSE_ERROR: {
    errorMsg = richAstResult->getAstParseErrorData()->toString();
    break;
  }
  case RichAstResultType::TOKENS_ERROR: {
    switch (richTokensResult->type) {
    case RichTokensResultType::OK:
      break;
    case RichTokensResultType::TOKEN_PARSE_ERROR:
      errorMsg = richTokensResult->getTokenParseErrorData()->toString();
      break;
    case RichTokensResultType::SOURCE_IO_ERROR:
      errorMsg = *richTokensResult->getSourceIoErrorData();
      break;
    }
    break;
  }
  }
  return errorMsg;
}
