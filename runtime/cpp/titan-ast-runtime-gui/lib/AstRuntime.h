//
// Created by tian wei jun on 2022-12-10.
//

#ifndef AST_RUNTIME_GUI_LIB_ASTRUNTIME_H_

#include <exception>
#include <list>
#include <map>
#include <string>
#include <mutex>
#include <memory>
#include <vector>
#include "Runtime.h"
using byte = uint8_t;

enum class TokenType : int {
  TEXT = 0,
  SKIP = 1
};

enum class GrammarType : int {
  TERMINAL_FRAGMENT = 0,
  TERMINAL = 1,
  NONTERMINAL = 2
};

enum class GrammarAction : int { TEXT = 0, SKIP = 1 };

enum class LookaheadMatchingMode : int {
  GREEDINESS = 0,
  LAZINESS = 1
};

class DLL_PUBLIC Grammar {
 public:
  explicit Grammar(int index);
  Grammar(const Grammar &grammar) = default;
  Grammar(Grammar &&grammar) noexcept = default;
  Grammar &operator=(const Grammar &other);
  ~Grammar();

  int index;
  std::string name;
  GrammarType type;
  GrammarAction action;

};

class DLL_PUBLIC TerminalGrammar : public Grammar {
public:
  explicit TerminalGrammar(int index);
  ~TerminalGrammar();

  LookaheadMatchingMode lookaheadMatchingMode;
};

class DLL_PUBLIC NonterminalGrammar : public Grammar {
public:
  explicit NonterminalGrammar(int index);
  ~NonterminalGrammar();
};

class DLL_PUBLIC AstGrammar {
 public:
  AstGrammar();
  AstGrammar(std::string name, GrammarType type);
  AstGrammar(const AstGrammar &grammar);
  AstGrammar(AstGrammar &&grammar) noexcept;
  AstGrammar &operator=(const AstGrammar &other);
  ~AstGrammar();
  bool compare(const AstGrammar &o) const;
  bool equals(const AstGrammar &o) const;

  std::string name;
  GrammarType type;
};

class DLL_PUBLIC AstToken {
 public:
  AstToken();
  AstToken(int start, std::string text);
  AstToken(const AstToken &token);
  AstToken(AstToken &&token) noexcept;
  AstToken &operator=(const AstToken &other);
  ~AstToken() = default;
  const AstToken *clone() const;

  int start;
  std::string text;
};

class DLL_PUBLIC Ast {
 public:
  explicit Ast(AstGrammar grammar);
  Ast(Ast &ast) = delete;
  Ast(Ast &&ast) = delete;
  virtual ~Ast();

  virtual std::string toString() const = 0;

  const AstGrammar grammar;
  const std::vector<Ast *> children;
};

class DLL_PUBLIC TerminalAst : public Ast {
 public:
  TerminalAst(AstGrammar grammar, AstToken token);
  TerminalAst(TerminalAst &ast) = delete;
  TerminalAst(TerminalAst &&ast) = delete;

  std::string toString() const override;

  const AstToken token;
};

class DLL_PUBLIC NonterminalAst : public Ast {
 public:
  NonterminalAst(AstGrammar grammar, std::string alias);
  NonterminalAst(NonterminalAst &ast) = delete;
  NonterminalAst(NonterminalAst &&ast) = delete;

  std::string toString() const override;

  const std::string alias;
};

struct DLL_PUBLIC BuildAutomataResult{
  bool isOk{false};
  std::string msg{""};
};

enum class  TokensResultType : int {
  OK = 0,
  TOKEN_PARSE_ERROR = 1,
  SOURCE_IO_ERROR = 2
};

class DLL_PUBLIC Token {
 public:
  Token();
  Token(Grammar terminal, int start, std::string text, TokenType type);
  Token(const Token &token);
  Token(Token &&token) noexcept;
  Token &operator=(const Token &other);
  ~Token() = default;
  const Token *clone() const;

  Grammar terminal;
  int start;
  std::string text;
  TokenType type;
};

class DLL_PUBLIC TokenParseErrorData {
 public:
  TokenParseErrorData(std::list<Token *> *finishedTokens, int start, int end,
                      std::string errorText);
  ~TokenParseErrorData();

  std::string toString();

 public:
  std::list<Token *> *finishedTokens;
  int start;
  int end;
  std::string errorText;
};

class DLL_PUBLIC TokensResult {
 public:
  TokensResult(TokensResultType type, void *data);
  ~TokensResult();

  bool isOk() const;

  static TokensResult *generateOkResult(std::list<Token *> *data);
  std::list<Token *> *getOkData() const;

  static TokensResult *generateSourceIoErrorResult(std::string *data);
  std::string *getSourceIoErrorData() const;

  static TokensResult *generateTokenParseErrorResult(TokenParseErrorData *data);
  TokenParseErrorData *getTokenParseErrorData() const;

 public:
  TokensResultType type;
  void *data;
};

