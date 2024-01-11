//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "BacktrackingBottomUpAstAutomata.h"
#include "AstRuntimeException.h"
#include "AutomataTmpAst.h"
#include "FaStateType.h"

#include <iostream>
#include <sstream>
#include <string>

size_t BacktrackingBottomUpHash::operator()(
    const BacktrackingBottomUpBranch *t) const {
  return t->hashCode();
}

bool BacktrackingBottomUpEqual::operator()(
    const BacktrackingBottomUpBranch *t1,
    const BacktrackingBottomUpBranch *t2) const {
  return t1->equals(t2);
}

BacktrackingBottomUpAstAutomata::BacktrackingBottomUpAstAutomata(
    const SyntaxDfa *astDfa, const Grammar *startGrammar,
    Grammar **innerGrammars, int countOfInnerGrammars)
    : tokenReducingSymbolInputStream(
          TokenReducingSymbolInputStream(innerGrammars, countOfInnerGrammars)),
      bottomUpBranchs(std::list<BacktrackingBottomUpBranch *>()),
      bottomUpBranchsShadow(std::unordered_set<BacktrackingBottomUpBranch *,
                                               BacktrackingBottomUpHash,
                                               BacktrackingBottomUpEqual>()),
      triedBottomUpBranchs(std::unordered_set<BacktrackingBottomUpBranch *,
                                              BacktrackingBottomUpHash,
                                              BacktrackingBottomUpEqual>()),
      astDfa(astDfa), startGrammar(startGrammar), result(std::list<Ast *>()) {}

BacktrackingBottomUpAstAutomata::~BacktrackingBottomUpAstAutomata() {
  // astDfa delete by PersistentObject.astDfa
  // startGrammar delete by persistentData.grammars
  // result delete by caller
  clear();
}
void BacktrackingBottomUpAstAutomata::clear() {
  result.clear();
  tokenReducingSymbolInputStream.clear();

  for (auto backtrackingBottomUpBranch : bottomUpBranchs) {
    delete backtrackingBottomUpBranch;
    backtrackingBottomUpBranch = nullptr;
  }
  bottomUpBranchs.clear();
  bottomUpBranchsShadow.clear();

  for (auto backtrackingBottomUpBranch : triedBottomUpBranchs) {
    delete backtrackingBottomUpBranch;
    backtrackingBottomUpBranch = nullptr;
  }
  triedBottomUpBranchs.clear();
}

const std::list<Ast *> *
BacktrackingBottomUpAstAutomata::buildAsts(std::list<Token *> *sourceTokens) {
  init(sourceTokens);
  while (!bottomUpBranchs.empty()) {
    consumeBottomUpBranch();
  }

  if (result.empty()) {
    AstRuntimeExceptionResolver::throwException(AstRuntimeException(
        AstRuntimeExceptionCode::RUNTIME_ERROR, getNoResultErrorInfo()));
  }

  auto *ret = new std::list<Ast *>();
  for (auto ast : result) {
    ret->push_back(ast);
  }
  clear();
  return ret;
}

const Ast *
BacktrackingBottomUpAstAutomata::buildAst(std::list<Token *> *sourceTokens) {
  init(sourceTokens);
  while (result.empty() && !bottomUpBranchs.empty()) {
    consumeBottomUpBranch();
  }

  if (result.empty()) {
    AstRuntimeExceptionResolver::throwException(AstRuntimeException(
        AstRuntimeExceptionCode::RUNTIME_ERROR, getNoResultErrorInfo()));
  }

  const Ast *ret = result.empty() ? nullptr : result.front();
  clear();
  return ret;
}

