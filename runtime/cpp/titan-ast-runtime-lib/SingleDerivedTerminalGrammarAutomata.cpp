//
// Created by tian wei jun on 2025/4/6.
//

#include "SingleDerivedTerminalGrammarAutomata.h"

SingleDerivedTerminalGrammarAutomata::SingleDerivedTerminalGrammarAutomata(
    const DerivedTerminalGrammarAutomataData
        *derivedTerminalGrammarAutomataData,
    const TokenDfa *tokenDfa)
    : DfaTokenAutomata(tokenDfa) {
  auto &map =
      derivedTerminalGrammarAutomataData->rootTerminalGrammarMaps.front();
  rootTerminalGrammar = map.rootTerminalGrammar;
  textTerminalMap = &(map.textTerminalMap);
}

// dfa delete by PersistentObject.tokenDfa
// derivedTerminalGrammarAutomataData delete by
// PersistentObject.derivedTerminalGrammarAutomataData tokens delete by caller
SingleDerivedTerminalGrammarAutomata::~SingleDerivedTerminalGrammarAutomata() =
    default;

TokensResult *SingleDerivedTerminalGrammarAutomata::buildToken(
    const std::string *sourceFilePath) {
  auto tokensResult = DfaTokenAutomata::buildToken(sourceFilePath);
  if (tokensResult->isOk()) {
    buildTokenBySingleDerivedTerminalGrammarAutomata(tokensResult->getOkData());
  }
  return tokensResult;
}
std::vector<Token *> *SingleDerivedTerminalGrammarAutomata::
    buildTokenBySingleDerivedTerminalGrammarAutomata(
        std::vector<Token *> *tokens) const {
  for (auto token : *tokens) {
    if (rootTerminalGrammar->index == token->terminal.index) {
      auto terminalIt = textTerminalMap->find(&token->text);
      if (terminalIt != textTerminalMap->end()) {
        auto terminal = terminalIt->second;
        token->terminal = *terminal;
      }
    }
  }
  return tokens;
}