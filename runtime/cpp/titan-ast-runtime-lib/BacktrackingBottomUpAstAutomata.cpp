//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "BacktrackingBottomUpAstAutomata.h"
#include "AutomataTmpAst.h"
#include "FaStateType.h"

#include <iostream>
#include <sstream>
#include <string>

bool BacktrackingBottomUpCompare::operator()(
    const BacktrackingBottomUpBranch *t1,
    const BacktrackingBottomUpBranch *t2) const {

  return t1->compare(t2);
}

BacktrackingBottomUpAstAutomata::BacktrackingBottomUpAstAutomata(
    const SyntaxDfa *astDfa, const Grammar *startGrammar,
    Grammar **innerGrammars, int countOfInnerGrammars)
    : tokenReducingSymbolInputStream(
        TokenReducingSymbolInputStream(innerGrammars, countOfInnerGrammars)),
      bottomUpBranchs(std::set<BacktrackingBottomUpBranch *,
                               BacktrackingBottomUpCompare>()),
      triedBottomUpBranchs(std::set<BacktrackingBottomUpBranch *,
                                    BacktrackingBottomUpCompare>()),
      astDfa(astDfa), startGrammar(startGrammar), result(nullptr) {}

BacktrackingBottomUpAstAutomata::~BacktrackingBottomUpAstAutomata() {
  // astDfa delete by PersistentObject.astDfa
  // startGrammar delete by persistentData.grammars
  // result delete by caller
  clear();
}
void BacktrackingBottomUpAstAutomata::clear() {
  result = nullptr;
  tokenReducingSymbolInputStream.clear();

  for (auto backtrackingBottomUpBranch : bottomUpBranchs) {
    delete backtrackingBottomUpBranch;
    backtrackingBottomUpBranch = nullptr;
  }
  bottomUpBranchs.clear();

  for (auto backtrackingBottomUpBranch : triedBottomUpBranchs) {
    delete backtrackingBottomUpBranch;
    backtrackingBottomUpBranch = nullptr;
  }
  triedBottomUpBranchs.clear();
}

AstResult *
BacktrackingBottomUpAstAutomata::buildAst(std::list<Token *> *sourceTokens) {
  init(sourceTokens);
  while (result == nullptr && !bottomUpBranchs.empty()) {
    consumeBottomUpBranch();
  }

  AstResult *astResult = nullptr;
  if (result == nullptr) {
    astResult = AstResult::generateAstParseErrorResult(getAstParseErrorData());
  } else {
    astResult = AstResult::generateOkResult(result);
  }
  clear();
  return astResult;
}