void BacktrackingBottomUpAstAutomata::consumeBottomUpBranch() {
  BacktrackingBottomUpBranch *bottomUpBranch = bottomUpBranchs.front();
  bottomUpBranchs.pop_front();
  bottomUpBranchsShadow.erase(bottomUpBranch);
  auto triedBottomUpBranchsIt = triedBottomUpBranchs.find(bottomUpBranch);
  if (triedBottomUpBranchsIt != triedBottomUpBranchs.end()) {
    delete bottomUpBranch;
    bottomUpBranch = nullptr;
    return;
  }
  auto triedBottomUpBranch = bottomUpBranch->clone();
  switch (bottomUpBranch->status) {
  case BacktrackingBottomUpBranchStatus::CREATED:
    reduceBottomUpBranch(bottomUpBranch);
    bottomUpBranchs.push_front(bottomUpBranch);
    bottomUpBranchsShadow.insert(bottomUpBranch);
    break;
  case BacktrackingBottomUpBranchStatus::REDUCED:
    shiftBottomUpBranch(bottomUpBranch);
    bottomUpBranchs.push_front(bottomUpBranch);
    bottomUpBranchsShadow.insert(bottomUpBranch);
    break;
  case BacktrackingBottomUpBranchStatus::SHIFTED:
    closeBottomUpBranch(bottomUpBranch);
    bottomUpBranchs.push_front(bottomUpBranch);
    bottomUpBranchsShadow.insert(bottomUpBranch);
    break;
  case BacktrackingBottomUpBranchStatus::NON_ACCEPTED:
    delete bottomUpBranch;
    bottomUpBranch = nullptr;
    break;
  case BacktrackingBottomUpBranchStatus::ACCEPTED:
    const AutomataTmpAst *automataTmpAst =
        bottomUpBranch->reducingSymbols.back()->astOfCurrentDfaState;
    result.push_back(automataTmpAst->toAst());

    delete bottomUpBranch;
    bottomUpBranch = nullptr;
    break;
  }
  triedBottomUpBranchs.insert(triedBottomUpBranch);
}

void BacktrackingBottomUpAstAutomata::reduceBottomUpBranch(
    BacktrackingBottomUpBranch *bottomUpBranch) {
  ReducingSymbol *topReducingSymbol = bottomUpBranch->reducingSymbols.back();
  const SyntaxDfaState *currentDfaState = topReducingSymbol->currentDfaState;
  if (!currentDfaState->closingProductionRules.empty()) {
    auto &closingProductionRules = currentDfaState->closingProductionRules;
    for (auto closingProductionRule : closingProductionRules) {
      doReduce(bottomUpBranch, closingProductionRule);
    }
  }
  bottomUpBranch->status = BacktrackingBottomUpBranchStatus::REDUCED;
}

