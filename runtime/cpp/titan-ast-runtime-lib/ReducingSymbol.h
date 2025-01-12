//
// Created by tian wei jun on 2022/12/4 0004.
//

#ifndef AST__REDUCINGSYMBOL_H_
#define AST__REDUCINGSYMBOL_H_

#include "AutomataTmpAst.h"
#include "Grammar.h"
#include "SyntaxDfaState.h"

class ReducingSymbol {
 public:
  ReducingSymbol();
  ReducingSymbol(const ReducingSymbol &reducingSymbol) = delete;
  ReducingSymbol(const ReducingSymbol &&reducingSymbol) = delete;
  ~ReducingSymbol();

  // for BacktrackingBottomUpAstAutomata.triedBottomUpBranchs(set)
  int compare(const ReducingSymbol *that) const;
  ReducingSymbol *cloneForAstAutomata() const;
  // ast
  const AutomataTmpAst *astOfCurrentDfaState;
  // 状态
  const SyntaxDfaState *currentDfaState;
  // token流中的位置
  int endIndexOfToken;
};

#endif// AST__REDUCINGSYMBOL_H_
