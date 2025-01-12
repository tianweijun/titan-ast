//
// Created by tian wei jun on 2022/11/25 0025.
//

#ifndef AST__KEYWORDDFATOKENAUTOMATA_H_
#define AST__KEYWORDDFATOKENAUTOMATA_H_
#include "ByteBuffer.h"
#include "ByteBufferedInputStream.h"
#include "DfaTokenAutomata.h"
#include "KeyWordAutomata.h"
#include "Token.h"
#include "TokenDfa.h"
#include <list>
#include <string>

class KeyWordDfaTokenAutomata : public DfaTokenAutomata {
 public:
  explicit KeyWordDfaTokenAutomata(const KeyWordAutomata *keyWordAutomata,
                                   const TokenDfa *tokenDfa);
  KeyWordDfaTokenAutomata(const KeyWordDfaTokenAutomata &dfaTokenAutomata) =
      delete;
  KeyWordDfaTokenAutomata(const KeyWordDfaTokenAutomata &&dfaTokenAutomata) =
      delete;
  ~KeyWordDfaTokenAutomata() override;
  TokensResult *buildToken(const std::string *sourceFilePath) override;

 private:
  const KeyWordAutomata *keyWordAutomata;
};

#endif// AST__KEYWORDDFATOKENAUTOMATA_H_
