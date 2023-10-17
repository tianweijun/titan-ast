//
// Created by tian wei jun on 2022-12-10.
//

#ifndef AST_RUNTIME_GUI_LIB_ASTRUNTIME_H_

#include <exception>
#include <list>
#include <map>
#include <string>
#include "Runtime.h"

enum class TokenType : int {
  TEXT = 0,
  SKIP = 1
};

enum class GrammarType : int {
  TERMINAL_FRAGMENT = 0,
  TERMINAL = 1,
  NONTERMINAL = 2
};

enum class GrammarAction : int {
  TEXT = 0,
  SKIP = 1
};

class DLL_PUBLIC Grammar {
 public:
  Grammar();
  Grammar(std::string name, GrammarType type, GrammarAction action);
  Grammar(const Grammar &grammar);
  Grammar(Grammar &&grammar) noexcept;
  Grammar &operator=(const Grammar &other);
  ~Grammar();
  bool compare(const Grammar &o) const;
  bool equals(const Grammar &o) const;

  std::string name;
  GrammarType type;
  GrammarAction action;
};

enum class LookaheadMatchingMode : int {
  GREEDINESS = 0,
  LAZINESS = 1,
  ACCEPT_WHEN_FIRST_ARRIVE_AT_TERMINAL_STATE = 2
};

class DLL_PUBLIC TerminalGrammar : public Grammar{
 public :
  TerminalGrammar();
  TerminalGrammar(std::string name, GrammarType type, GrammarAction action);
  TerminalGrammar(const TerminalGrammar &grammar);
  TerminalGrammar(TerminalGrammar &&grammar) noexcept;
  TerminalGrammar &operator=(const TerminalGrammar &other);
  ~TerminalGrammar();

  LookaheadMatchingMode lookaheadMatchingMode;

};

class DLL_PUBLIC NonterminaltGrammar : public Grammar{
 public :
  NonterminaltGrammar();
  NonterminaltGrammar(std::string name, GrammarType type, GrammarAction action);
  NonterminaltGrammar(const NonterminaltGrammar &grammar);
  NonterminaltGrammar(NonterminaltGrammar &&grammar) noexcept;
  NonterminaltGrammar &operator=(const NonterminaltGrammar &other);
  ~NonterminaltGrammar();

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
  explicit Ast(Grammar grammar);
  Ast(Grammar grammar, std::string alias);
  explicit Ast(const Token &token);
  Ast(const Ast &ast) = delete;
  Ast(const Ast &&ast) = delete;
  ~Ast();

  std::string toString() const;

  Grammar grammar;
  std::string alias;
  // grammar.type == GrammarType.TERMINAL
  AstToken token;
  std::list<Ast *> children;
};

class DLL_PUBLIC RuntimeAutomataAstApplication {
 public:
  RuntimeAutomataAstApplication();
  RuntimeAutomataAstApplication(const RuntimeAutomataAstApplication &runtimeAutomataAstApplication) = delete;
  RuntimeAutomataAstApplication(const RuntimeAutomataAstApplication &&runtimeAutomataAstApplication) = delete;
  ~RuntimeAutomataAstApplication();

  void setContext(const std::string *automataFilePath);
  const Ast *buildAst(const std::string *sourceCodeFilePath);
  RuntimeAutomataAstApplication *clone();

  const std::list<Ast *> *buildAsts(const std::string *sourceCodeFilePath);

 private:
  const void *persistentAutomataAstApplication;
};


enum class AstRuntimeExceptionCode : int {
  LOGIC_ERROR = 0,
  IO_ERROR,
  INVALID_ARGUMENT
};

class DLL_PUBLIC AstRuntimeException : public std::exception {
 public:
  explicit AstRuntimeException(AstRuntimeExceptionCode code, std::string msg) noexcept;
  explicit AstRuntimeException(AstRuntimeExceptionCode code, const char *msg) noexcept;
  AstRuntimeException(const AstRuntimeException &ex);
  ~AstRuntimeException() noexcept override;

  const char *what() const noexcept override;

  const AstRuntimeExceptionCode code;
  const std::string msg;
};

class DLL_PUBLIC AstRuntimeExceptionResolver {
 private:
  AstRuntimeExceptionResolver();
  ~AstRuntimeExceptionResolver();

 public:
  AstRuntimeExceptionResolver(AstRuntimeExceptionResolver &ExResolver) = delete;
  AstRuntimeExceptionResolver(AstRuntimeExceptionResolver &&ExResolver) = delete;

 public:
  static void throwException(const AstRuntimeException& ex);
  static void clearExceptions();
  static bool hasThrewException();
  static const std::list<AstRuntimeException> *getExceptions();
  static void destory();
};

#define AST_RUNTIME_GUI_LIB_ASTRUNTIME_H_

#endif//AST_RUNTIME_GUI_LIB_ASTRUNTIME_H_
