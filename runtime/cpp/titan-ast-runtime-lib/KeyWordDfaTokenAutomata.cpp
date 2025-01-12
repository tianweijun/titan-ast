//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "KeyWordDfaTokenAutomata.h"

KeyWordDfaTokenAutomata::KeyWordDfaTokenAutomata(
    const KeyWordAutomata *keyWordAutomata, const TokenDfa *tokenDfa)
    : DfaTokenAutomata(tokenDfa) {
  this->keyWordAutomata = keyWordAutomata;
}

// dfa delete by PersistentObject.tokenDfa
// keyWordAutomata delete by PersistentObject.keyWordAutomata
// tokens delete by caller
KeyWordDfaTokenAutomata::~KeyWordDfaTokenAutomata() = default;

TokensResult *
KeyWordDfaTokenAutomata::buildToken(const std::string *sourceFilePath) {
  auto tokensResult = DfaTokenAutomata::buildToken(sourceFilePath);
  if (tokensResult->isOk()) {
    keyWordAutomata->buildToken(tokensResult->getOkData());
  }
  return tokensResult;
}
