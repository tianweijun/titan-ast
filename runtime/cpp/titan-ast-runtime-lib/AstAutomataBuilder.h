//
// Created by tian wei jun on 2024/1/16.
//

#ifndef TITAN_AST_RUNTIME_RUNTIME_ASTAUTOMATABUILDER_H_
#define TITAN_AST_RUNTIME_RUNTIME_ASTAUTOMATABUILDER_H_

#include "AstAutomata.h"
#include "AutomataData.h"
class AstAutomataBuilder {
 public:
  AstAutomata *build(AutomataData *automataData);
};

#endif// TITAN_AST_RUNTIME_RUNTIME_ASTAUTOMATABUILDER_H_
