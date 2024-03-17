//
// Created by tian wei jun on 2022/11/17 0017.
//

#ifndef AST__RUNTIME__GRAMMAR_H_
#define AST__RUNTIME__GRAMMAR_H_
#include "GrammarAction.h"
#include "GrammarType.h"
#include "AstGrammar.h"
#include <string>

class Grammar {
public:
  explicit Grammar(int index);
  Grammar(const Grammar &grammar) = default;
  Grammar(Grammar &&grammar) noexcept = default;
  Grammar &operator=(const Grammar &other);
  ~Grammar();
  AstGrammar toAstGrammar() const;
  Grammar getGrammar() const;

  int index;
  std::string name;
  GrammarType type;
  GrammarAction action;

};

enum class LookaheadMatchingMode : int {
  GREEDINESS = 0,
  LAZINESS = 1
};

class TerminalGrammar : public Grammar {
public:
  explicit TerminalGrammar(int index);
  ~TerminalGrammar();

  LookaheadMatchingMode lookaheadMatchingMode;
};

class NonterminaltGrammar : public Grammar {
public:
  explicit NonterminaltGrammar(int index);
  ~NonterminaltGrammar();
};

class PtrGrammarContentCompare {
public:
  bool operator()(const Grammar *t1, const Grammar *t2) const {
    return t1->index<t2->index;
  }
};

class PtrGrammarContentHash {
public:
  size_t operator()(const Grammar *t1) const {
    return t1->index;
  }
};

class PtrGrammarContentEq {
public:
  bool operator()(const Grammar *t1, const Grammar *t2) const {
    return t1->index==t2->index;
  }
};


#endif // AST__RUNTIME__GRAMMAR_H_
