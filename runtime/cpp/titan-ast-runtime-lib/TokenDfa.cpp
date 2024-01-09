//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "TokenDfa.h"

TokenDfa::TokenDfa(const TokenDfaState *start, const TokenDfaState **states,
                   int sizeOfStates)
    : start(start), states(states), sizeOfStates(sizeOfStates) {}

TokenDfa::~TokenDfa() {
  for (int i = 0; i < sizeOfStates; i++) {
    const TokenDfaState *tokenDfaState = states[i];
    delete tokenDfaState;
  }
  delete[] states;
}
