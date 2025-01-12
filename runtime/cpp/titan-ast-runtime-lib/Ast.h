//
// Created by tian wei jun on 2022/11/17 0017.
//

#ifndef AST__RUNTIME__AST_H_
#define AST__RUNTIME__AST_H_

#include "AstGrammar.h"
#include "AstToken.h"
#include "ProductionRule.h"
#include "Runtime.h"
#include "Token.h"
#include <map>
#include <string>
#include <vector>

class DLL_PUBLIC Ast {
 public:
  explicit Ast(AstGrammar grammar);
  Ast(Ast &ast) = delete;
  Ast(Ast &&ast) = delete;
  virtual ~Ast();

  virtual std::string toString() const = 0;

  const AstGrammar grammar;
  const std::vector<Ast *> children;
};

class DLL_PUBLIC TerminalAst : public Ast {
 public:
  TerminalAst(AstGrammar grammar, AstToken token);
  TerminalAst(TerminalAst &ast) = delete;
  TerminalAst(TerminalAst &&ast) = delete;

  std::string toString() const override;

  const AstToken token;
};

class DLL_PUBLIC NonterminalAst : public Ast {
 public:
  NonterminalAst(AstGrammar grammar, std::string alias);
  NonterminalAst(NonterminalAst &ast) = delete;
  NonterminalAst(NonterminalAst &&ast) = delete;

  std::string toString() const override;

  const std::string alias;
};

#endif// AST__RUNTIME__AST_H_
