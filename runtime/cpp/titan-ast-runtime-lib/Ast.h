//
// Created by tian wei jun on 2022/11/17 0017.
//

#ifndef AST__RUNTIME__AST_H_
#define AST__RUNTIME__AST_H_

#include "AstToken.h"
#include "ProductionRule.h"
#include "Runtime.h"
#include "Token.h"
#include "AstGrammar.h"
#include <map>
#include <string>
#include <vector>

class DLL_PUBLIC Ast {
public:
  explicit Ast(AstGrammar grammar);
  Ast(AstGrammar grammar, std::string alias);
  Ast(const Ast &ast) = delete;
  Ast(const Ast &&ast) = delete;
  ~Ast();

  std::string toString() const;

  AstGrammar grammar;
  std::string alias;
  // grammar.type == GrammarType.TERMINAL
  AstToken token;
  std::vector<Ast *> children;
};

#endif // AST__RUNTIME__AST_H_
