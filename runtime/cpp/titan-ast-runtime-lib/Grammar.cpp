//
// Created by tian wei jun on 2022/11/17 0017.
//

#include "Grammar.h"

Grammar::Grammar(int index)
    : index(index),name(std::string()), type(GrammarType::TERMINAL),
      action(GrammarAction::TEXT) {}

Grammar &Grammar::operator=(const Grammar &other) = default;

Grammar::~Grammar() = default;

AstGrammar Grammar::toAstGrammar() const { return {this->name,this->type}; }


Grammar Grammar::getGrammar() const{
  if(this->type==GrammarType::TERMINAL){
    TerminalGrammar grammar(this->index);
    grammar.type = this->type;
    grammar.name = this->name;
    grammar.action = this->action;
    grammar.lookaheadMatchingMode = ((TerminalGrammar*)this)->lookaheadMatchingMode;
    return grammar;
  }else{
    NonterminaltGrammar grammar(this->index);
    grammar.type = this->type;
    grammar.name = this->name;
    grammar.action = this->action;
    return grammar;
  }
}

// ------------------TerminalGrammar----------------------

TerminalGrammar::TerminalGrammar(int index)
    : Grammar(index), lookaheadMatchingMode(LookaheadMatchingMode::GREEDINESS) {
  this->type = GrammarType::TERMINAL;
}


TerminalGrammar::~TerminalGrammar() = default;

// ------------------NonterminaltGrammar----------------------
NonterminaltGrammar::NonterminaltGrammar(int index) : Grammar(index) {
  this->type = GrammarType::NONTERMINAL;
}

NonterminaltGrammar::~NonterminaltGrammar() = default;