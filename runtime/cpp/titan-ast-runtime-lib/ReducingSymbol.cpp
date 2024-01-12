//
// Created by tian wei jun on 2022/12/4 0004.
//

#include "ReducingSymbol.h"
#include <unordered_set>

ReducingSymbol::ReducingSymbol()
    : endIndexOfToken(-1), reducedGrammar(nullptr),
      astOfCurrentDfaState(nullptr), currentDfaState(nullptr) {}

ReducingSymbol::~ReducingSymbol() {
  // reducedGrammar delete by PersistentData.grammars
  // currentDfaState delete by SyntaxDfa
  delete astOfCurrentDfaState;
  astOfCurrentDfaState = nullptr;
}

ReducingSymbol *ReducingSymbol::clone() const {
  auto *reducingSymbol = new ReducingSymbol();
  reducingSymbol->endIndexOfToken = this->endIndexOfToken;
  reducingSymbol->reducedGrammar = this->reducedGrammar;
  reducingSymbol->currentDfaState = this->currentDfaState;
  reducingSymbol->astOfCurrentDfaState = this->astOfCurrentDfaState->clone();
  return reducingSymbol;
}

// for BacktrackingBottomUpAstAutomata.triedBottomUpBranchs(set)
bool ReducingSymbol::equals(const ReducingSymbol *o) const {
  // this->reducedGrammar->type == o->reducedGrammar->type &&
  // o->reducedGrammar->name == this->reducedGrammar->name;
  return this->reducedGrammar == o->reducedGrammar &&
         this->endIndexOfToken == o->endIndexOfToken &&
         this->currentDfaState == o->currentDfaState &&
         this->astOfCurrentDfaState->equals(o->astOfCurrentDfaState);
}

// for BacktrackingBottomUpAstAutomata.triedBottomUpBranchs(set)
bool ReducingSymbol::compare(const ReducingSymbol *o) const {
  if (this->endIndexOfToken != o->endIndexOfToken) {
    return this->endIndexOfToken < o->endIndexOfToken;
  }

  if (this->reducedGrammar != o->reducedGrammar) {
    return this->reducedGrammar < o->reducedGrammar;
  }

  if (this->currentDfaState != o->currentDfaState) {
    return this->currentDfaState < o->currentDfaState;
  }

  if (!this->astOfCurrentDfaState->equals(o->astOfCurrentDfaState)) {
    return this->astOfCurrentDfaState->compare(o->astOfCurrentDfaState);
  }

  return false;
}
size_t ReducingSymbol::hashCode() const {
  size_t hashCode = (endIndexOfToken & 0xFF) << 24;
  hashCode += (((long)reducedGrammar) & 0xFF) << 16;
  hashCode += (((long)currentDfaState) & 0xFF) << 8;
  hashCode += (astOfCurrentDfaState->hashCode() & 0xFF);
  return hashCode;
}
