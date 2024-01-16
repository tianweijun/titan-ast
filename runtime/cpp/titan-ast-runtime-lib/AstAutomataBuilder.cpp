//
// Created by tian wei jun on 2024/1/16.
//

#include "AstAutomataBuilder.h"
#include "BacktrackingBottomUpAstAutomata.h"
#include "FollowFilterBacktrackingBottomUpAstAutomata.h"

AstAutomata *AstAutomataBuilder::build(PersistentObject *ptrPersistentObject) {
  AstAutomata *astAutomata = nullptr;
  switch (ptrPersistentObject->astAutomataType) {
  case AstAutomataType::BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
    astAutomata = new BacktrackingBottomUpAstAutomata(
        ptrPersistentObject->astDfa, ptrPersistentObject->startGrammar,
        ptrPersistentObject->persistentData->grammars,
        ptrPersistentObject->persistentData->sizeOfGramamrs);
    break;
  case AstAutomataType::FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
    astAutomata = new FollowFilterBacktrackingBottomUpAstAutomata(
        ptrPersistentObject->astDfa, ptrPersistentObject->startGrammar,
        ptrPersistentObject->persistentData->grammars,
        ptrPersistentObject->persistentData->sizeOfGramamrs,
        ptrPersistentObject->nonterminalFollowMap,
        ptrPersistentObject->eofGrammar);
    break;
  }
  return astAutomata;
}
