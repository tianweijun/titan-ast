//
// Created by tian wei jun on 2022/12/3 0003.
//
#include "TokenType.h"

TokenType TokenTypeNamespace::getByGrammarAction(GrammarAction grammarAction) {
  TokenType res = TokenType::TEXT;
  switch (grammarAction) {
    case GrammarAction::SKIP:
      res = TokenType::SKIP;
      break;
    case GrammarAction::TEXT:
    default:
      res = TokenType::TEXT;
  }
  return res;
}
