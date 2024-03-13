//
// Created by tian wei jun on 2022/11/17 0017.
//

#ifndef AST__RUNTIME__GRAMMAR_H_
#define AST__RUNTIME__GRAMMAR_H_
#include "GrammarAction.h"
#include "GrammarType.h"
#include "Runtime.h"
#include <string>

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
  LAZINESS = 1
};

class DLL_PUBLIC TerminalGrammar : public Grammar {
public:
  TerminalGrammar();
  TerminalGrammar(std::string name, GrammarType type, GrammarAction action);
  TerminalGrammar(const TerminalGrammar &grammar);
  TerminalGrammar(TerminalGrammar &&grammar) noexcept;
  TerminalGrammar &operator=(const TerminalGrammar &other);
  ~TerminalGrammar();

  LookaheadMatchingMode lookaheadMatchingMode;
};

class DLL_PUBLIC NonterminaltGrammar : public Grammar {
public:
  NonterminaltGrammar();
  NonterminaltGrammar(std::string name, GrammarType type, GrammarAction action);
  NonterminaltGrammar(const NonterminaltGrammar &grammar);
  NonterminaltGrammar(NonterminaltGrammar &&grammar) noexcept;
  NonterminaltGrammar &operator=(const NonterminaltGrammar &other);
  ~NonterminaltGrammar();
};

class PtrGrammarCompare {
public:
  bool operator()(const Grammar *t1, const Grammar *t2) const {
    return reinterpret_cast<uintptr_t>(t1) < reinterpret_cast<uintptr_t>(t2);
  }
};

class PtrGrammarContentCompare {
public:
  bool operator()(const Grammar *t1, const Grammar *t2) const {
    return t1->compare(*t2);
  }
};

#endif // AST__RUNTIME__GRAMMAR_H_
