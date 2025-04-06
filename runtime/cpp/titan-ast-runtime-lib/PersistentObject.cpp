//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "PersistentObject.h"
#include "AstAutomataType.h"

PersistentObject::PersistentObject()
    : persistentData(nullptr), derivedTerminalGrammarAutomataData(nullptr),
      tokenDfa(nullptr),
      astAutomataType(AstAutomataType::BACKTRACKING_BOTTOM_UP_AST_AUTOMATA),
      astDfa(nullptr), startGrammar(nullptr), eofGrammar(nullptr),
      nonterminalFollowMap(nullptr) {}

PersistentObject::PersistentObject(PersistentData *persistentData)
    : persistentData(persistentData),
      derivedTerminalGrammarAutomataData(nullptr),
      astAutomataType(AstAutomataType::BACKTRACKING_BOTTOM_UP_AST_AUTOMATA),
      tokenDfa(nullptr), astDfa(nullptr), startGrammar(nullptr),
      eofGrammar(nullptr), nonterminalFollowMap(nullptr) {
  init();
}

BuildAutomataResult
PersistentObject::initByPersistentData(PersistentData *persistentData) {
  this->persistentData = persistentData;
  return init();
}

PersistentObject::~PersistentObject() {
  // persistentData delete by initByPersistentData caller
  // other delete by AutomataData
}

BuildAutomataResult PersistentObject::init() {
  BuildAutomataResult buildAutomataResult;
  // 按文件组织顺序获得各个部分数据，每个部分获取一次
  buildAutomataResult = initStringPool();
  if (!buildAutomataResult.isOk) {
    persistentData->compact();
    return buildAutomataResult;
  }
  buildAutomataResult = initGrammars();
  if (!buildAutomataResult.isOk) {
    persistentData->compact();
    return buildAutomataResult;
  }
  buildAutomataResult = initDerivedTerminalGrammarAutomataData();
  if (!buildAutomataResult.isOk) {
    persistentData->compact();
    return buildAutomataResult;
  }
  buildAutomataResult = initTokenDfa();
  if (!buildAutomataResult.isOk) {
    persistentData->compact();
    return buildAutomataResult;
  }
  buildAutomataResult = initProductionRules();
  if (!buildAutomataResult.isOk) {
    persistentData->compact();
    return buildAutomataResult;
  }

  buildAutomataResult = buildAutomataResult = initAstAutomata();
  persistentData->compact();
  return buildAutomataResult;
}

BuildAutomataResult PersistentObject::initAstAutomata() {
  GetAstAutomataTypeResult getAstAutomataTypeResult =
      persistentData->getAstAutomataTypeByInputStream();
  if (!getAstAutomataTypeResult.isOk) {
    return persistentData->sourceDataError();
  }
  BuildAutomataResult buildAutomataResult;
  astAutomataType = getAstAutomataTypeResult.data;
  switch (astAutomataType) {
  case AstAutomataType::BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
    buildAutomataResult = initBacktrackingBottomUpAstAutomata();
    break;
  case AstAutomataType::FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
    buildAutomataResult = initFollowFilterBacktrackingBottomUpAstAutomata();
    break;
  }
  return buildAutomataResult;
}

BuildAutomataResult
PersistentObject::initFollowFilterBacktrackingBottomUpAstAutomata() {
  BuildAutomataResult buildAutomataResult;
  buildAutomataResult = initStartGrammar();
  if (!buildAutomataResult.isOk) {
    return buildAutomataResult;
  }
  buildAutomataResult = initAstDfa();
  if (!buildAutomataResult.isOk) {
    return buildAutomataResult;
  }
  buildAutomataResult = initEofGrammar();
  if (!buildAutomataResult.isOk) {
    return buildAutomataResult;
  }
  buildAutomataResult = initNonterminalFollowMap();
  return buildAutomataResult;
}

BuildAutomataResult PersistentObject::initNonterminalFollowMap() {
  GetNonterminalFollowMapByInputStreamResult
      getNonterminalFollowMapByInputStreamResult =
          persistentData->getNonterminalFollowMapByInputStream();
  if (!getNonterminalFollowMapByInputStreamResult.isOk) {
    return persistentData->sourceDataError();
  }
  nonterminalFollowMap = getNonterminalFollowMapByInputStreamResult.data;
  return {true, ""};
}

