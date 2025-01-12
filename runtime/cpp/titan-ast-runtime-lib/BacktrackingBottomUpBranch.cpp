//
// Created by tian wei jun on 2022/12/4 0004.
//

#include "BacktrackingBottomUpBranch.h"
#include <unordered_set>

BacktrackingBottomUpBranch::BacktrackingBottomUpBranch()
    : reducingSymbols(std::list<ReducingSymbol *>()) {}

BacktrackingBottomUpBranch::~BacktrackingBottomUpBranch() {
  for (auto reducingSymbol : reducingSymbols) {
    delete reducingSymbol;
    reducingSymbol = nullptr;
  }
  reducingSymbols.clear();
}

BacktrackingBottomUpBranch *
BacktrackingBottomUpBranch::cloneForAstAutomata() const {
  auto *bottomUpBranch = new BacktrackingBottomUpBranch();

  for (auto reducingSymbol : reducingSymbols) {
    bottomUpBranch->reducingSymbols.push_back(
        reducingSymbol->cloneForAstAutomata());
  }
  return bottomUpBranch;
}

// for BacktrackingBottomUpAstAutomata.triedBottomUpBranchs(set)
bool BacktrackingBottomUpBranch::compare(
    const BacktrackingBottomUpBranch *o) const {
  if (this->reducingSymbols.back()->endIndexOfToken != o->reducingSymbols.back()->endIndexOfToken) {
    return this->reducingSymbols.back()->endIndexOfToken < o->reducingSymbols.back()->endIndexOfToken;
  }

  if (this->reducingSymbols.size() != o->reducingSymbols.size()) {
    return this->reducingSymbols.size() < o->reducingSymbols.size();
  }
  auto thisReducingSymbolsIt = this->reducingSymbols.rbegin();
  auto oReducingSymbolsIt = o->reducingSymbols.rbegin();
  while (thisReducingSymbolsIt != this->reducingSymbols.rend()) {
    ReducingSymbol *thisReducingSymbol = *thisReducingSymbolsIt;
    ReducingSymbol *oReducingSymbol = *oReducingSymbolsIt;

    auto compare = thisReducingSymbol->compare(oReducingSymbol);

    if (0 != compare) {
      return compare < 0;
    }
    thisReducingSymbolsIt++;
    oReducingSymbolsIt++;
  }
  return false;
}
