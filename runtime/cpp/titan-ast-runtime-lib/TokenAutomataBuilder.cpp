//
// Created by tian wei jun on 2024/1/10.
//

#include "TokenAutomataBuilder.h"
#include "DerivedTerminalGrammarAutomata.h"
#include "SingleDerivedTerminalGrammarAutomata.h"

TokenAutomataBuilder::TokenAutomataBuilder() = default;

TokenAutomata *TokenAutomataBuilder::build(AutomataData *automataData) {
  const DerivedTerminalGrammarAutomataData *derivedTerminalGrammarAutomataData
      = automataData->derivedTerminalGrammarAutomataData;
  const TokenDfa *tokenDfa = automataData->tokenDfa;
  TokenAutomata *tokenAutomata = nullptr;
  if (derivedTerminalGrammarAutomataData->count==0) {
    tokenAutomata = new DfaTokenAutomata(tokenDfa);
  }else if (derivedTerminalGrammarAutomataData->count==1) {
    tokenAutomata = new SingleDerivedTerminalGrammarAutomata(derivedTerminalGrammarAutomataData, tokenDfa);
  }else{
    tokenAutomata = new DerivedTerminalGrammarAutomata(derivedTerminalGrammarAutomataData, tokenDfa);
  }
  return tokenAutomata;
}
