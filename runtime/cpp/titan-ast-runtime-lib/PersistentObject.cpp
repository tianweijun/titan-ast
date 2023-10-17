//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "PersistentObject.h"

PersistentObject::PersistentObject() : persistentData(nullptr),
                                       tokenDfa(nullptr), astDfa(nullptr),
                                       startGrammar(nullptr) {
}

PersistentObject::PersistentObject(PersistentData *persistentData) : persistentData(persistentData),
                                                                     tokenDfa(nullptr), astDfa(nullptr),
                                                                     startGrammar(nullptr) {
  init();
}

PersistentObject::~PersistentObject() {
  delete astDfa;
  astDfa = nullptr;
  delete tokenDfa;
  tokenDfa = nullptr;
  delete persistentData;
  persistentData = nullptr;
  //startGrammar delete by persistentData.grammars
}

void PersistentObject::init() {
  // 按文件组织顺序获得各个部分数据，每个部分获取一次
  initStringPool();
  initGrammars();
  initTokenDfa();
  initStartGrammar();
  initProductionRules();
  initAstDfa();

  persistentData->compact();
}

void PersistentObject::initAstDfa() {
  astDfa = persistentData->getSyntaxDfaByInputStream();
}

void PersistentObject::initProductionRules() const {
  persistentData->getProductionRulesByInputStream();
}

void PersistentObject::initStartGrammar() {
  startGrammar = persistentData->getStartGrammarByInputStream();
}

void PersistentObject::initTokenDfa() {
  tokenDfa = persistentData->getTokenDfaByInputStream();
}

void PersistentObject::initGrammars() const {
  persistentData->getGrammarsByInputStream();
}

void PersistentObject::initStringPool() const {
  persistentData->getStringPoolByInputStream();
}
