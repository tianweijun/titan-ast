//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "DfaTokenAutomata.h"
#include <sstream>

const int DfaTokenAutomata::eof = -1;

DfaTokenAutomata::DfaTokenAutomata(const TokenDfa *tokenDfa) : dfa(tokenDfa),oneTokenStringBuilder(256) {}

// dfa delete by PersistentObject.tokenDfa
DfaTokenAutomata::~DfaTokenAutomata() = default;

TokensResult *DfaTokenAutomata::buildToken(const std::string *sourceFilePath) {
  ByteBufferedInputStream byteBufferedInputStream;
  auto initResult = byteBufferedInputStream.init(sourceFilePath);
  if (!initResult.isOk) {
    // byteBufferedInputStream初始化错误（可能原因：源文件不存在）
    return TokensResult::generateSourceIoErrorResult(
        new std::string(initResult.msg));
  }
  // tokens delete by caller
  auto *tokens = new std::list<Token *>();
  TokensResult *tokensResult = nullptr;
  while (true) {
    BuildOneTokenMethodResult *buildOneTokenMethodResult =
        buildOneToken(byteBufferedInputStream);
    if (buildOneTokenMethodResult->type == BuildOneTokenMethodResultType::TOKEN) {
      tokens->push_back(buildOneTokenMethodResult->getTokenData());
      delete buildOneTokenMethodResult;
      continue;
    }
    if (buildOneTokenMethodResult->type == BuildOneTokenMethodResultType::ALL_TEXT_HAS_BEEN_BUILT) {
      auto arrayTokens = new std::vector<Token *> (tokens->size(), nullptr);
      int indexOfToken = 0;
      for(auto token : *tokens){
        (*arrayTokens)[indexOfToken++] = token;
      }
      tokensResult = TokensResult::generateOkResult(arrayTokens);
      delete buildOneTokenMethodResult;
      break;
    }
    if (buildOneTokenMethodResult->type == BuildOneTokenMethodResultType::TOKEN_PARSE_ERROR) {
      BuildOneTokenMethodTokenGeneratorErrorData *tokenGeneratorErrorData =
          buildOneTokenMethodResult
              ->getBuildOneTokenMethodTokenGeneratorErrorData();
          auto arrayTokens = new std::vector<Token *> (tokens->size(), nullptr);
          int indexOfToken = 0;
          for(auto token : *tokens){
            (*arrayTokens)[indexOfToken++] = token;
          }
          tokensResult = TokensResult::generateTokenParseErrorResult(
          new TokenParseErrorData(arrayTokens, tokenGeneratorErrorData->start,
                                  tokenGeneratorErrorData->end,
                                  tokenGeneratorErrorData->errorText));
      delete buildOneTokenMethodResult;
      break;
    }
    if (buildOneTokenMethodResult->type == BuildOneTokenMethodResultType::IO_ERROR) {
      tokensResult = TokensResult::generateSourceIoErrorResult(new std::string(
          "read data from file '" + *sourceFilePath + "' error"));
      for (Token *token : *tokens) {
        delete token;
      }
      delete tokens;
      delete buildOneTokenMethodResult;
      break;
    }
    for (Token *token : *tokens) {
      delete token;
    }
    delete tokens;
    delete buildOneTokenMethodResult;
    break;
  };

  return tokensResult;
}

