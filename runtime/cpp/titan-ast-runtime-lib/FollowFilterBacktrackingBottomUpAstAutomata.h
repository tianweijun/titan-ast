//
// Created by tian wei jun on 2024/1/16.
//

#ifndef AST_RUNTIME_RUNTIME_FOLLOWFILTERBACKTRACKINGBOTTOMUPASTAUTOMATA_H_
#define AST_RUNTIME_RUNTIME_FOLLOWFILTERBACKTRACKINGBOTTOMUPASTAUTOMATA_H_

#include "BacktrackingBottomUpAstAutomata.h"
#include <unordered_set>

class FollowFilterBacktrackingBottomUpAstAutomata
    : public BacktrackingBottomUpAstAutomata {

 public:
  FollowFilterBacktrackingBottomUpAstAutomata(
      const SyntaxDfa *astDfa, const Grammar *startGrammar,
      Grammar **innerGrammars, int countOfInnerGrammars,
      const std::unordered_map<
          const Grammar *,
          std::unordered_set<const Grammar *, PtrGrammarContentHash,
                             PtrGrammarContentEq> *,
          PtrGrammarContentHash, PtrGrammarContentEq> *nonterminalFollowMap,
      const Grammar *eofGrammar);

  AstAutomataType getType() override;

 protected:
  void
  reduceBottomUpBranch(BacktrackingBottomUpBranch *bottomUpBranch) override;

 public:
  const std::unordered_map<
      const Grammar *,
      std::unordered_set<const Grammar *, PtrGrammarContentHash,
                         PtrGrammarContentEq> *,
      PtrGrammarContentHash, PtrGrammarContentEq> *nonterminalFollowMap;
  const Grammar *eofGrammar;
};

#endif// AST_RUNTIME_RUNTIME_FOLLOWFILTERBACKTRACKINGBOTTOMUPASTAUTOMATA_H_
