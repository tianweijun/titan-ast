//
// Created by tian wei jun on 2022/11/25 0025.
//

#ifndef AST__BACKTRACKINGBOTTOMUPASTAUTOMATA_H_
#define AST__BACKTRACKINGBOTTOMUPASTAUTOMATA_H_

#include "Ast.h"
#include "BacktrackingBottomUpBranch.h"
#include "Grammar.h"
#include "SyntaxDfa.h"
#include "Token.h"
#include "TokenReducingSymbolInputStream.h"
#include <list>
#include <unordered_set>

class BacktrackingBottomUpHash {
public:
  size_t operator()(const BacktrackingBottomUpBranch *t) const;
};
class BacktrackingBottomUpEqual {
public:
  bool operator()(const BacktrackingBottomUpBranch *t1,
                  const BacktrackingBottomUpBranch *t2) const;
};

class BacktrackingBottomUpAstAutomata {
public:
  BacktrackingBottomUpAstAutomata(const SyntaxDfa *astDfa,
                                  const Grammar *startGrammar,
                                  Grammar **innerGrammars,
                                  int countOfInnerGrammars);
  BacktrackingBottomUpAstAutomata(const BacktrackingBottomUpAstAutomata &
                                      backtrackingBottomUpAstAutomata) = delete;
  BacktrackingBottomUpAstAutomata(const BacktrackingBottomUpAstAutomata &&
                                      backtrackingBottomUpAstAutomata) = delete;
  ~BacktrackingBottomUpAstAutomata();

  const Ast *buildAst(std::list<Token *> *sourceTokens);

  const std::list<Ast *> *buildAsts(std::list<Token *> *sourceTokens);

private:
  void init(std::list<Token *> *sourceTokens);
  ReducingSymbol *getConnectedSignOfStartGrammarReducingSymbol();
  bool addNewBacktrackingBottomUpBranch(
      BacktrackingBottomUpBranch *newBacktrackingBottomUpBranch);
  void consumeBottomUpBranch();
  void closeBottomUpBranch(BacktrackingBottomUpBranch *bottomUpBranch);
  void shiftBottomUpBranch(BacktrackingBottomUpBranch *bottomUpBranch);
  void reduceBottomUpBranch(BacktrackingBottomUpBranch *bottomUpBranch);
  void doReduce(BacktrackingBottomUpBranch *bottomUpBranch,
                ProductionRule *closingProductionRule);
  void clear();

private:
  TokenReducingSymbolInputStream tokenReducingSymbolInputStream;
  std::list<BacktrackingBottomUpBranch *> bottomUpBranchs;
  std::unordered_set<BacktrackingBottomUpBranch *, BacktrackingBottomUpHash,
                     BacktrackingBottomUpEqual>
      bottomUpBranchsShadow;
  std::unordered_set<BacktrackingBottomUpBranch *, BacktrackingBottomUpHash,
                     BacktrackingBottomUpEqual>
      triedBottomUpBranchs;

  const SyntaxDfa *astDfa;
  const Grammar *startGrammar;
  std::list<Ast *> result;
};

#endif // AST__BACKTRACKINGBOTTOMUPASTAUTOMATA_H_
