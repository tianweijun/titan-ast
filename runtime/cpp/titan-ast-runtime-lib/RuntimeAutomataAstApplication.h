//
// Created by tian wei jun on 2022/11/23 0023.
//

#ifndef AST__RUNTIME__RUNTIMEAUTOMATAASTAPPLICATION_H_
#define AST__RUNTIME__RUNTIMEAUTOMATAASTAPPLICATION_H_
#include "Ast.h"
#include "PersistentAutomataAstApplication.h"
#include "Runtime.h"
#include <string>

class DLL_PUBLIC RuntimeAutomataAstApplication {
public:
  RuntimeAutomataAstApplication();
  RuntimeAutomataAstApplication(const RuntimeAutomataAstApplication
                                    &runtimeAutomataAstApplication) = delete;
  RuntimeAutomataAstApplication(const RuntimeAutomataAstApplication
                                    &&runtimeAutomataAstApplication) = delete;
  ~RuntimeAutomataAstApplication();

  void setContext(const std::string *automataFilePath);
  const Ast *buildAst(const std::string *sourceCodeFilePath);
  RuntimeAutomataAstApplication *clone();

private:
  const PersistentAutomataAstApplication *persistentAutomataAstApplication;
};

#endif // AST__RUNTIME__RUNTIMEAUTOMATAASTAPPLICATION_H_
