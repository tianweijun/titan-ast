//
// Created by tian wei jun on 2024/1/10.
//

#include "TokenAutomataBuilder.h"
#include "KeyWordDfaTokenAutomata.h"

TokenAutomataBuilder::TokenAutomataBuilder() = default;

TokenAutomata *TokenAutomataBuilder::build(AutomataData *automataData) {
  const KeyWordAutomata *keyWordAutomata = automataData->keyWordAutomata;
  const TokenDfa *tokenDfa = automataData->tokenDfa;
  TokenAutomata *tokenAutomata = nullptr;
  if (keyWordAutomata->emptyOrNot == KeyWordAutomata::EMPTY) {
    tokenAutomata = new DfaTokenAutomata(tokenDfa);
  }
  if (keyWordAutomata->emptyOrNot == KeyWordAutomata::NOT_EMPTY) {
    tokenAutomata = new KeyWordDfaTokenAutomata(keyWordAutomata, tokenDfa);
  }
  return tokenAutomata;
}
