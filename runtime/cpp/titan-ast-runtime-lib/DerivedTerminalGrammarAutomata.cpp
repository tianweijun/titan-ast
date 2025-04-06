//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "DerivedTerminalGrammarAutomata.h"

DerivedTerminalGrammarAutomata::DerivedTerminalGrammarAutomata(
    const DerivedTerminalGrammarAutomataData
        *derivedTerminalGrammarAutomataData,
    const TokenDfa *tokenDfa)
    : DfaTokenAutomata(tokenDfa) {
  rootTerminalGrammarMap = std::unordered_map<
      const Grammar *,
      const std::unordered_map<std::string *, Grammar *, TextTerminalMapHash,
                         TextTerminalMapEq> *,
      PtrGrammarContentHash, PtrGrammarContentEq>(
      derivedTerminalGrammarAutomataData->count);
  for (auto &map :
       derivedTerminalGrammarAutomataData->rootTerminalGrammarMaps) {
    const std::pair<const Grammar *,
              const std::unordered_map<std::string *, Grammar *, TextTerminalMapHash,
                                 TextTerminalMapEq> *>
        pair(map.rootTerminalGrammar, &map.textTerminalMap);
    rootTerminalGrammarMap.insert(pair);
  }
}

// dfa delete by PersistentObject.tokenDfa
// derivedTerminalGrammarAutomataData delete by
// PersistentObject.derivedTerminalGrammarAutomataData tokens delete by caller
DerivedTerminalGrammarAutomata::~DerivedTerminalGrammarAutomata() = default;

TokensResult *
DerivedTerminalGrammarAutomata::buildToken(const std::string *sourceFilePath) {
  auto tokensResult = DfaTokenAutomata::buildToken(sourceFilePath);
  if (tokensResult->isOk()) {
    buildTokenByDerivedTerminalGrammarAutomata(tokensResult->getOkData());
  }
  return tokensResult;
}
std::vector<Token *> *DerivedTerminalGrammarAutomata::buildTokenByDerivedTerminalGrammarAutomata(
    std::vector<Token *> *tokens) const {
  for (auto token : *tokens) {
    auto rootTerminalGrammarMapIt = rootTerminalGrammarMap.find(&token->terminal);
    if (rootTerminalGrammarMapIt != rootTerminalGrammarMap.end()) {
      auto textTerminalMap = rootTerminalGrammarMapIt->second;
      auto terminalIt = textTerminalMap->find(&token->text);
      if (terminalIt != textTerminalMap->end()) {
        auto terminal = terminalIt->second;
        token->terminal = *terminal;
      }
    }
  }
  return tokens;
}




