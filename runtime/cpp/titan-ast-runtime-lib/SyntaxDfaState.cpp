//
// Created by tian wei jun on 2022/12/1 0001.
//

#include "SyntaxDfaState.h"

SyntaxDfaState::SyntaxDfaState(int index)
    : index(index), type(0),
      edges(std::unordered_map<const Grammar *, SyntaxDfaState *,
                               PtrGrammarContentHash, PtrGrammarContentEq>()),
      closingProductionRules(std::vector<ProductionRule *>()) {}

// edges grammar delete by PersistentData.gammars
// edges SyntaxDfaState delete by dfa
// closingProductionRules delete by  PersistentData.productionRules
SyntaxDfaState::~SyntaxDfaState() = default;