void BacktrackingBottomUpAstAutomata::doReduce(
    BacktrackingBottomUpBranch *bottomUpBranch,
    ProductionRule *closingProductionRule) {
  int endIndexOfToken = bottomUpBranch->reducingSymbols.back()->endIndexOfToken;
  // 空归约
  if (FaStateType::isClosingTag(
          closingProductionRule->reducingDfa->start->type)) {
    const SyntaxDfaState *topReducingSymbolDfaState =
        bottomUpBranch->reducingSymbols.back()->currentDfaState;
    SyntaxDfaState *nextDfaState = nullptr;
    auto nextDfaStateIt =
        topReducingSymbolDfaState->edges.find(closingProductionRule->grammar);
    if (nextDfaStateIt != topReducingSymbolDfaState->edges.end()) {
      nextDfaState = nextDfaStateIt->second;
    }
    // 连通的
    if (nextDfaState) {
      BacktrackingBottomUpBranch *newBottomUpBranch = bottomUpBranch->clone();
      newBottomUpBranch->status = BacktrackingBottomUpBranchStatus::CREATED;
      // 归约的符号
      auto *nonterminalReducingSymbol = new ReducingSymbol();
      nonterminalReducingSymbol->reducedGrammar =
          closingProductionRule->grammar;
      nonterminalReducingSymbol->astOfCurrentDfaState = new AutomataTmpAst(
          closingProductionRule->grammar, closingProductionRule->alias);
      nonterminalReducingSymbol->currentDfaState = nextDfaState;
      nonterminalReducingSymbol->endIndexOfToken = endIndexOfToken;
      // 归约的符号进栈
      newBottomUpBranch->reducingSymbols.push_back(nonterminalReducingSymbol);
      if (!addNewBacktrackingBottomUpBranch(newBottomUpBranch)) {
        delete newBottomUpBranch;
      }
    }
  }
  // 非空归约
  const SyntaxDfaState *reducingProductionRuleDfaState =
      closingProductionRule->reducingDfa->start;
  int countOfComsumedReducingSymbol = 0;
  auto reducingSymbolListIt = bottomUpBranch->reducingSymbols.rbegin();
  while (reducingSymbolListIt != bottomUpBranch->reducingSymbols.rend()) {
    // 读取一个归约符号
    ReducingSymbol *inputReducingSymbol = *reducingSymbolListIt;
    ++countOfComsumedReducingSymbol;
    if (countOfComsumedReducingSymbol >=
        bottomUpBranch->reducingSymbols.size()) { // 栈顶都没有，直接结束
      break;
    }
    SyntaxDfaState *nextReducingProductionRuleDfaState = nullptr;
    auto nextReducingProductionRuleDfaStateIt =
        reducingProductionRuleDfaState->edges.find(
            inputReducingSymbol->reducedGrammar);
    if (nextReducingProductionRuleDfaStateIt !=
        reducingProductionRuleDfaState->edges.end()) {
      nextReducingProductionRuleDfaState =
          nextReducingProductionRuleDfaStateIt->second;
    }
    if (!nextReducingProductionRuleDfaState) { // 无法按照产生式向前归约，结束
      break;
    }
    if (FaStateType::isClosingTag(nextReducingProductionRuleDfaState->type)) {
      auto topReducingSymbolDfaStateIt = next(reducingSymbolListIt);
      const SyntaxDfaState *topReducingSymbolDfaState =
          (*topReducingSymbolDfaStateIt)->currentDfaState;
      SyntaxDfaState *nextDfaState = nullptr;
      auto nextDfaStateIt =
          topReducingSymbolDfaState->edges.find(closingProductionRule->grammar);
      if (nextDfaStateIt != topReducingSymbolDfaState->edges.end()) {
        nextDfaState = nextDfaStateIt->second;
      }
      // 连通的
      if (nextDfaState) {
        BacktrackingBottomUpBranch *newBottomUpBranch = bottomUpBranch->clone();
        newBottomUpBranch->status = BacktrackingBottomUpBranchStatus::CREATED;
        // 被归约的符号出栈，同时建立语法树孩子节点
        auto *reducingAst = new AutomataTmpAst(closingProductionRule->grammar,
                                               closingProductionRule->alias);
        for (int countOfReducingSymbol = 1;
             countOfReducingSymbol <= countOfComsumedReducingSymbol;
             countOfReducingSymbol++) {
          ReducingSymbol *childReducingSymbol =
              newBottomUpBranch->reducingSymbols.back();
          auto *childOfReducingAst = const_cast<AutomataTmpAst *>(
              childReducingSymbol->astOfCurrentDfaState->clone());
          reducingAst->children.push_front(childOfReducingAst);

          newBottomUpBranch->reducingSymbols.pop_back();
          delete childReducingSymbol;
        }
        // 归约的符号
        auto *nonterminalReducingSymbol = new ReducingSymbol();
        nonterminalReducingSymbol->reducedGrammar =
            closingProductionRule->grammar;
        nonterminalReducingSymbol->endIndexOfToken = endIndexOfToken;
        nonterminalReducingSymbol->currentDfaState = nextDfaState;
        nonterminalReducingSymbol->astOfCurrentDfaState = reducingAst;
        // 归约的符号进栈
        newBottomUpBranch->reducingSymbols.push_back(nonterminalReducingSymbol);
        if (!addNewBacktrackingBottomUpBranch(newBottomUpBranch)) {
          delete newBottomUpBranch;
        }
      }
    }
    reducingProductionRuleDfaState = nextReducingProductionRuleDfaState;
    ++reducingSymbolListIt;
  }
}

