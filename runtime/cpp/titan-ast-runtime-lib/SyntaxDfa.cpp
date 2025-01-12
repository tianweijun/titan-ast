//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "SyntaxDfa.h"

SyntaxDfa::SyntaxDfa(const SyntaxDfaState *start, const SyntaxDfaState **states,
                     const int sizeOfStates)
    : start(start), states(states), sizeOfStates(sizeOfStates) {}

SyntaxDfa::~SyntaxDfa() {
  for (int i = 0; i < sizeOfStates; i++) {
    const SyntaxDfaState *syntaxDfaState = states[i];
    delete syntaxDfaState;
    syntaxDfaState = nullptr;
  }
  delete[] states;
}