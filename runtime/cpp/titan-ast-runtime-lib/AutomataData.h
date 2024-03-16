//
// Created by tian wei jun on 2024/3/15.
//

#ifndef TITAN_AST_RUNTIME_RUNTIME_AUTOMATADATA_H_
#define TITAN_AST_RUNTIME_RUNTIME_AUTOMATADATA_H_

#include <set>
#include "Grammar.h"
#include "KeyWordAutomata.h"
#include "SyntaxDfa.h"
#include "TokenDfa.h"
#include "AstAutomataType.h"

class AutomataData {
public:
  AutomataData();
  AutomataData(const AutomataData &automataData) = delete;
  AutomataData(const AutomataData &&automataData) = delete;
  ~AutomataData();

  //matadata
  std::string **stringPool;
  int sizeOfStringPool;
  Grammar **grammars;
  int sizeOfGramamrs;
  ProductionRule **productionRules;
  int sizeOfProductionRules;

  //token dfa
  const KeyWordAutomata *keyWordAutomata;
  const TokenDfa *tokenDfa;
  //ast dfa
  AstAutomataType astAutomataType;
  const SyntaxDfa *astDfa;
  const Grammar *startGrammar;

  const Grammar *eofGrammar;
  const std::map<const Grammar *,
                 std::set<const Grammar *, PtrGrammarContentCompare> *,
                 PtrGrammarContentCompare> *nonterminalFollowMap;
};

#endif // TITAN_AST_RUNTIME_RUNTIME_AUTOMATADATA_H_