void BacktrackingBottomUpAstAutomata::shiftBottomUpBranch(
    BacktrackingBottomUpBranch *bottomUpBranch) {
  ReducingSymbol *topReducingSymbol = bottomUpBranch->reducingSymbols.back();
  // 将输入流定位到分支读取的位置
  tokenReducingSymbolInputStream.nextReadIndex =
      topReducingSymbol->endIndexOfToken + 1;
  // 移进一个token
  if (tokenReducingSymbolInputStream.hasNext()) {
    AutomataTmpToken *token = tokenReducingSymbolInputStream.read();
    SyntaxDfaState *nextDfaState = nullptr;
    auto nextDfaStateIt =
        topReducingSymbol->currentDfaState->edges.find(token->terminal);
    if (nextDfaStateIt != topReducingSymbol->currentDfaState->edges.end()) {
      nextDfaState = nextDfaStateIt->second;
    }
    // 连通的
    if (nextDfaState) {
      BacktrackingBottomUpBranch *terminalBottomUpBranch =
          bottomUpBranch->clone();
      terminalBottomUpBranch->status =
          BacktrackingBottomUpBranchStatus::CREATED;
      // 归约的符号
      auto *terminalReducingSymbol = new ReducingSymbol();
      terminalReducingSymbol->reducedGrammar = token->terminal;
      terminalReducingSymbol->astOfCurrentDfaState = new AutomataTmpAst(token);
      terminalReducingSymbol->currentDfaState = nextDfaState;
      terminalReducingSymbol->endIndexOfToken =
          tokenReducingSymbolInputStream.nextReadIndex - 1;
      // 归约的符号进栈
      terminalBottomUpBranch->reducingSymbols.push_back(terminalReducingSymbol);
      if (!addNewBacktrackingBottomUpBranch(terminalBottomUpBranch)) {
        delete terminalBottomUpBranch;
      }
    }
  }
  bottomUpBranch->status = BacktrackingBottomUpBranchStatus::SHIFTED;
}

void BacktrackingBottomUpAstAutomata::closeBottomUpBranch(
    BacktrackingBottomUpBranch *bottomUpBranch) {
  ReducingSymbol *topReducingSymbol = bottomUpBranch->reducingSymbols.back();
  tokenReducingSymbolInputStream.nextReadIndex =
      topReducingSymbol->endIndexOfToken + 1;
  bottomUpBranch->status = BacktrackingBottomUpBranchStatus::NON_ACCEPTED;
  // 可接受状态:栈中有两个归约，栈底是基准标志，栈顶是归约结果，并且源文件输入流全部识别了
  if (tokenReducingSymbolInputStream.hasReadAll() &&
      bottomUpBranch->reducingSymbols.size() == 2 &&
      startGrammar == topReducingSymbol->reducedGrammar) {
    bottomUpBranch->status = BacktrackingBottomUpBranchStatus::ACCEPTED;
  }
}

void BacktrackingBottomUpAstAutomata::init(std::list<Token *> *sourceTokens) {
  clear();
  tokenReducingSymbolInputStream.init(sourceTokens);

  ReducingSymbol *connectedSignOfStartGrammarReducingSymbol =
      getConnectedSignOfStartGrammarReducingSymbol();

  auto *beginningBottomUpBranch = new BacktrackingBottomUpBranch();
  beginningBottomUpBranch->reducingSymbols.push_back(
      connectedSignOfStartGrammarReducingSymbol);
  if (!addNewBacktrackingBottomUpBranch(beginningBottomUpBranch)) {
    delete beginningBottomUpBranch;
  }
}

