//
// Created by tian wei jun on 2022/12/8 0008.
//

#ifndef AST__AUTOMATATMPAST_H_
#define AST__AUTOMATATMPAST_H_

#include "Ast.h"
#include "AutomataTmpToken.h"
#include "Grammar.h"
#include "Token.h"
#include <list>
#include <map>
#include <string>

class AutomataTmpAst {
public:
  AutomataTmpAst(const Grammar *grammar, const std::string *alias);
  explicit AutomataTmpAst(const AutomataTmpToken *token);
  AutomataTmpAst(const AutomataTmpAst &automataTmpAst) = delete;
  AutomataTmpAst(const AutomataTmpAst &&automataTmpAst) = delete;
  ~AutomataTmpAst();

  const AutomataTmpAst *clone() const;

  bool equals(const AutomataTmpAst *o) const;
  bool compare(const AutomataTmpAst *o) const;
  size_t hashCode() const;

  const Grammar *const grammar;
  const std::string *const alias;
  // grammar.type == GrammarType.TERMINAL
  const AutomataTmpToken *token;
  std::list<AutomataTmpAst *> children;
  Ast *toAst() const;
};

#endif // AST__AUTOMATATMPAST_H_
