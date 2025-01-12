//
// Created by tian wei jun on 2022/11/25 0025.
//

#ifndef AST__BACKTRACKINGBOTTOMUPASTAUTOMATA_H_
#define AST__BACKTRACKINGBOTTOMUPASTAUTOMATA_H_

#include "Ast.h"
#include "AstAutomata.h"
#include "BacktrackingBottomUpBranch.h"
#include "Grammar.h"
#include "SyntaxDfa.h"
#include "Token.h"
#include "TokenReducingSymbolInputStream.h"
#include <list>
#include <set>

class BacktrackingBottomUpCompare {
 public:
  bool operator()(const BacktrackingBottomUpBranch *t1,
                  const BacktrackingBottomUpBranch *t2) const;
};

class BacktrackingBottomUpAstAutomata : public AstAutomata {
 public:
  BacktrackingBottomUpAstAutomata(const SyntaxDfa *astDfa,
                                  const Grammar *startGrammar,
                                  Grammar **innerGrammars,
                                  int countOfInnerGrammars);
  BacktrackingBottomUpAstAutomata(const BacktrackingBottomUpAstAutomata &
                                      backtrackingBottomUpAstAutomata) = delete;
  BacktrackingBottomUpAstAutomata(const BacktrackingBottomUpAstAutomata &&
                                      backtrackingBottomUpAstAutomata) = delete;
  ~BacktrackingBottomUpAstAutomata() override;

  AstAutomataType getType() override;

  AstResult *buildAst(std::list<Token *> *sourceTokens) override;

 protected:
  virtual void reduceBottomUpBranch(BacktrackingBottomUpBranch *bottomUpBranch);

  void doReduce(BacktrackingBottomUpBranch *bottomUpBranch,
                ProductionRule *closingProductionRule);

  TokenReducingSymbolInputStream tokenReducingSymbolInputStream;

 private:
  void init(std::list<Token *> *sourceTokens);
  ReducingSymbol *getConnectedSignOfStartGrammarReducingSymbol();
  bool addNewBacktrackingBottomUpBranch(
      BacktrackingBottomUpBranch *newBacktrackingBottomUpBranch);
  void consumeBottomUpBranch();
  bool isAcceptedBottomUpBranch(BacktrackingBottomUpBranch *bottomUpBranch);
  void shiftBottomUpBranch(BacktrackingBottomUpBranch *bottomUpBranch);
  void clear();
  AstParseErrorData *getAstParseErrorData();

 private:
  std::set<BacktrackingBottomUpBranch *, BacktrackingBottomUpCompare>
      bottomUpBranchs;
  std::set<BacktrackingBottomUpBranch *, BacktrackingBottomUpCompare>
      triedBottomUpBranchs;

  const SyntaxDfa *astDfa;
  const Grammar *startGrammar;
  Ast *result;
};

#endif// AST__BACKTRACKINGBOTTOMUPASTAUTOMATA_H_
