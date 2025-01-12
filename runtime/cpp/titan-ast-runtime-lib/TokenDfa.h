//
// Created by tian wei jun on 2022/11/25 0025.
//

#ifndef AST__TOKENDFA_H_
#define AST__TOKENDFA_H_

#include "TokenDfaState.h"

class TokenDfa {
 public:
  TokenDfa(const TokenDfaState *start, const TokenDfaState **states,
           int sizeOfStates);
  TokenDfa(const TokenDfa &tokenDfa) = delete;
  TokenDfa(const TokenDfa &&tokenDfa) = delete;
  ~TokenDfa();

  const TokenDfaState *const start;
  const TokenDfaState **const states;
  const int sizeOfStates;
};

#endif// AST__TOKENDFA_H_
