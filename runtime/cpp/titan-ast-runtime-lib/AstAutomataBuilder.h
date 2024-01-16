//
// Created by tian wei jun on 2024/1/16.
//

#ifndef TITAN_AST_RUNTIME_RUNTIME_ASTAUTOMATABUILDER_H_
#define TITAN_AST_RUNTIME_RUNTIME_ASTAUTOMATABUILDER_H_

#include "AstAutomata.h"
#include "PersistentObject.h"
class AstAutomataBuilder {
public:
  AstAutomata *build(PersistentObject *ptrPersistentObject);
};

#endif // TITAN_AST_RUNTIME_RUNTIME_ASTAUTOMATABUILDER_H_
