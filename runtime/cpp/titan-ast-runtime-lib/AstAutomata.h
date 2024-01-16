//
// Created by tian wei jun on 2024/1/16.
//

#ifndef AST_RUNTIME_RUNTIME_ASTAUTOMATA_H_
#define AST_RUNTIME_RUNTIME_ASTAUTOMATA_H_
#include "Ast.h"
#include "AstAutomataType.h"
class AstAutomata {
public:
  virtual ~AstAutomata() = default;

  virtual AstAutomataType getType() = 0;

  virtual const Ast *buildAst(std::list<Token *> *sourceTokens) = 0;

  virtual const std::list<Ast *> *
  buildAsts(std::list<Token *> *sourceTokens) = 0;
};
#endif // AST_RUNTIME_RUNTIME_ASTAUTOMATA_H_
