//
// Created by tian wei jun on 2024/1/16.
//

#include "AstAutomataBuilder.h"
#include "BacktrackingBottomUpAstAutomata.h"
#include "FollowFilterBacktrackingBottomUpAstAutomata.h"

AstAutomata *AstAutomataBuilder::build(AutomataData *automataData) {
  AstAutomata *astAutomata = nullptr;
  switch (automataData->astAutomataType) {
    case AstAutomataType::BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
      astAutomata = new BacktrackingBottomUpAstAutomata(
          automataData->astDfa, automataData->startGrammar,
          automataData->grammars, automataData->sizeOfGramamrs);
      break;
    case AstAutomataType::FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
      astAutomata = new FollowFilterBacktrackingBottomUpAstAutomata(
          automataData->astDfa, automataData->startGrammar,
          automataData->grammars, automataData->sizeOfGramamrs,
          automataData->nonterminalFollowMap, automataData->eofGrammar);
      break;
  }
  return astAutomata;
}
