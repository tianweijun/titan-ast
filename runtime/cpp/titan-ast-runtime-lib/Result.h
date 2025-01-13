//
// Created by tian wei jun on 2024/10/25.
//

#ifndef AST_RUNTIME_RUNTIME_RESULT_H_
#define AST_RUNTIME_RUNTIME_RESULT_H_
#include "Ast.h"
#include "Runtime.h"
#include "Token.h"
#include <list>
#include <string>

struct DLL_PUBLIC BuildAutomataResult {
  bool isOk{false};
  std::string msg{""};
};

enum class TokensResultType : int {
  OK = 0,
  TOKEN_PARSE_ERROR = 1,
  SOURCE_IO_ERROR = 2
};

class DLL_PUBLIC TokenParseErrorData {
public:
  TokenParseErrorData(std::vector<Token *> *finishedTokens, int start, int end,
                      std::string errorText);
  ~TokenParseErrorData();

  std::string toString();

public:
  std::vector<Token *> *finishedTokens;
  int start;
  int end;
  std::string errorText;
};

class DLL_PUBLIC TokensResult {
public:
  TokensResult(TokensResultType type, void *data);
  ~TokensResult();

  bool isOk() const;

  static TokensResult *generateOkResult(std::vector<Token *> *data);
  std::vector<Token *> *getOkData() const;

  static TokensResult *generateSourceIoErrorResult(std::string *data);
  std::string *getSourceIoErrorData() const;

  static TokensResult *generateTokenParseErrorResult(TokenParseErrorData *data);
  TokenParseErrorData *getTokenParseErrorData() const;

public:
  TokensResultType type;
  void *data;
};

enum class AstResultType : int {
  OK = 0,
  AST_PARSE_ERROR = 1,
  TOKENS_ERROR = 2
};

class DLL_PUBLIC AstParseErrorData {
public:
  AstParseErrorData(int start, int end, std::string errorText);
  ~AstParseErrorData();

  std::string toString();

public:
  int start;
  int end;
  std::string errorText;
};

class DLL_PUBLIC AstResult {
public:
  AstResult(AstResultType type, const void *data);
  ~AstResult();

  bool isOk() const;

  static AstResult *generateOkResult(const Ast *data);
  Ast *getOkData() const;

  static AstResult *generateAstParseErrorResult(AstParseErrorData *data);
  AstParseErrorData *getAstParseErrorData() const;

  static AstResult *generateTokensErrorResult();

public:
  AstResultType type;
  const void *data;
};

class DLL_PUBLIC AstGeneratorResult {
public:
  AstGeneratorResult(TokensResult *tokensResult, AstResult *astResult);
  ~AstGeneratorResult();

  bool isOk() const;

  std::vector<Token *> *getOkTokens() const;
  Ast *getOkAst() const;
  std::string getErrorMsg() const;

public:
  TokensResult *tokensResult;
  AstResult *astResult;
};

enum class RichTokensResultType : int {
  OK = 0,
  TOKEN_PARSE_ERROR = 1,
  SOURCE_IO_ERROR = 2
};

class DLL_PUBLIC RichTokenParseErrorData {
public:
  RichTokenParseErrorData(std::vector<Token *> *finishedTokens, int start,
                          int end, int startLineNumber, int startOffsetInLine,
                          int endLineNumber, int endOffsetInLine,
                          std::string errorText);
  ~RichTokenParseErrorData();

  std::string toString();

public:
  std::vector<Token *> *finishedTokens;
  int start;
  int end;
  int startLineNumber;
  int startOffsetInLine;
  int endLineNumber;
  int endOffsetInLine;
  std::string errorText;
};

class DLL_PUBLIC RichTokensResult {
public:
  RichTokensResult(RichTokensResultType type, void *data);
  ~RichTokensResult();

  bool isOk() const;

  static RichTokensResult *generateOkResult(std::vector<Token *> *data);
  std::vector<Token *> *getOkData() const;

  static RichTokensResult *generateSourceIoErrorResult(std::string *data);
  std::string *getSourceIoErrorData() const;

  static RichTokensResult *
  generateTokenParseErrorResult(RichTokenParseErrorData *data);
  RichTokenParseErrorData *getTokenParseErrorData() const;

public:
  RichTokensResultType type;
  void *data;
};

class DLL_PUBLIC LineNumberRange {
public:
  LineNumberRange();
  LineNumberRange(int start, int end);
  ~LineNumberRange();

public:
  int start;
  int end;
};
class DLL_PUBLIC LineNumberRangeDto {
public:
  LineNumberRangeDto(bool isOk, int lineNumber, int start, int end);
  ~LineNumberRangeDto();

public:
  bool isOk;
  int lineNumber;
  int start;
  int end;
};
class DLL_PUBLIC LineNumberDetail {
public:
  LineNumberDetail(LineNumberRange *lineNumberRanges,
                   int sizeOfLineNumberRanges);
  ~LineNumberDetail();
  LineNumberRangeDto getLineNumberRangeDto(int bytePosition);

public:
  LineNumberRange *lineNumberRanges;
  int sizeOfLineNumberRanges;
};

class DLL_PUBLIC RichAstParseErrorData {
public:
  RichAstParseErrorData(int start, int end, int startLineNumber,
                        int startOffsetInLine, int endLineNumber,
                        int endOffsetInLine, std::string errorText);
  ~RichAstParseErrorData();

  std::string toString();

public:
  int start;
  int end;
  int startLineNumber;
  int startOffsetInLine;
  int endLineNumber;
  int endOffsetInLine;
  std::string errorText;
};

enum class RichAstResultType : int {
  OK = 0,
  AST_PARSE_ERROR = 1,
  TOKENS_ERROR = 2
};

class DLL_PUBLIC RichAstResult {
public:
  RichAstResult(RichAstResultType type, const void *data);
  ~RichAstResult();

  bool isOk() const;

  static RichAstResult *generateOkResult(const Ast *data);
  Ast *getOkData() const;

  static RichAstResult *
  generateRichAstParseErrorResult(RichAstParseErrorData *data);
  RichAstParseErrorData *getAstParseErrorData() const;

  static RichAstResult *generateRichTokensErrorResult();

public:
  RichAstResultType type;
  const void *data;
};

class DLL_PUBLIC RichAstGeneratorResult {
public:
  RichAstGeneratorResult(const RichTokensResult *richTokensResult,
                         const RichAstResult *astResult);
  ~RichAstGeneratorResult();

  bool isOk() const;

  std::vector<Token *> *getOkTokens() const;
  Ast *getOkAst() const;
  std::string getErrorMsg() const;

public:
  const RichTokensResult *richTokensResult;
  const RichAstResult *richAstResult;
};
#endif // AST_RUNTIME_RUNTIME_RESULT_H_
