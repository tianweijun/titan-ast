//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "DfaTokenAutomata.h"
#include "AstRuntimeException.h"

DfaTokenAutomata::DfaTokenAutomata(const TokenDfa *tokenDfa) : dfa(tokenDfa),
                                                               tokens(nullptr),
                                                               oneTokenStringBuilder(256),
                                                               startIndexOfToken(0),
                                                               eof(-1){
}

//dfa delete by PersistentObject.tokenDfa
//tokens delete by caller
DfaTokenAutomata::~DfaTokenAutomata() = default;

std::list<Token *> *DfaTokenAutomata::buildToken(const std::string *sourceFilePath) {
  byteBufferedInputStream.init(sourceFilePath);
  // byteBufferedInputStream初始化错误（可能原因：源文件不存在）
  if (AstRuntimeExceptionResolver::hasThrewException()) {
    return nullptr;
  }
  //tokens delete by caller
  this->tokens = new std::list<Token *>();
  bool hasBuildedToken = false;
  do {
    hasBuildedToken = buildOneToken();
  } while (hasBuildedToken);
  std::list<Token *> * ret = this->tokens;
  clear();
  return ret;
}

void DfaTokenAutomata::clear(){
  this->tokens = nullptr;
  byteBufferedInputStream.clear();
  oneTokenStringBuilder.clear();
}

bool DfaTokenAutomata::buildOneToken() {
  const TokenDfaState *terminalState = getTerminalState();
  if (!terminalState) {// 输入流读完结束
    return false;
  }
  std::string text((char *) oneTokenStringBuilder.buffer, oneTokenStringBuilder.length());
  auto type = TokenTypeNamespace::getByGrammarAction(terminalState->terminal->action);
  auto *token = new Token(*terminalState->terminal, startIndexOfToken, text, type);
  tokens->push_back(token);

  return true;
}

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
    ch = byteBufferedInputStream.read();
  }
  if (!firstTerminalState) {
    std::string tokenStr((char *) (oneTokenStringBuilder.buffer), oneTokenStringBuilder.position);
    AstRuntimeExceptionResolver::throwException(
        AstRuntimeException(AstRuntimeExceptionCode::INVALID_ARGUMENT,
                                     "'" + tokenStr + "' does not match any token"));
    return nullptr;
  }
  // 重复嗅探更高优先级或贪婪
  int lengthOfToken = oneTokenStringBuilder.length();
  // heaviest terminal state
  const TokenDfaState *heaviestTerminalState = firstTerminalState;
  auto* terminal = (TerminalGrammar*) heaviestTerminalState->terminal;
  ch = byteBufferedInputStream.read();
  // 如果没有文本嗅探了直接跳出循环
  // 如果当前接受状态是acceptWhenFirstArriveAtTerminalState，就直接接受,跳出嗅探循环
  while (ch != eof  &&
         terminal->lookaheadMatchingMode != LookaheadMatchingMode::ACCEPT_WHEN_FIRST_ARRIVE_AT_TERMINAL_STATE) {
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
      // 新状态具有更高优先级的，接受状态转移
      if (currentState->weight > heaviestTerminalState->weight) {
        heaviestTerminalState = currentState;
        lengthOfToken = oneTokenStringBuilder.length();
        byteBufferedInputStream.mark();
      }
      // 相同优先级说明状态是同一个token的终态
      // 如果是贪婪的，则增加识别的字符，接受状态转移
      // 不是贪婪的，则不接受状态转移
      if (currentState->weight == heaviestTerminalState->weight
          && terminal == heaviestTerminalState->terminal) {
        if (terminal->lookaheadMatchingMode == LookaheadMatchingMode::GREEDINESS) {
          heaviestTerminalState = currentState;
          terminal = (TerminalGrammar*) heaviestTerminalState->terminal;
          lengthOfToken = oneTokenStringBuilder.length();
          byteBufferedInputStream.mark();
        }
      }
      // 新token优先级更低直接被覆盖，不接受替换旧终态
    }
    ch = byteBufferedInputStream.read();
  }
  byteBufferedInputStream.reset();
  oneTokenStringBuilder.setPosition(lengthOfToken);
  return heaviestTerminalState;
}
