//
// Created by tian wei jun on 2022/11/17 0017.
//

#include "Grammar.h"

Grammar::Grammar()
    : name(std::string()), type(GrammarType::TERMINAL),
      action(GrammarAction::TEXT) {}

Grammar::Grammar(std::string name, GrammarType type, GrammarAction action)
    : name(name), type(type), action(action) {}

Grammar::Grammar(const Grammar &grammar) {
  this->name = grammar.name;
  this->type = grammar.type;
  this->action = grammar.action;
}

Grammar::Grammar(Grammar &&grammar) noexcept {
  this->name = grammar.name;
  this->type = grammar.type;
  this->action = grammar.action;
}

Grammar &Grammar::operator=(const Grammar &other) = default;

Grammar::~Grammar() = default;

bool Grammar::compare(const Grammar &o) const {
  if (type != o.type) {
    return type < o.type;
  }
  return name < o.name;
}

bool Grammar::equals(const Grammar &o) const {
  return type == o.type && name == o.name;
}
AstGrammar Grammar::toAstGrammar() const { return AstGrammar(this->name,this->type); }

// ------------------TerminalGrammar----------------------

TerminalGrammar::TerminalGrammar()
    : Grammar(), lookaheadMatchingMode(LookaheadMatchingMode::GREEDINESS) {
  this->type = GrammarType::TERMINAL;
}

TerminalGrammar::TerminalGrammar(std::string name, GrammarType type,
                                 GrammarAction action)
    : Grammar(name, type, action),
      lookaheadMatchingMode(LookaheadMatchingMode::GREEDINESS) {
  this->type = GrammarType::TERMINAL;
}

TerminalGrammar::TerminalGrammar(const TerminalGrammar &grammar) = default;

TerminalGrammar::TerminalGrammar(TerminalGrammar &&grammar) noexcept = default;

TerminalGrammar &
TerminalGrammar::operator=(const TerminalGrammar &other) = default;

TerminalGrammar::~TerminalGrammar() = default;

// ------------------NonterminaltGrammar----------------------
NonterminaltGrammar::NonterminaltGrammar() : Grammar() {
  this->type = GrammarType::NONTERMINAL;
}

NonterminaltGrammar::NonterminaltGrammar(std::string name, GrammarType type,
                                         GrammarAction action)
    : Grammar(name, type, action) {
  this->type = GrammarType::NONTERMINAL;
}

NonterminaltGrammar::NonterminaltGrammar(const NonterminaltGrammar &grammar) =
    default;

NonterminaltGrammar::NonterminaltGrammar(
    NonterminaltGrammar &&grammar) noexcept = default;

NonterminaltGrammar &
NonterminaltGrammar::operator=(const NonterminaltGrammar &other) = default;

NonterminaltGrammar::~NonterminaltGrammar() = default;