ReducingSymbol *BacktrackingBottomUpAstAutomata::
    getConnectedSignOfStartGrammarReducingSymbol() {
  auto *connectedSignOfStartGrammarReducingSymbol = new ReducingSymbol();
  connectedSignOfStartGrammarReducingSymbol->reducedGrammar = startGrammar;
  connectedSignOfStartGrammarReducingSymbol->astOfCurrentDfaState =
      new AutomataTmpAst(startGrammar, nullptr);
  connectedSignOfStartGrammarReducingSymbol->endIndexOfToken = -1;
  connectedSignOfStartGrammarReducingSymbol->currentDfaState = astDfa->start;
  return connectedSignOfStartGrammarReducingSymbol;
}

bool BacktrackingBottomUpAstAutomata::addNewBacktrackingBottomUpBranch(
    BacktrackingBottomUpBranch *newBacktrackingBottomUpBranch) {
  auto findTriedBottomUpBranchsIt =
      triedBottomUpBranchs.find(newBacktrackingBottomUpBranch);
  if (findTriedBottomUpBranchsIt != triedBottomUpBranchs.end()) {
    return false;
  }

  auto findBottomUpBranchsShadowIt =
      triedBottomUpBranchs.find(newBacktrackingBottomUpBranch);
  if (findBottomUpBranchsShadowIt == bottomUpBranchsShadow.end()) {
    bottomUpBranchs.push_front(newBacktrackingBottomUpBranch);
    bottomUpBranchsShadow.insert(newBacktrackingBottomUpBranch);
    return true;
  }
  return false;
}
std::string BacktrackingBottomUpAstAutomata::getNoResultErrorInfo() {
  auto sizeOfTokens = tokenReducingSymbolInputStream.sizeOfTokenReducingSymbols;
  int indexOfLastToken = sizeOfTokens <= 0 ? 0 : sizeOfTokens - 1;

  int startIndexOfToken = indexOfLastToken;
  int endIndexOfToken = 0;
  for (auto branch : triedBottomUpBranchs) {
    int lastIndexOfBranch = branch->reducingSymbols.back()->endIndexOfToken;
    if (startIndexOfToken > lastIndexOfBranch) {
      startIndexOfToken = lastIndexOfBranch;
    }
    if (endIndexOfToken < lastIndexOfBranch) {
      endIndexOfToken = lastIndexOfBranch;
    }
  }
  if (startIndexOfToken == indexOfLastToken || startIndexOfToken < 0) {
    startIndexOfToken = 0;
  }
  if (endIndexOfToken == 0) {
    endIndexOfToken = indexOfLastToken > 1 ? 1 : indexOfLastToken;
  } else {
    if (endIndexOfToken + 1 < indexOfLastToken) {
      endIndexOfToken += 1;
    }
  }

  int startIndexByte = 0;
  int endIndexByte = 0;

  std::stringstream tokenInfo;
  if (sizeOfTokens > 0) {
    auto tokenReducingSymbols =
        tokenReducingSymbolInputStream.tokenReducingSymbols;
    AutomataTmpToken *startToken = &tokenReducingSymbols[startIndexOfToken];
    AutomataTmpToken *endToken = &tokenReducingSymbols[endIndexOfToken];
    startIndexByte = startToken->start;
    endIndexByte = endToken->start + endToken->text->length();

    for (int indexOfToken = startIndexOfToken; indexOfToken <= endIndexOfToken;
         indexOfToken++) {
      AutomataTmpToken *token = &tokenReducingSymbols[indexOfToken];
      tokenInfo << *(token->text) << " ";
    }
  }
  auto strTokenInfo = tokenInfo.str();
  if (!strTokenInfo.empty()) {
    strTokenInfo.pop_back();
  }

  std::stringstream errorInfo;
  errorInfo << "generate ast failed,error near [" << startIndexByte << ","
            << endIndexByte << "):" << strTokenInfo;
  return errorInfo.str();
}
