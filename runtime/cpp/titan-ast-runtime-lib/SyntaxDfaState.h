//
// Created by tian wei jun on 2022/12/1 0001.
//

#ifndef AST__SYNTAXDFASTATE_H_
#define AST__SYNTAXDFASTATE_H_
#include "Grammar.h"
#include "ProductionRule.h"
#include <list>
#include <map>

class ProductionRule;

class SyntaxDfaState {
 public:
  SyntaxDfaState();
  SyntaxDfaState(const SyntaxDfaState &syntaxDfaState) = delete;
  SyntaxDfaState(const SyntaxDfaState &&syntaxDfaState) = delete;
  ~SyntaxDfaState();

  int type;
  // 转移
  std::map<const Grammar *, SyntaxDfaState *> edges;
  std::list<ProductionRule *> closingProductionRules;
};

#endif//AST__SYNTAXDFASTATE_H_
