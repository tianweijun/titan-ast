//
// Created by tian wei jun on 2022/12/4 0004.
//

#ifndef AST__BACKTRACKINGBOTTOMUPBRANCH_H_
#define AST__BACKTRACKINGBOTTOMUPBRANCH_H_

#include "ReducingSymbol.h"
#include <list>

class BacktrackingBottomUpBranch {
 public:
  BacktrackingBottomUpBranch();
  BacktrackingBottomUpBranch(
      const BacktrackingBottomUpBranch &backtrackingBottomUpBranch) = delete;
  BacktrackingBottomUpBranch(
      const BacktrackingBottomUpBranch &&backtrackingBottomUpBranch) = delete;
  ~BacktrackingBottomUpBranch();

  BacktrackingBottomUpBranch *cloneForAstAutomata() const;
  // for BacktrackingBottomUpAstAutomata.triedBottomUpBranchs(set)
  bool compare(const BacktrackingBottomUpBranch *o) const;

  std::list<ReducingSymbol *> reducingSymbols;
};

#endif// AST__BACKTRACKINGBOTTOMUPBRANCH_H_
