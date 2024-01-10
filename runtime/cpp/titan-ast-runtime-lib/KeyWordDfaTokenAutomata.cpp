//
// Created by tian wei jun on 2022/11/25 0025.
//

#include "KeyWordDfaTokenAutomata.h"
#include "AstRuntimeException.h"

KeyWordDfaTokenAutomata::KeyWordDfaTokenAutomata(
    const KeyWordAutomata *keyWordAutomata, const TokenDfa *tokenDfa)
    : DfaTokenAutomata(tokenDfa) {
  this->keyWordAutomata = keyWordAutomata;
}

// dfa delete by PersistentObject.tokenDfa
// keyWordAutomata delete by PersistentObject.keyWordAutomata
// tokens delete by caller
KeyWordDfaTokenAutomata::~KeyWordDfaTokenAutomata() = default;

std::list<Token *> *
KeyWordDfaTokenAutomata::buildToken(const std::string *sourceFilePath) {
  auto ret = DfaTokenAutomata::buildToken(sourceFilePath);
  return keyWordAutomata->buildToken(ret);
}
