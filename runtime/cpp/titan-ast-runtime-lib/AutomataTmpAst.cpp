//
// Created by tian wei jun on 2022/12/8 0008.
//

#include "AutomataTmpAst.h"

AutomataTmpAst::AutomataTmpAst(const Grammar *grammar)
    : grammar(grammar), children(std::list<AutomataTmpAst *>()) {}

AutomataTmpAst::~AutomataTmpAst() {
  // grammar delete by PersistentData.grammars
  // alias delete by PersistentData.stringPool
  //  token did not need to delete
  for (auto child : children) {
    delete child;
  }
}

TerminalAutomataTmpAst::TerminalAutomataTmpAst(const Grammar *grammar,
                                               const AutomataTmpToken *token)
    : AutomataTmpAst(grammar), token(token) {}

const AutomataTmpAst *TerminalAutomataTmpAst::cloneForAstAutomata() const {
  return new TerminalAutomataTmpAst(this->grammar, this->token);
}

Ast *TerminalAutomataTmpAst::toAst() const {
  AstGrammar astGrammar = {this->grammar->name, this->grammar->type};
  AstToken astToken = {this->token->start, *this->token->text};
  return new TerminalAst(astGrammar, astToken);
}

NonterminalAutomataTmpAst::NonterminalAutomataTmpAst(const Grammar *grammar,
                                                     const std::string *alias)
    : AutomataTmpAst(grammar), alias(alias) {}

const AutomataTmpAst *NonterminalAutomataTmpAst::cloneForAstAutomata() const {
  auto *ast =
      new NonterminalAutomataTmpAst(const_cast<Grammar *>(this->grammar),
                                    const_cast<std::string *>(this->alias));
  for (auto thisChild : this->children) {
    ast->children.push_back(
        const_cast<AutomataTmpAst *>(thisChild->cloneForAstAutomata()));
  }
  return ast;
}

Ast *NonterminalAutomataTmpAst::toAst() const {
  std::string astAlias;
  if (this->alias) {
    astAlias = *this->alias;
  }
  auto *ast =
      new NonterminalAst({this->grammar->name, this->grammar->type}, astAlias);
  auto *astChildren = const_cast<std::vector<Ast *> *>(&ast->children);
  astChildren->reserve(this->children.size());
  for (auto thisChild : this->children) {
    astChildren->push_back(thisChild->toAst());
  }
  return ast;
}
