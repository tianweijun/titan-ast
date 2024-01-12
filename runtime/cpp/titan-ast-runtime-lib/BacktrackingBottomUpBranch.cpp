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

BacktrackingBottomUpBranch *BacktrackingBottomUpBranch::clone() const {
  auto *bottomUpBranch = new BacktrackingBottomUpBranch();

  for (auto reducingSymbol : reducingSymbols) {
    bottomUpBranch->reducingSymbols.push_back(reducingSymbol->clone());
  }
  return bottomUpBranch;
}

// for BacktrackingBottomUpAstAutomata.triedBottomUpBranchs(set)
bool BacktrackingBottomUpBranch::compare(
    const BacktrackingBottomUpBranch *o) const {

  if (this->reducingSymbols.size() != o->reducingSymbols.size()) {
    return this->reducingSymbols.size() < o->reducingSymbols.size();
  }
  auto thisReducingSymbolsIt = this->reducingSymbols.begin();
  auto oReducingSymbolsIt = o->reducingSymbols.begin();
  while (thisReducingSymbolsIt != this->reducingSymbols.end()) {
    ReducingSymbol *thisReducingSymbol = *thisReducingSymbolsIt;
    ReducingSymbol *oReducingSymbol = *oReducingSymbolsIt;
    if (!thisReducingSymbol->equals(oReducingSymbol)) {
      return thisReducingSymbol->compare(oReducingSymbol);
    }
    thisReducingSymbolsIt++;
    oReducingSymbolsIt++;
  }
  return false;
}

bool BacktrackingBottomUpBranch::equals(
    const BacktrackingBottomUpBranch *o) const {
  if (this->reducingSymbols.size() != o->reducingSymbols.size()) {
    return false;
  }
  auto thisReducingSymbolsIt = this->reducingSymbols.begin();
  auto oReducingSymbolsIt = o->reducingSymbols.begin();
  while (thisReducingSymbolsIt != this->reducingSymbols.end()) {
    ReducingSymbol *thisReducingSymbol = *thisReducingSymbolsIt;
    ReducingSymbol *oReducingSymbol = *oReducingSymbolsIt;
    if (!thisReducingSymbol->equals(oReducingSymbol)) {
      return false;
    }
    thisReducingSymbolsIt++;
    oReducingSymbolsIt++;
  }
  return true;
}

size_t BacktrackingBottomUpBranch::hashCode() const {
  size_t reducingSymbolsHashCode = 0;
  for (auto reducingSymbol : reducingSymbols) {
    reducingSymbolsHashCode += reducingSymbol->hashCode();
  }
  return std::hash<size_t>()(reducingSymbolsHashCode);
}

bool BacktrackingBottomUpBranch::operator==(
    const BacktrackingBottomUpBranch &o) const {
  return equals(&o);
}