void BacktrackingBottomUpAstAutomata::consumeBottomUpBranch() {
  BacktrackingBottomUpBranch *bottomUpBranch = *bottomUpBranchs.begin();
  bottomUpBranchs.erase(bottomUpBranch);
  auto triedBottomUpBranchsIt = triedBottomUpBranchs.find(bottomUpBranch);
  if (triedBottomUpBranchsIt != triedBottomUpBranchs.end()) {
    delete bottomUpBranch;
    bottomUpBranch = nullptr;
    return;
  }

  auto triedBottomUpBranch = bottomUpBranch->cloneForAstAutomata();
  triedBottomUpBranchs.insert(triedBottomUpBranch);

  if (isAcceptedBottomUpBranch(bottomUpBranch)) {
    const AutomataTmpAst *automataTmpAst =
        bottomUpBranch->reducingSymbols.back()->astOfCurrentDfaState;
    result = automataTmpAst->toAst();

    delete bottomUpBranch;
    bottomUpBranch = nullptr;
    return;
  }

  reduceBottomUpBranch(bottomUpBranch);
  shiftBottomUpBranch(bottomUpBranch);
  delete bottomUpBranch;
  bottomUpBranch = nullptr;
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
      BacktrackingBottomUpBranch *newBottomUpBranch =
          bottomUpBranch->cloneForAstAutomata();

      // 归约的符号
      auto *nonterminalReducingSymbol = new ReducingSymbol();
      nonterminalReducingSymbol->astOfCurrentDfaState =
          new NonterminalAutomataTmpAst(closingProductionRule->grammar,
                                        closingProductionRule->alias);
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
    if (countOfComsumedReducingSymbol >= bottomUpBranch->reducingSymbols.size()) {// 栈顶都没有，直接结束
      break;
    }
    SyntaxDfaState *nextReducingProductionRuleDfaState = nullptr;
    auto nextReducingProductionRuleDfaStateIt =
        reducingProductionRuleDfaState->edges.find(
            inputReducingSymbol->astOfCurrentDfaState->grammar);
    if (nextReducingProductionRuleDfaStateIt != reducingProductionRuleDfaState->edges.end()) {
      nextReducingProductionRuleDfaState =
          nextReducingProductionRuleDfaStateIt->second;
    }
    if (!nextReducingProductionRuleDfaState) {// 无法按照产生式向前归约，结束
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
        BacktrackingBottomUpBranch *newBottomUpBranch =
            bottomUpBranch->cloneForAstAutomata();

        // 被归约的符号出栈，同时建立语法树孩子节点
        auto *reducingAst = new NonterminalAutomataTmpAst(
            closingProductionRule->grammar, closingProductionRule->alias);
        for (int countOfReducingSymbol = 1;
             countOfReducingSymbol <= countOfComsumedReducingSymbol;
             countOfReducingSymbol++) {
          ReducingSymbol *childReducingSymbol =
              newBottomUpBranch->reducingSymbols.back();
          auto *childOfReducingAst = const_cast<AutomataTmpAst *>(
              childReducingSymbol->astOfCurrentDfaState->cloneForAstAutomata());
          reducingAst->children.push_front(childOfReducingAst);

          newBottomUpBranch->reducingSymbols.pop_back();
          delete childReducingSymbol;
        }
        // 归约的符号
        auto *nonterminalReducingSymbol = new ReducingSymbol();
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
          bottomUpBranch->cloneForAstAutomata();
      // 归约的符号
      auto *terminalReducingSymbol = new ReducingSymbol();
      terminalReducingSymbol->astOfCurrentDfaState =
          new TerminalAutomataTmpAst(token->terminal, token);
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
}

bool BacktrackingBottomUpAstAutomata::isAcceptedBottomUpBranch(
    BacktrackingBottomUpBranch *bottomUpBranch) {
  ReducingSymbol *topReducingSymbol = bottomUpBranch->reducingSymbols.back();
  tokenReducingSymbolInputStream.nextReadIndex =
      topReducingSymbol->endIndexOfToken + 1;

  // 可接受状态:栈中有两个归约，栈底是基准标志，栈顶是归约结果，并且源文件输入流全部识别了
  return tokenReducingSymbolInputStream.hasReadAll() && bottomUpBranch->reducingSymbols.size() == 2 && startGrammar == topReducingSymbol->astOfCurrentDfaState->grammar;
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
  connectedSignOfStartGrammarReducingSymbol->astOfCurrentDfaState =
      new NonterminalAutomataTmpAst(
          startGrammar,
          nullptr);// 应该是augmentedNonterminal,简化为startGrammar
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

  bool hasInsert = bottomUpBranchs.insert(newBacktrackingBottomUpBranch).second;
  if (hasInsert) {
    if (triedBottomUpBranchs.empty()) {
      return hasInsert;
    }
    int minEndIndexOfTask =
        (*bottomUpBranchs.begin())->reducingSymbols.back()->endIndexOfToken;

    auto firstTriedBranch = *triedBottomUpBranchs.begin();
    int minEndIndexOfTriedBranch =
        firstTriedBranch->reducingSymbols.back()->endIndexOfToken;
    while (minEndIndexOfTriedBranch < minEndIndexOfTask) {
      triedBottomUpBranchs.erase(firstTriedBranch);
      delete firstTriedBranch;
      if (triedBottomUpBranchs.empty()) {
        break;
      }
      firstTriedBranch = *triedBottomUpBranchs.begin();
      minEndIndexOfTriedBranch =
          firstTriedBranch->reducingSymbols.back()->endIndexOfToken;
    }
    // std::cout<<minEndIndexOfTask<<":"<<bottomUpBranchs.size()<<"
    // "<<triedBottomUpBranchs.size()<<std::endl;
  }
  return hasInsert;
}

/**
 * 错误信息最开始处，错误信息所有可能的范围.
 * @return error info
 */
AstParseErrorData *BacktrackingBottomUpAstAutomata::getAstParseErrorData() {
  auto sizeOfTokens = tokenReducingSymbolInputStream.sizeOfTokenReducingSymbols;
  if (sizeOfTokens <= 0) {
    return new AstParseErrorData(0, 0, "");
  }
  int indexOfLastToken = sizeOfTokens - 1;

  int startIndexOfToken = indexOfLastToken;
  int endIndexOfToken = 0;
  for (auto branch : triedBottomUpBranchs) {
    int lastIndexOfBranch = branch->reducingSymbols.back()->endIndexOfToken;
    if (lastIndexOfBranch < 0) {
      lastIndexOfBranch = 0;
    }
    if (startIndexOfToken > lastIndexOfBranch) {// 错误开始处尽量小
      startIndexOfToken = lastIndexOfBranch;
    }
    if (endIndexOfToken < lastIndexOfBranch) {// 错误结束处尽量大
      endIndexOfToken = lastIndexOfBranch;
    }
  }

  if (endIndexOfToken + 1 <= indexOfLastToken) {// 如果还有下一个token，将他加入过来，错误信息必须涵盖可能的位置。
    endIndexOfToken += 1;
  }

  int startIndexByte = 0;
  int endIndexByte = 0;

  std::stringstream tokenInfo;
  auto tokenReducingSymbols =
      tokenReducingSymbolInputStream.tokenReducingSymbols;
  AutomataTmpToken *startToken = &tokenReducingSymbols[startIndexOfToken];
  AutomataTmpToken *endToken = &tokenReducingSymbols[endIndexOfToken];
  startIndexByte = startToken->start;
  endIndexByte = endToken->start + (int) endToken->text->length();

  for (int indexOfToken = startIndexOfToken; indexOfToken <= endIndexOfToken;
       indexOfToken++) {
    AutomataTmpToken *token = &tokenReducingSymbols[indexOfToken];
    tokenInfo << *(token->text) << " ";
  }
  auto strTokenInfo = tokenInfo.str();
  strTokenInfo.pop_back();

  return new AstParseErrorData(startIndexByte, endIndexByte, strTokenInfo);
}

AstAutomataType BacktrackingBottomUpAstAutomata::getType() {
  return AstAutomataType::BACKTRACKING_BOTTOM_UP_AST_AUTOMATA;
}
