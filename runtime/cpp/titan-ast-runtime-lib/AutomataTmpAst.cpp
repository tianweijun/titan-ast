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

bool AutomataTmpAst::equals(const AutomataTmpAst *o) const {
  if (this->grammar != o->grammar || this->alias != o->alias ||
      this->token != o->token) {
    return false;
  }
  if (this->children.size() != o->children.size()) {
    return false;
  }
  auto thisChildrenIt = this->children.begin();
  auto oChildrenIt = o->children.begin();
  while (thisChildrenIt != this->children.end()) {
    AutomataTmpAst *thisChild = *thisChildrenIt;
    AutomataTmpAst *oChild = *oChildrenIt;
    if (!thisChild->equals(oChild)) {
      return false;
    }
    thisChildrenIt++;
    oChildrenIt++;
  }
  return true;
}

bool AutomataTmpAst::compare(const AutomataTmpAst *o) const {
  if (this->grammar != o->grammar) {
    return reinterpret_cast<uintptr_t>(this->grammar) <
           reinterpret_cast<uintptr_t>(o->grammar);
  }
  if (nullptr == this->alias) {
    if (nullptr == o->alias) {
    } else {
      return true;
    }
  } else {
    if (nullptr == o->alias) {
      return false;
    } else {
      if (*this->alias != *o->alias) {
        return (*this->alias) < (*o->alias);
      }
    }
  }

  if (nullptr == this->token) {
    if (nullptr == o->token) {
    } else {
      return true;
    }
  } else {
    if (nullptr == o->token) {
      return false;
    } else {
      if (this->token != o->token) {
        return reinterpret_cast<uintptr_t>(this->token) <
               reinterpret_cast<uintptr_t>(o->token);
      }
    }
  }

  if (this->children.size() != o->children.size()) {
    return this->children.size() < o->children.size();
  }
  auto thisChildrenIt = this->children.begin();
  auto oChildrenIt = o->children.begin();
  while (thisChildrenIt != this->children.end()) {
    AutomataTmpAst *thisChild = *thisChildrenIt;
    AutomataTmpAst *oChild = *oChildrenIt;
    if (!thisChild->equals(oChild)) {
      return thisChild->compare(oChild);
    }
    thisChildrenIt++;
    oChildrenIt++;
  }
  return false;
}

size_t AutomataTmpAst::hashCode() const {
  size_t hashCode = (long)grammar;
  hashCode += children.size();
  return hashCode;
}