//
// Created by tian wei jun on 2022/11/25 0025.
//

#ifndef AST__DFATOKENAUTOMATA_H_
#define AST__DFATOKENAUTOMATA_H_
#include "ByteBuffer.h"
#include "ByteBufferedInputStream.h"
#include "KeyWordAutomata.h"
#include "Token.h"
#include "TokenAutomata.h"
#include "TokenDfa.h"
#include <list>
#include <string>

class DfaTokenAutomata : public TokenAutomata {
public:
  explicit DfaTokenAutomata(const TokenDfa *tokenDfa);
  DfaTokenAutomata(const DfaTokenAutomata &dfaTokenAutomata) = delete;
  DfaTokenAutomata(const DfaTokenAutomata &&dfaTokenAutomata) = delete;
  ~DfaTokenAutomata() override;
  std::list<Token *> *buildToken(const std::string *sourceFilePath) override;

private:
  const TokenDfa *dfa;
  ByteBufferedInputStream byteBufferedInputStream{};
  std::list<Token *> *tokens;
  ByteBuffer oneTokenStringBuilder;
  int startIndexOfToken;
  const int eof;

private:
  bool buildOneToken();
  const TokenDfaState *getTerminalState();
  void clear();
};

#endif // AST__DFATOKENAUTOMATA_H_
