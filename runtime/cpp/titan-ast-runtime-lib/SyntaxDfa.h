//
// Created by tian wei jun on 2022/11/25 0025.
//

#ifndef AST__SYNTAXDFA_H_
#define AST__SYNTAXDFA_H_

#include "SyntaxDfaState.h"

class SyntaxDfaState;

class SyntaxDfa {
 public:
  SyntaxDfa(const SyntaxDfaState *start, const SyntaxDfaState **states,
            const int sizeOfStates);
  SyntaxDfa(const SyntaxDfa &syntaxDfa) = delete;
  SyntaxDfa(const SyntaxDfa &&syntaxDfa) = delete;
  ~SyntaxDfa();

  const SyntaxDfaState *const start;
  const SyntaxDfaState **const states;
  const int sizeOfStates;
};

#endif// AST__SYNTAXDFA_H_
