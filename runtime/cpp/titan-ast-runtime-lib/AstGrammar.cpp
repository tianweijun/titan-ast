//
// Created by tian wei jun on 2024/3/14.
//

#include "AstGrammar.h"

AstGrammar::AstGrammar() : name(std::string()), type(GrammarType::TERMINAL) {}

AstGrammar::AstGrammar(std::string name, GrammarType type)
    : name(name), type(type) {}

AstGrammar::AstGrammar(const AstGrammar &grammar) {
  this->name = grammar.name;
  this->type = grammar.type;
}

AstGrammar::AstGrammar(AstGrammar &&grammar) noexcept {
  this->name = grammar.name;
  this->type = grammar.type;
}

AstGrammar &AstGrammar::operator=(const AstGrammar &other) = default;

AstGrammar::~AstGrammar() = default;

bool AstGrammar::compare(const AstGrammar &o) const {
  if (type != o.type) {
    return type < o.type;
  }
  return name < o.name;
}

bool AstGrammar::equals(const AstGrammar &o) const {
  return type == o.type && name == o.name;
}