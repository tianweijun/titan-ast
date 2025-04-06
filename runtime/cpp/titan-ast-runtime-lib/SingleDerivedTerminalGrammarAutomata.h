//
// Created by tian wei jun on 2025/4/6.
//

#ifndef TITAN_AST_RUNTIME_RUNTIME_SINGLEDERIVEDTERMINALGRAMMARAUTOMATA_H_
#define TITAN_AST_RUNTIME_RUNTIME_SINGLEDERIVEDTERMINALGRAMMARAUTOMATA_H_

#include "ByteBuffer.h"
#include "ByteBufferedInputStream.h"
#include "DerivedTerminalGrammarAutomataData.h"
#include "DfaTokenAutomata.h"
#include "Token.h"
#include "TokenDfa.h"
#include <list>
#include <string>

class SingleDerivedTerminalGrammarAutomata : public DfaTokenAutomata {
public:
  explicit SingleDerivedTerminalGrammarAutomata(
      const DerivedTerminalGrammarAutomataData
          *derivedTerminalGrammarAutomataData,
      const TokenDfa *tokenDfa);
  SingleDerivedTerminalGrammarAutomata(
      const SingleDerivedTerminalGrammarAutomata &dfaTokenAutomata) = delete;
  SingleDerivedTerminalGrammarAutomata(
      const SingleDerivedTerminalGrammarAutomata &&dfaTokenAutomata) = delete;
  ~SingleDerivedTerminalGrammarAutomata() override;
  TokensResult *buildToken(const std::string *sourceFilePath) override;
  std::vector<Token *> *buildTokenBySingleDerivedTerminalGrammarAutomata(
      std::vector<Token *> *tokens) const;

private:
  Grammar* rootTerminalGrammar;
  const std::unordered_map<std::string *, Grammar *, TextTerminalMapHash,
                     TextTerminalMapEq> * textTerminalMap;
};

#endif // TITAN_AST_RUNTIME_RUNTIME_SINGLEDERIVEDTERMINALGRAMMARAUTOMATA_H_
