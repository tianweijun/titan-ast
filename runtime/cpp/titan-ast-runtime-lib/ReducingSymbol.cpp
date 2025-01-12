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

ReducingSymbol *ReducingSymbol::cloneForAstAutomata() const {
  auto *reducingSymbol = new ReducingSymbol();
  reducingSymbol->endIndexOfToken = this->endIndexOfToken;
  reducingSymbol->currentDfaState = this->currentDfaState;
  reducingSymbol->astOfCurrentDfaState =
      this->astOfCurrentDfaState->cloneForAstAutomata();
  return reducingSymbol;
}

// for BacktrackingBottomUpAstAutomata.triedBottomUpBranchs(set)
int ReducingSymbol::compare(const ReducingSymbol *that) const {
  int compare = endIndexOfToken - that->endIndexOfToken;
  if (0 != compare) {
    return compare;
  }

  compare = currentDfaState->index - that->currentDfaState->index;
  if (0 != compare) {
    return compare;
  }

  return astOfCurrentDfaState->grammar->index - that->astOfCurrentDfaState->grammar->index;
}
