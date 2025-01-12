//
// Created by tian wei jun on 2024/1/10.
//

#ifndef AST_RUNTIME_RUNTIME_TOKENAUTOMATABUILDER_H_
#define AST_RUNTIME_RUNTIME_TOKENAUTOMATABUILDER_H_

#include "AutomataData.h"
#include "TokenAutomata.h"

class TokenAutomataBuilder {
 public:
  TokenAutomataBuilder();
  TokenAutomata *build(AutomataData *automataData);
};

#endif// AST_RUNTIME_RUNTIME_TOKENAUTOMATABUILDER_H_
