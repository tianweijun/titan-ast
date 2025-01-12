//
// Created by tian wei jun on 2022/11/24 0024.
//

#include "AstGuiOutputer.h"
#include "GuiApi.h"

AstGuiOutputer::AstGuiOutputer(const Ast *ast) : ast(ast), stringTree(nullptr), hasOpened(false) {
}
AstGuiOutputer::~AstGuiOutputer() {
  delete stringTree;
  stringTree = nullptr;
}

void AstGuiOutputer::output() {
  if (!ast) {
    return;
  }
  StringTree *ast2StringTree = buildStringTree(ast);
  futureOfView =
      std::async(std::launch::async, guiapi::showViewUntilClose, ast2StringTree);
  hasOpened = true;
}
void AstGuiOutputer::waitToClose() {
  if (hasOpened) {
    futureOfView.get();
    hasOpened = false;
  }
}

StringTree *AstGuiOutputer::buildStringTree(const Ast *argAst) {
  if (!argAst) {
    return nullptr;
  }
  auto *strTree = new StringTree();
  strTree->text = argAst->toString();
  if (!argAst->children.empty()) {
    for (Ast *astTreeChild : argAst->children) {
      StringTree *strTreeChild = buildStringTree(astTreeChild);
      strTree->children->push_back(strTreeChild);
    }
  }
  return strTree;
}