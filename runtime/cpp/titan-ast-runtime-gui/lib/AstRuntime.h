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

enum class TokenType : int {
  TEXT = 0,
  SKIP = 1
};

enum class GrammarType : int {
  TERMINAL_FRAGMENT = 0,
  TERMINAL = 1,
  NONTERMINAL = 2
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
  Ast(AstGrammar grammar, std::string alias);
  Ast(const Ast &ast) = delete;
  Ast(const Ast &&ast) = delete;
  ~Ast();

  std::string toString() const;

  AstGrammar grammar;
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
  std::vector<AstGrammar> getGrammars();

 private:
  std::shared_ptr<RuntimeAutomataAstApplication> automataData;
  void *tokenAutomata;
  void *astAutomata;

  static std::mutex cloneLock;
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