BuildOneTokenMethodResult *DfaTokenAutomata::buildOneToken(
    ByteBufferedInputStream &byteBufferedInputStream) {
  oneTokenStringBuilder.clear();
  int startIndexOfToken = byteBufferedInputStream.nextReadIndex;
  auto readResult = byteBufferedInputStream.read();
  if (!readResult.isOk) {
    return BuildOneTokenMethodResult::generateIoErrorResult();
  }
  int ch = readResult.data;
  if (ch == eof) {// 输入流读取完了
    return BuildOneTokenMethodResult::generateAllTextHasBeenBuiltResult();
  }
  // first terminal state
  const TokenDfaState *firstTerminalState = nullptr;
  const TokenDfaState *currentState = dfa->start;
  while (ch != eof) {
    TokenDfaState *nextState = nullptr;
    auto nextStateIt = currentState->edges.find(ch);
    if (nextStateIt != currentState->edges.end()) {
      nextState = nextStateIt->second;
    }
    oneTokenStringBuilder.append((byte) ch);
    currentState = nextState;
    if (!nextState) {// 不通
      break;
    }
    if (FaStateType::isClosingTag(currentState->type)) {// 找到终态
      firstTerminalState = currentState;
      byteBufferedInputStream.mark();
      break;
    }
    readResult = byteBufferedInputStream.read();
    if (!readResult.isOk) {
      return BuildOneTokenMethodResult::generateIoErrorResult();
    }
    ch = readResult.data;
  }
  if (!firstTerminalState) {
    std::string tokenStr((char *) (oneTokenStringBuilder.buffer),
                         oneTokenStringBuilder.position);
    return BuildOneTokenMethodResult::generateTokenParseErrorResult(
        new BuildOneTokenMethodTokenGeneratorErrorData{
            startIndexOfToken,
            startIndexOfToken + oneTokenStringBuilder.length(), tokenStr});
  }
  // 重复嗅探更高优先级或贪婪
  int lengthOfToken = oneTokenStringBuilder.length();
  // heaviest terminal state
  const TokenDfaState *heaviestTerminalState = firstTerminalState;
  readResult = byteBufferedInputStream.read();
  if (!readResult.isOk) {
    return BuildOneTokenMethodResult::generateIoErrorResult();
  }
  ch = readResult.data;
  // 如果没有文本嗅探了直接跳出循环
  // 如果当前接受状态是acceptWhenFirstArriveAtTerminalState，就直接接受,跳出嗅探循环
  while (ch != eof) {
    TokenDfaState *nextState = nullptr;
    auto nextStateIt = currentState->edges.find(ch);
    if (nextStateIt != currentState->edges.end()) {
      nextState = nextStateIt->second;
    }
    oneTokenStringBuilder.append((byte) ch);
    currentState = nextState;
    if (!nextState) {// 不通
      break;
    }
    if (FaStateType::isClosingTag(
            currentState->type)) {// 找到终态
                                  // 新状态具有更高优先级的，接受终态转移
      bool isHigherPriority =
          currentState->weight > heaviestTerminalState->weight;
      // 相同优先级说明状态是同一个token的终态
      // 如果是贪婪的，则增加识别的字符，接受终态转移
      // 不是贪婪的，则不接受终态转移
      bool isSameAndGreediness =
          heaviestTerminalState->terminal == currentState->terminal && ((TerminalGrammar *) heaviestTerminalState->terminal)->lookaheadMatchingMode == LookaheadMatchingMode::GREEDINESS;
      // 新状态具有更高优先级的，接受状态转移
      if (isHigherPriority || isSameAndGreediness) {
        heaviestTerminalState = currentState;
        lengthOfToken = oneTokenStringBuilder.length();
        byteBufferedInputStream.mark();
      }
      // 新token优先级更低直接被覆盖，不接受替换旧终态
    }
    readResult = byteBufferedInputStream.read();
    if (!readResult.isOk) {
      return BuildOneTokenMethodResult::generateIoErrorResult();
    }
    ch = readResult.data;
  }
  byteBufferedInputStream.reset();
  oneTokenStringBuilder.setPosition(lengthOfToken);
  // build token result
  std::string text((char *) oneTokenStringBuilder.buffer,
                   oneTokenStringBuilder.length());
  auto type = TokenTypeNamespace::getByGrammarAction(
      heaviestTerminalState->terminal->action);
  auto *token = new Token(*heaviestTerminalState->terminal, startIndexOfToken,
                          text, type);
  return BuildOneTokenMethodResult::generateTokenResult(token);
}

BuildOneTokenMethodResult::BuildOneTokenMethodResult(
    BuildOneTokenMethodResultType type, void *data)
    : type(type), data(data) {}
BuildOneTokenMethodResult::~BuildOneTokenMethodResult() {
  switch (type) {
    case BuildOneTokenMethodResultType::
        TOKEN:                                                  // delete data by TokensResult,DfaTokenAutomata::buildToken
    case BuildOneTokenMethodResultType::ALL_TEXT_HAS_BEEN_BUILT:// delete nothing
    case BuildOneTokenMethodResultType::IO_ERROR:               // delete nothing
      // delete nothing
      break;
    case BuildOneTokenMethodResultType::TOKEN_PARSE_ERROR: {
      auto tokenGeneratorErrorData =
          getBuildOneTokenMethodTokenGeneratorErrorData();
      delete tokenGeneratorErrorData;
      break;
    }
  }
}

BuildOneTokenMethodResult *
BuildOneTokenMethodResult::generateTokenResult(Token *data) {
  return new BuildOneTokenMethodResult(BuildOneTokenMethodResultType::TOKEN,
                                       data);
}
Token *BuildOneTokenMethodResult::getTokenData() const {
  return static_cast<Token *>(data);
}

BuildOneTokenMethodResult *
BuildOneTokenMethodResult::generateAllTextHasBeenBuiltResult() {
  return new BuildOneTokenMethodResult(
      BuildOneTokenMethodResultType::ALL_TEXT_HAS_BEEN_BUILT, nullptr);
}

BuildOneTokenMethodResult *
BuildOneTokenMethodResult::generateTokenParseErrorResult(
    BuildOneTokenMethodTokenGeneratorErrorData *data) {
  return new BuildOneTokenMethodResult(
      BuildOneTokenMethodResultType::TOKEN_PARSE_ERROR, data);
}
BuildOneTokenMethodTokenGeneratorErrorData *
BuildOneTokenMethodResult::getBuildOneTokenMethodTokenGeneratorErrorData()
    const {
  return static_cast<BuildOneTokenMethodTokenGeneratorErrorData *>(data);
}

BuildOneTokenMethodResult *BuildOneTokenMethodResult::generateIoErrorResult() {
  return new BuildOneTokenMethodResult(BuildOneTokenMethodResultType::IO_ERROR,
                                       nullptr);
}
