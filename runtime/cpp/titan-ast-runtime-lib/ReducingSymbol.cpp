//
// Created by tian wei jun on 2022/12/4 0004.
//

#include "ReducingSymbol.h"
#include <unordered_set>

ReducingSymbol::ReducingSymbol()
    : endIndexOfToken(-1), astOfCurrentDfaState(nullptr),
      currentDfaState(nullptr) {}

ReducingSymbol::~ReducingSymbol() {
  // reducedGrammar delete by PersistentData.grammars
  // currentDfaState delete by SyntaxDfa
  delete astOfCurrentDfaState;
  astOfCurrentDfaState = nullptr;
}

ReducingSymbol *ReducingSymbol::clone() const {
  auto *reducingSymbol = new ReducingSymbol();
  reducingSymbol->endIndexOfToken = this->endIndexOfToken;
  reducingSymbol->currentDfaState = this->currentDfaState;
  reducingSymbol->astOfCurrentDfaState = this->astOfCurrentDfaState->clone();
  return reducingSymbol;
}

// for BacktrackingBottomUpAstAutomata.triedBottomUpBranchs(set)
bool ReducingSymbol::equals(const ReducingSymbol *o) const {
  // o->reducedGrammar->name == this->reducedGrammar->name;
  return this->endIndexOfToken == o->endIndexOfToken &&
         this->currentDfaState == o->currentDfaState &&
         this->astOfCurrentDfaState->equals(o->astOfCurrentDfaState);
}

// for BacktrackingBottomUpAstAutomata.triedBottomUpBranchs(set)
bool ReducingSymbol::compare(const ReducingSymbol *o) const {
  if (this->astOfCurrentDfaState->grammar != o->astOfCurrentDfaState->grammar) {
    return reinterpret_cast<uintptr_t>(this->astOfCurrentDfaState->grammar) <
           reinterpret_cast<uintptr_t>(o->astOfCurrentDfaState->grammar);
  }

  return reinterpret_cast<uintptr_t>(this->currentDfaState) <
         reinterpret_cast<uintptr_t>(o->currentDfaState);
}

size_t ReducingSymbol::hashCode() const {
  size_t hashCode = (endIndexOfToken & 0xFF) << 16;
  hashCode += (((long)currentDfaState) & 0xFF) << 8;
  hashCode += (astOfCurrentDfaState->hashCode() & 0xFF);
  return hashCode;
}
