//
// Created by tian wei jun on 2022/11/24 0024.
//

#ifndef AST__ASTGUIOUTPUTER_H_
#define AST__ASTGUIOUTPUTER_H_

#include "AstRuntime.h"
#include "StringTree.h"
#include <future>

class AstGuiOutputer {
 public:
  explicit AstGuiOutputer(const Ast *ast);
  AstGuiOutputer(const AstGuiOutputer &astGuiOutputer) = delete;
  AstGuiOutputer(const AstGuiOutputer &&astGuiOutputer) = delete;
  ~AstGuiOutputer();

  void output();
  StringTree *buildStringTree(const Ast *argAst);
  void waitToClose();

  StringTree *stringTree;
  const Ast *ast;
  std::future<int> futureOfView;
  bool hasOpened;
};

#endif//AST__ASTGUIOUTPUTER_H_
