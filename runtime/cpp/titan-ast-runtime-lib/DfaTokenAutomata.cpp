//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "DfaTokenAutomata.h"
#include "AstRuntimeException.h"
#include <sstream>

DfaTokenAutomata::DfaTokenAutomata(const TokenDfa *tokenDfa)
    : dfa(tokenDfa), tokens(nullptr), oneTokenStringBuilder(256),
      startIndexOfToken(0), eof(-1) {}

// dfa delete by PersistentObject.tokenDfa
// tokens delete by caller
DfaTokenAutomata::~DfaTokenAutomata() = default;

std::list<Token *> *
DfaTokenAutomata::buildToken(const std::string *sourceFilePath) {
  byteBufferedInputStream.init(sourceFilePath);
  // byteBufferedInputStream初始化错误（可能原因：源文件不存在）
  if (AstRuntimeExceptionResolver::hasThrewException()) {
    return nullptr;
  }
  // tokens delete by caller
  this->tokens = new std::list<Token *>();
  bool hasBuildedToken = false;
  do {
    hasBuildedToken = buildOneToken();
  } while (hasBuildedToken);
  std::list<Token *> *ret = this->tokens;
  clear();
  return ret;
}

void DfaTokenAutomata::clear() {
  this->tokens = nullptr;
  byteBufferedInputStream.clear();
  oneTokenStringBuilder.clear();
}

bool DfaTokenAutomata::buildOneToken() {
  const TokenDfaState *terminalState = getTerminalState();
  if (!terminalState) { // 表示输入流读取完了，或者源输入流内容无法根据语法生成token
    return false;
  }
  std::string text((char *)oneTokenStringBuilder.buffer,
                   oneTokenStringBuilder.length());
  auto type =
      TokenTypeNamespace::getByGrammarAction(terminalState->terminal->action);
  auto *token =
      new Token(*terminalState->terminal, startIndexOfToken, text, type);
  tokens->push_back(token);

  return true;
}

/**
 * .
 * @return 返回null，表示输入流读取完了，或者源输入流内容无法根据语法生成token
 */
const TokenDfaState *DfaTokenAutomata::getTerminalState() {
  oneTokenStringBuilder.clear();
  startIndexOfToken = byteBufferedInputStream.nextReadIndex;
  int ch = byteBufferedInputStream.read();
  if (ch == eof) {
    return nullptr;
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
    oneTokenStringBuilder.append((byte)ch);
    currentState = nextState;
    if (!nextState) { // 不通
      break;
    }
    if (FaStateType::isClosingTag(currentState->type)) { // 找到终态
      firstTerminalState = currentState;
      byteBufferedInputStream.mark();
      break;
    }
    ch = byteBufferedInputStream.read();
  }
  if (!firstTerminalState) {
    std::string tokenStr((char *)(oneTokenStringBuilder.buffer),
                         oneTokenStringBuilder.limit);
    std::stringstream errorInfo;
    errorInfo << "[" << startIndexOfToken << ","
              << startIndexOfToken + oneTokenStringBuilder.length() << "):'"
              << tokenStr << "' does not match any token";
    AstRuntimeExceptionResolver::throwException(AstRuntimeException(
        AstRuntimeExceptionCode::INVALID_ARGUMENT, errorInfo.str()));
    return nullptr;
  }
  // 重复嗅探更高优先级或贪婪
  int lengthOfToken = oneTokenStringBuilder.length();
  // heaviest terminal state
  const TokenDfaState *heaviestTerminalState = firstTerminalState;
  ch = byteBufferedInputStream.read();
  // 如果没有文本嗅探了直接跳出循环
  // 如果当前接受状态是acceptWhenFirstArriveAtTerminalState，就直接接受,跳出嗅探循环
  while (ch != eof) {
    TokenDfaState *nextState = nullptr;
    auto nextStateIt = currentState->edges.find(ch);
    if (nextStateIt != currentState->edges.end()) {
      nextState = nextStateIt->second;
    }
    oneTokenStringBuilder.append((byte)ch);
    currentState = nextState;
    if (!nextState) { // 不通
      break;
    }
    if (FaStateType::isClosingTag(currentState->type)) { // 找到终态
                                                         // 新状态具有更高优先级的，接受终态转移
      bool isHigherPriority = currentState->weight > heaviestTerminalState->weight;
      // 相同优先级说明状态是同一个token的终态
      // 如果是贪婪的，则增加识别的字符，接受终态转移
      // 不是贪婪的，则不接受终态转移
      bool isSameAndGreediness =
          heaviestTerminalState->terminal == currentState->terminal &&
          ((TerminalGrammar *)heaviestTerminalState->terminal)
                  ->lookaheadMatchingMode == LookaheadMatchingMode::GREEDINESS;
      // 新状态具有更高优先级的，接受状态转移
      if (isHigherPriority || isSameAndGreediness) {
        heaviestTerminalState = currentState;
        lengthOfToken = oneTokenStringBuilder.length();
        byteBufferedInputStream.mark();
      }
      // 新token优先级更低直接被覆盖，不接受替换旧终态
    }
    ch = byteBufferedInputStream.read();
  }
  byteBufferedInputStream.reset();
  oneTokenStringBuilder.setLimit(lengthOfToken);
  return heaviestTerminalState;
}
