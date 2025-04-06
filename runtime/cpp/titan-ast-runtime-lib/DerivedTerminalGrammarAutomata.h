//
// Created by tian wei jun on 2022/11/25 0025.
//

#ifndef AST__DERIVEDTERMINALGRAMMARAUTOMATA_H_
#define AST__DERIVEDTERMINALGRAMMARAUTOMATA_H_
#include "ByteBuffer.h"
#include "ByteBufferedInputStream.h"
#include "DerivedTerminalGrammarAutomataData.h"
#include "DfaTokenAutomata.h"
#include "Token.h"
#include "TokenDfa.h"
#include <list>
#include <string>

class DerivedTerminalGrammarAutomata : public DfaTokenAutomata {
public:
  explicit DerivedTerminalGrammarAutomata(
      const DerivedTerminalGrammarAutomataData
          *derivedTerminalGrammarAutomataData,
      const TokenDfa *tokenDfa);
  DerivedTerminalGrammarAutomata(
      const DerivedTerminalGrammarAutomata &dfaTokenAutomata) = delete;
  DerivedTerminalGrammarAutomata(
      const DerivedTerminalGrammarAutomata &&dfaTokenAutomata) = delete;
  ~DerivedTerminalGrammarAutomata() override;
  TokensResult *buildToken(const std::string *sourceFilePath) override;
  std::vector<Token *> *buildTokenByDerivedTerminalGrammarAutomata(
      std::vector<Token *> *tokens) const;

private:
  std::unordered_map<
      const Grammar *,
      const std::unordered_map<std::string *, Grammar *, TextTerminalMapHash,
                               TextTerminalMapEq> *,
      PtrGrammarContentHash, PtrGrammarContentEq>
      rootTerminalGrammarMap;
};

#endif // AST__DERIVEDTERMINALGRAMMARAUTOMATA_H_
