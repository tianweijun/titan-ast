//
// Created by tian wei jun on 2022/11/23 0023.
//

#ifndef AST__RUNTIME__RUNTIMEAUTOMATAASTAPPLICATION_H_
#define AST__RUNTIME__RUNTIMEAUTOMATAASTAPPLICATION_H_
#include "Ast.h"
#include "AstAutomata.h"
#include "AutomataData.h"
#include "Result.h"
#include "Runtime.h"
#include "TokenAutomata.h"
#include <memory>
#include <mutex>
#include <string>
#include <vector>

class DLL_PUBLIC RuntimeAutomataAstApplication {
 public:
  RuntimeAutomataAstApplication();
  RuntimeAutomataAstApplication(const RuntimeAutomataAstApplication
                                    &runtimeAutomataAstApplication) = delete;
  RuntimeAutomataAstApplication(const RuntimeAutomataAstApplication
                                    &&runtimeAutomataAstApplication) = delete;
  virtual ~RuntimeAutomataAstApplication();

  BuildAutomataResult setContext(const std::string *automataFilePath);
  AstGeneratorResult *buildAst(const std::string *sourceCodeFilePath);
  virtual RuntimeAutomataAstApplication *clone();
  void cloneDataToCloner(RuntimeAutomataAstApplication *cloner);
  std::vector<AstGrammar> getGrammars();

 private:
  std::shared_ptr<AutomataData> automataData;
  TokenAutomata *tokenAutomata;
  AstAutomata *astAutomata;

  static std::mutex cloneLock;
};

#endif// AST__RUNTIME__RUNTIMEAUTOMATAASTAPPLICATION_H_
