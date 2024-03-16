//
// Created by tian wei jun on 2022/12/8 0008.
//

#include "AutomataTmpAst.h"

AutomataTmpAst::AutomataTmpAst(const Grammar *grammar, const std::string *alias)
    : grammar(grammar), alias(alias), token(nullptr),
      children(std::list<AutomataTmpAst *>()) {}

AutomataTmpAst::AutomataTmpAst(const AutomataTmpToken *token)
    : grammar(token->terminal), alias(nullptr), token(token),
      children(std::list<AutomataTmpAst *>()) {}

AutomataTmpAst::~AutomataTmpAst() {
  // grammar delete by PersistentData.grammars
  // alias delete by PersistentData.stringPool
  // parent delete by itself
  //  token did not need to delete
  for (auto ast : children) {
    delete ast;
    ast = nullptr;
  }
}

const AutomataTmpAst *AutomataTmpAst::clone() const {
  auto *ast = new AutomataTmpAst(const_cast<Grammar *>(this->grammar),
                                 const_cast<std::string *>(this->alias));
  ast->token = this->token;
  for (auto thisChild : this->children) {
    ast->children.push_back(const_cast<AutomataTmpAst *>(thisChild->clone()));
  }
  return ast;
}

Ast *AutomataTmpAst::toAst() const {
  Ast *ast = new Ast(this->grammar->toAstGrammar());
  if (this->alias) {
    ast->alias = *this->alias;
  }
  if (this->token) {
    ast->token = this->token->toAstToken();
  }
  for (auto thisChild : this->children) {
    ast->children.push_back(thisChild->toAst());
  }
  return ast;
}