//
// Created by tian wei jun on 2022/12/1 0001.
//

#ifndef AST__SYNTAXDFASTATE_H_
#define AST__SYNTAXDFASTATE_H_
#include "Grammar.h"
#include "ProductionRule.h"
#include <unordered_map>
#include <vector>

class ProductionRule;

class SyntaxDfaState {
 public:
  SyntaxDfaState(int index);
  SyntaxDfaState(const SyntaxDfaState &syntaxDfaState) = delete;
  SyntaxDfaState(const SyntaxDfaState &&syntaxDfaState) = delete;
  ~SyntaxDfaState();

  int index;
  int type;
  // 转移
  std::unordered_map<const Grammar *, SyntaxDfaState *, PtrGrammarContentHash,
                     PtrGrammarContentEq>
      edges;
  std::vector<ProductionRule *> closingProductionRules;
};

#endif// AST__SYNTAXDFASTATE_H_
