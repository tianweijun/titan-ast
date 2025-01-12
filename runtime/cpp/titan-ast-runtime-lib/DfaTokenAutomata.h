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

enum class BuildOneTokenMethodResultType {
  TOKEN = 0,
  ALL_TEXT_HAS_BEEN_BUILT = 1,
  TOKEN_PARSE_ERROR = 2,
  IO_ERROR = 3
};

struct BuildOneTokenMethodTokenGeneratorErrorData {
 public:
  int start{0};
  int end{0};
  std::string errorText{""};
};

class BuildOneTokenMethodResult {
 public:
  BuildOneTokenMethodResult(BuildOneTokenMethodResultType type, void *data);
  ~BuildOneTokenMethodResult();

  static BuildOneTokenMethodResult *generateTokenResult(Token *data);
  Token *getTokenData() const;

  static BuildOneTokenMethodResult *generateAllTextHasBeenBuiltResult();

  static BuildOneTokenMethodResult *generateTokenParseErrorResult(
      BuildOneTokenMethodTokenGeneratorErrorData *data);
  BuildOneTokenMethodTokenGeneratorErrorData *
  getBuildOneTokenMethodTokenGeneratorErrorData() const;

  static BuildOneTokenMethodResult *generateIoErrorResult();

 public:
  BuildOneTokenMethodResultType type;
  void *data;
};

class DfaTokenAutomata : public TokenAutomata {
 public:
  explicit DfaTokenAutomata(const TokenDfa *tokenDfa);
  DfaTokenAutomata(const DfaTokenAutomata &dfaTokenAutomata) = delete;
  DfaTokenAutomata(const DfaTokenAutomata &&dfaTokenAutomata) = delete;
  ~DfaTokenAutomata() override;
  TokensResult *buildToken(const std::string *sourceFilePath) override;

 private:
  const TokenDfa *dfa;
  ByteBuffer oneTokenStringBuilder;

 private:
  static const int eof;

 private:
  BuildOneTokenMethodResult *
  buildOneToken(ByteBufferedInputStream &byteBufferedInputStream);
};

#endif// AST__DFATOKENAUTOMATA_H_
