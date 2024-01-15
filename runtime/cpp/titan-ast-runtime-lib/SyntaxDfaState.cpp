//
// Created by tian wei jun on 2022/12/1 0001.
//

#include "SyntaxDfaState.h"

int SyntaxDfaState::syntaxDfaStateId = 0;

SyntaxDfaState::SyntaxDfaState()
    : type(0), edges(std::map<const Grammar *, SyntaxDfaState *>()),
      closingProductionRules(std::list<ProductionRule *>()) {
  id = ++syntaxDfaStateId;
}

bool SyntaxDfaState::equals(const SyntaxDfaState *o) const {
  return this->id == o->id;
}
bool SyntaxDfaState::compare(const SyntaxDfaState *o) const {
  return this->id < o->id;
}

// edges grammar delete by PersistentData.gammars
// edges SyntaxDfaState delete by dfa
// closingProductionRules delete by  PersistentData.productionRules
SyntaxDfaState::~SyntaxDfaState() = default;