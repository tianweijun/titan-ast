//
// Created by tian wei jun on 2022/11/17 0017.
//

#include "Grammar.h"

Grammar::Grammar(int index)
    : index(index), name(std::string()), type(GrammarType::TERMINAL),
      action(GrammarAction::TEXT) {}

Grammar &Grammar::operator=(const Grammar &other) = default;

Grammar::~Grammar() = default;

// ------------------TerminalGrammar----------------------

TerminalGrammar::TerminalGrammar(int index)
    : Grammar(index), lookaheadMatchingMode(LookaheadMatchingMode::GREEDINESS) {
  this->type = GrammarType::TERMINAL;
}

TerminalGrammar::~TerminalGrammar() = default;

// ------------------NonterminalGrammar----------------------
NonterminalGrammar::NonterminalGrammar(int index) : Grammar(index) {
  this->type = GrammarType::NONTERMINAL;
}

NonterminalGrammar::~NonterminalGrammar() = default;