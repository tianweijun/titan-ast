//
// Created by tian wei jun on 2022/11/17 0017.
//

#include "Ast.h"

#include "StringUtils.h"
#include <utility>

Ast::Ast(AstGrammar grammar)
    : grammar(std::move(grammar)), children(std::vector<Ast *>{}) {}

Ast::~Ast() {
  for (auto child : children) {
    delete child;
  }
}

TerminalAst::TerminalAst(AstGrammar grammar, AstToken token)
    : Ast(grammar), token(token) {}

std::string TerminalAst::toString() const { return token.text; }

NonterminalAst::NonterminalAst(AstGrammar grammar, std::string alias)
    : Ast(grammar), alias(alias) {}

std::string NonterminalAst::toString() const {
  std::string displayString;
  if (StringUtils::isNotBlank(&alias)) {
    displayString = grammar.name + "[" + alias + "]";
  } else {
    displayString = grammar.name;
  }
  return displayString;
}
