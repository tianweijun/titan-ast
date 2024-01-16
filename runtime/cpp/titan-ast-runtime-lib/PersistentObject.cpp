//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "PersistentObject.h"
#include "AstAutomataType.h"

PersistentObject::PersistentObject()
    : persistentData(nullptr), keyWordAutomata(nullptr), tokenDfa(nullptr),
      astAutomataType(AstAutomataType::BACKTRACKING_BOTTOM_UP_AST_AUTOMATA),
      astDfa(nullptr), startGrammar(nullptr), eofGrammar(nullptr),
      nonterminalFollowMap(nullptr) {}

PersistentObject::PersistentObject(PersistentData *persistentData)
    : persistentData(persistentData), keyWordAutomata(nullptr),
      astAutomataType(AstAutomataType::BACKTRACKING_BOTTOM_UP_AST_AUTOMATA),
      tokenDfa(nullptr), astDfa(nullptr), startGrammar(nullptr),
      eofGrammar(nullptr), nonterminalFollowMap(nullptr) {
  init();
}

void PersistentObject::initByPersistentData(PersistentData *persistentData) {
  this->persistentData = persistentData;
  init();
}

PersistentObject::~PersistentObject() {
  delete astDfa;
  astDfa = nullptr;

  if (nullptr != nonterminalFollowMap) {
    for (auto iter = nonterminalFollowMap->begin();
         iter != nonterminalFollowMap->end(); iter++) {
      auto follows = iter->second;
      delete follows;
    }
  }
  delete nonterminalFollowMap;
  nonterminalFollowMap = nullptr;

  delete tokenDfa;
  tokenDfa = nullptr;

  delete keyWordAutomata;
  keyWordAutomata = nullptr;

  delete persistentData;
  persistentData = nullptr;
  // startGrammar delete by persistentData.grammars
}

void PersistentObject::init() {
  // 按文件组织顺序获得各个部分数据，每个部分获取一次
  initStringPool();
  initGrammars();
  initKeyWordAutomata();
  initTokenDfa();
  initProductionRules();
  initAstAutomata();

  persistentData->compact();
}

void PersistentObject::initAstAutomata() {
  astAutomataType = persistentData->getAstAutomataTypeByInputStream();
  switch (astAutomataType) {
  case AstAutomataType::BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
    initBacktrackingBottomUpAstAutomata();
    break;
  case AstAutomataType::FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
    initFollowFilterBacktrackingBottomUpAstAutomata();
    break;
  }
}

void PersistentObject::initFollowFilterBacktrackingBottomUpAstAutomata() {
  initStartGrammar();
  initAstDfa();
  initEofGrammar();
  initNonterminalFollowMap();
}

void PersistentObject::initNonterminalFollowMap() {
  nonterminalFollowMap = reinterpret_cast<
      const std::map<const Grammar *, std::set<const Grammar *, PtrGrammarCompare> *,
                     PtrGrammarCompare> *>(
      persistentData->getNonterminalFollowMapByInputStream());
}

void PersistentObject::initEofGrammar() {
  eofGrammar = persistentData->getGrammarByInputStream();
}

void PersistentObject::initBacktrackingBottomUpAstAutomata() {
  initStartGrammar();
  initAstDfa();
}

void PersistentObject::initAstDfa() {
  astDfa = persistentData->getSyntaxDfaByInputStream();
}

void PersistentObject::initProductionRules() const {
  persistentData->getProductionRulesByInputStream();
}

void PersistentObject::initStartGrammar() {
  startGrammar = persistentData->getGrammarByInputStream();
}

void PersistentObject::initTokenDfa() {
  tokenDfa = persistentData->getTokenDfaByInputStream();
}

void PersistentObject::initKeyWordAutomata() {
  keyWordAutomata = persistentData->getKeyWordAutomataByInputStream();
}

void PersistentObject::initGrammars() const {
  persistentData->getGrammarsByInputStream();
}

void PersistentObject::initStringPool() const {
  persistentData->getStringPoolByInputStream();
}
