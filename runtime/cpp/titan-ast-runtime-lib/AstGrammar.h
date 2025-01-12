//
// Created by tian wei jun on 2024/3/14.
//

#ifndef TITAN_AST_RUNTIME_RUNTIME_ASTGRAMMAR_H_
#define TITAN_AST_RUNTIME_RUNTIME_ASTGRAMMAR_H_
#include "GrammarType.h"
#include "Runtime.h"
#include <string>

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

#endif// TITAN_AST_RUNTIME_RUNTIME_ASTGRAMMAR_H_
