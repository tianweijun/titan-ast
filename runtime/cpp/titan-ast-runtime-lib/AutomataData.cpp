//
// Created by tian wei jun on 2024/3/15.
//

#include "AutomataData.h"
AutomataData::AutomataData()
    : stringPool(nullptr), sizeOfStringPool(0), grammars(nullptr),
      sizeOfGramamrs(0), productionRules(nullptr), sizeOfProductionRules(0),
      derivedTerminalGrammarAutomataData(nullptr), tokenDfa(nullptr),
      astAutomataType(AstAutomataType::BACKTRACKING_BOTTOM_UP_AST_AUTOMATA),
      astDfa(nullptr), startGrammar(nullptr), eofGrammar(nullptr),
      nonterminalFollowMap(nullptr) {}

AutomataData::~AutomataData() {
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

  delete derivedTerminalGrammarAutomataData;
  derivedTerminalGrammarAutomataData = nullptr;

  // startGrammar delete by persistentData.grammars

  for (int i = 0; i < sizeOfProductionRules; i++) {
    ProductionRule *productionRule = productionRules[i];
    delete productionRule;
    productionRule = nullptr;
  }
  delete[] productionRules;
  productionRules = nullptr;

  for (int i = 0; i < sizeOfGramamrs; i++) {
    Grammar *grammar = grammars[i];
    delete grammar;
    grammar = nullptr;
  }
  delete[] grammars;
  grammars = nullptr;

  if (stringPool) {
    for (int i = 0; i < sizeOfStringPool; i++) {
      std::string *string = stringPool[i];
      delete string;
      string = nullptr;
    }
    delete[] stringPool;
    stringPool = nullptr;
  }
}
