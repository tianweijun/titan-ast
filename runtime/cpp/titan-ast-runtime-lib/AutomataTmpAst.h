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
  explicit AutomataTmpAst(const Grammar *grammar);
  AutomataTmpAst(const AutomataTmpAst &automataTmpAst) = delete;
  AutomataTmpAst(const AutomataTmpAst &&automataTmpAst) = delete;
  virtual ~AutomataTmpAst();

  virtual const AutomataTmpAst *cloneForAstAutomata() const = 0;
  virtual Ast *toAst() const = 0;

  const Grammar *const grammar;
  std::list<AutomataTmpAst *> children;
};

class TerminalAutomataTmpAst : public AutomataTmpAst {
 public:
  explicit TerminalAutomataTmpAst(const Grammar *grammar,
                                  const AutomataTmpToken *token);
  TerminalAutomataTmpAst(const TerminalAutomataTmpAst &automataTmpAst) = delete;
  TerminalAutomataTmpAst(const TerminalAutomataTmpAst &&automataTmpAst) =
      delete;

  const AutomataTmpAst *cloneForAstAutomata() const override;
  Ast *toAst() const override;

  const AutomataTmpToken *token;
};

class NonterminalAutomataTmpAst : public AutomataTmpAst {
 public:
  NonterminalAutomataTmpAst(const Grammar *grammar, const std::string *alias);
  NonterminalAutomataTmpAst(const NonterminalAutomataTmpAst &automataTmpAst) =
      delete;
  NonterminalAutomataTmpAst(const NonterminalAutomataTmpAst &&automataTmpAst) =
      delete;

  const AutomataTmpAst *cloneForAstAutomata() const override;
  Ast *toAst() const override;

  const std::string *alias;
};

#endif// AST__AUTOMATATMPAST_H_
