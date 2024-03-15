//
// Created by tian wei jun on 2022/11/17 0017.
//

#include "Ast.h"
#include "StringUtils.h"

Ast::Ast(AstGrammar grammar)
    : grammar(grammar), alias(std::string()), token(AstToken()),
      children(std::list<Ast *>()) {}

Ast::Ast(AstGrammar grammar, std::string alias)
    : grammar(grammar), alias(alias), token(AstToken()),
      children(std::list<Ast *>()) {}

Ast::~Ast() {
  for (auto ast : children) {
    delete ast;
  }
}

std::string Ast::toString() const {
  std::string displayString;
  GrammarType type = grammar.type;
  if (type == GrammarType::TERMINAL) {
    displayString = token.text;
  }
  if (type == GrammarType::NONTERMINAL) {
    if (StringUtils::isNotBlank(&alias)) {
      displayString = grammar.name + "[" + alias + "]";
    } else {
      displayString = grammar.name;
    }
  }
  return displayString;
}