BuildAutomataResult PersistentObject::initEofGrammar() {
  GetGrammarByInputStreamResult getGrammarByInputStreamResult =
      persistentData->getGrammarByInputStream();
  if (!getGrammarByInputStreamResult.isOk) {
    return persistentData->sourceDataError();
  }
  eofGrammar = getGrammarByInputStreamResult.data;
  return {true, ""};
}

BuildAutomataResult PersistentObject::initBacktrackingBottomUpAstAutomata() {
  BuildAutomataResult buildAutomataResult;
  buildAutomataResult = initStartGrammar();
  if (!buildAutomataResult.isOk) {
    return buildAutomataResult;
  }
  buildAutomataResult = initAstDfa();
  return buildAutomataResult;
}

BuildAutomataResult PersistentObject::initAstDfa() {
  GetSyntaxDfaByInputStreamResult getSyntaxDfaByInputStreamResult =
      persistentData->getSyntaxDfaByInputStream();
  if (!getSyntaxDfaByInputStreamResult.isOk) {
    return persistentData->sourceDataError();
  }
  astDfa = getSyntaxDfaByInputStreamResult.data;
  return {true, ""};
}

BuildAutomataResult PersistentObject::initProductionRules() const {
  GetProductionRulesByInputStreamResult getProductionRulesByInputStreamResult =
      persistentData->getProductionRulesByInputStream();
  if (!getProductionRulesByInputStreamResult.isOk) {
    return persistentData->sourceDataError();
  }
  return {true, ""};
}

BuildAutomataResult PersistentObject::initStartGrammar() {
  GetGrammarByInputStreamResult getGrammarByInputStreamResult =
      persistentData->getGrammarByInputStream();
  if (!getGrammarByInputStreamResult.isOk) {
    return persistentData->sourceDataError();
  }
  startGrammar = getGrammarByInputStreamResult.data;
  return {true, ""};
}

BuildAutomataResult PersistentObject::initTokenDfa() {
  GetTokenDfaByInputStreamResult getTokenDfaByInputStreamResult =
      persistentData->getTokenDfaByInputStream();
  if (!getTokenDfaByInputStreamResult.isOk) {
    return persistentData->sourceDataError();
  }
  tokenDfa = getTokenDfaByInputStreamResult.data;
  return {true, ""};
}

BuildAutomataResult PersistentObject::initDerivedTerminalGrammarAutomataData() {
  GetDerivedTerminalGrammarAutomataDataByInputStreamResult result =
      persistentData->getDerivedTerminalGrammarAutomataDataByInputStream();
  if (!result.isOk) {
    return persistentData->sourceDataError();
  }
  derivedTerminalGrammarAutomataData = result.data;
  return {true, ""};
}

BuildAutomataResult PersistentObject::initGrammars() const {
  GetGrammarsByInputStreamResult getGrammarsByInputStreamResult =
      persistentData->getGrammarsByInputStream();
  if (!getGrammarsByInputStreamResult.isOk) {
    return persistentData->sourceDataError();
  }
  return {true, ""};
}

BuildAutomataResult PersistentObject::initStringPool() const {
  GetStringPoolByInputStreamResult getStringPoolByInputStreamResult =
      persistentData->getStringPoolByInputStream();
  if (!getStringPoolByInputStreamResult.isOk) {
    return persistentData->sourceDataError();
  }
  return {true, ""};
}

/**
 * all heap data is moved
 */
void *PersistentObject::setAutomataData(AutomataData *automataData) const {
  // matadata
  automataData->stringPool = this->persistentData->stringPool;
  automataData->sizeOfStringPool = this->persistentData->sizeOfStringPool;
  automataData->grammars = this->persistentData->grammars;
  automataData->sizeOfGramamrs = this->persistentData->sizeOfGramamrs;
  automataData->productionRules = this->persistentData->productionRules;
  automataData->sizeOfProductionRules =
      this->persistentData->sizeOfProductionRules;
  // token dfa
  automataData->derivedTerminalGrammarAutomataData = derivedTerminalGrammarAutomataData;
  automataData->tokenDfa = tokenDfa;
  // ast dfa
  automataData->astAutomataType = astAutomataType;
  automataData->astDfa = astDfa;
  automataData->startGrammar = startGrammar;

  automataData->eofGrammar = eofGrammar;
  automataData->nonterminalFollowMap = nonterminalFollowMap;
}
