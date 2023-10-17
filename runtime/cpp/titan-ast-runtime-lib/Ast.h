//
// Created by tian wei jun on 2022/11/17 0017.
//

#ifndef AST__RUNTIME__AST_H_
#define AST__RUNTIME__AST_H_

#include "Grammar.h"
#include "ProductionRule.h"
#include "Token.h"
#include "AstToken.h"
#include <list>
#include <map>
#include <string>
#include "Runtime.h"

class DLL_PUBLIC Ast {
 public:
  Ast(Grammar grammar);
  Ast(Grammar grammar, std::string alias);
  explicit Ast(const Token &token);
  Ast(const Ast &ast) = delete;
  Ast(const Ast &&ast) = delete;
  ~Ast();

  std::string toString() const;

  Grammar grammar;
  std::string alias;
  // grammar.type == GrammarType.TERMINAL
  AstToken token;
  std::list<Ast *> children;
};

#endif//AST__RUNTIME__AST_H_