enum class  AstResultType : int {
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
  AstGeneratorResult(TokensResult *tokensResult,
                     AstResult *astResult);
  ~AstGeneratorResult();

  bool isOk() const;

  std::list<Token *> *getOkTokens() const;
  Ast *getOkAst() const;
  std::string getErrorMsg() const;

 public:
  TokensResult *tokensResult;
  AstResult *astResult;
};

class DLL_PUBLIC RuntimeAutomataAstApplication {
 public:
  RuntimeAutomataAstApplication();
  RuntimeAutomataAstApplication(const RuntimeAutomataAstApplication
                                    &runtimeAutomataAstApplication) = delete;
  RuntimeAutomataAstApplication(const RuntimeAutomataAstApplication
                                    &&runtimeAutomataAstApplication) = delete;
  virtual ~RuntimeAutomataAstApplication();

  BuildAutomataResult setContext(const std::string *automataFilePath);
  AstGeneratorResult *buildAst(const std::string *sourceCodeFilePath);
  virtual RuntimeAutomataAstApplication *clone();
  void cloneDataToCloner(RuntimeAutomataAstApplication *cloner);
  std::vector<AstGrammar> getGrammars();

 private:
  std::shared_ptr<void*> automataData;
  void *tokenAutomata;
  void *astAutomata;

  static std::mutex cloneLock;
};


enum class  RichTokensResultType : int {
  OK = 0,
  TOKEN_PARSE_ERROR = 1,
  SOURCE_IO_ERROR = 2
};

class DLL_PUBLIC RichTokenParseErrorData {
 public:
  RichTokenParseErrorData(std::list<Token *> *finishedTokens,
                          int startLineNumber, int start, int endLineNumber,
                          int end, std::string errorText);
  ~RichTokenParseErrorData();

  std::string toString();

 public:
  std::list<Token *> *finishedTokens;
  int startLineNumber;
  int start;
  int endLineNumber;
  int end;
  std::string errorText;
};

class DLL_PUBLIC RichTokensResult {
 public:
  RichTokensResult(RichTokensResultType type, void *data);
  ~RichTokensResult();

  bool isOk() const;

  static RichTokensResult *generateOkResult(std::list<Token *> *data);
  std::list<Token *> *getOkData() const;

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
  LineNumberRangeDto(bool isOk,int lineNumber, int start, int end);
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
  RichAstParseErrorData(int startLineNumber, int start, int endLineNumber,
                        int end, std::string errorText);
  ~RichAstParseErrorData();

  std::string toString();

 public:
  int startLineNumber;
  int start;
  int endLineNumber;
  int end;
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
                         const LineNumberDetail *richAstResult,
                         const RichAstResult *astResult);
  ~RichAstGeneratorResult();

  bool isOk() const;

  std::list<Token *> *getOkTokens() const;
  Ast *getOkAst() const;
  std::string getErrorMsg() const;

 public:
  const RichTokensResult *richTokensResult;
  const LineNumberDetail *lineNumberDetail;
  const RichAstResult *richAstResult;
};

class DLL_PUBLIC AstGeneratorResult2RichResultConverter {
 public:
  AstGeneratorResult2RichResultConverter();
  ~AstGeneratorResult2RichResultConverter();
  void setNewline(byte newline);
  byte getNewline();
  RichAstGeneratorResult *convert(AstGeneratorResult *astGeneratorResult);

 private:
  byte newline;

 private:
  RichTokensResult *convert2RichTokensResult(TokensResult *tokensResult);
  RichTokenParseErrorData *
  convert2RichTokenGeneratorErrorData(TokenParseErrorData *tokenParseErrorData);
  LineNumberDetail *buildLineNumberDetail(std::list<Token *> *tokens);
  RichAstResult *convert2RichAstResult(AstResult *astResult,
                                       LineNumberDetail *lineNumberDetail);
  RichAstParseErrorData *
  convert2RichAstParseErrorData(AstParseErrorData *astParseErrorData,
                                LineNumberDetail *lineNumberDetail);
};

class DLL_PUBLIC RuntimeAutomataRichAstApplication
    : public RuntimeAutomataAstApplication {
 public:
  RuntimeAutomataRichAstApplication();
  RuntimeAutomataRichAstApplication(
      const RuntimeAutomataRichAstApplication
          &runtimeAutomataRichAstApplication) = delete;
  RuntimeAutomataRichAstApplication(
      const RuntimeAutomataRichAstApplication
          &&runtimeAutomataRichAstApplication) = delete;
  ~RuntimeAutomataRichAstApplication() override;

  RuntimeAutomataAstApplication *clone() override;

  void setNewline(byte newline);
  RichAstGeneratorResult* buildRichAst(const std::string* sourceFilePath);

 private:
  AstGeneratorResult2RichResultConverter richResultConverter;
};
#define AST_RUNTIME_GUI_LIB_ASTRUNTIME_H_

#endif//AST_RUNTIME_GUI_LIB_ASTRUNTIME_H_
