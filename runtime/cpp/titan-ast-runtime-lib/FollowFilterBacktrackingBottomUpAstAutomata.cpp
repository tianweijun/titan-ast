//
// Created by tian wei jun on 2024/1/16.
//

#include "FollowFilterBacktrackingBottomUpAstAutomata.h"
FollowFilterBacktrackingBottomUpAstAutomata::
    FollowFilterBacktrackingBottomUpAstAutomata(
        const SyntaxDfa *astDfa, const Grammar *startGrammar,
        Grammar **innerGrammars, int countOfInnerGrammars,
        const std::unordered_map<
            const Grammar *,
            std::unordered_set<const Grammar *, PtrGrammarContentHash,
                               PtrGrammarContentEq> *,
            PtrGrammarContentHash, PtrGrammarContentEq> *nonterminalFollowMap,
        const Grammar *eofGrammar)
    : BacktrackingBottomUpAstAutomata(astDfa, startGrammar, innerGrammars,
                                      countOfInnerGrammars),
      nonterminalFollowMap(nonterminalFollowMap), eofGrammar(eofGrammar) {}

void FollowFilterBacktrackingBottomUpAstAutomata::reduceBottomUpBranch(
    BacktrackingBottomUpBranch *bottomUpBranch) {
  ReducingSymbol *topReducingSymbol = bottomUpBranch->reducingSymbols.back();
  const SyntaxDfaState *currentDfaState = topReducingSymbol->currentDfaState;
  if (!currentDfaState->closingProductionRules.empty()) {

    tokenReducingSymbolInputStream.nextReadIndex =
        topReducingSymbol->endIndexOfToken + 1;

    const Grammar *terminalOfNextToken = nullptr;     // 下一个token的语法名字
    if (tokenReducingSymbolInputStream.hasReadAll()) {// token读完了,相当于eof
      terminalOfNextToken = eofGrammar;
    } else {
      terminalOfNextToken = tokenReducingSymbolInputStream.read()->terminal;
    }

    auto &closingProductionRules = currentDfaState->closingProductionRules;
    for (auto closingProductionRule : closingProductionRules) {

      const Grammar *nonterminal = closingProductionRule->grammar;
      auto follow = nonterminalFollowMap->find(nonterminal)->second;

      if (follow->find(terminalOfNextToken) != follow->end()) {
        doReduce(bottomUpBranch, closingProductionRule);
      }
    }
  }
}
AstAutomataType FollowFilterBacktrackingBottomUpAstAutomata::getType() {
  return AstAutomataType::FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA;
}
