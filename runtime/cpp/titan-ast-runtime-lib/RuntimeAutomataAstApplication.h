//
// Created by tian wei jun on 2022/11/23 0023.
//

#ifndef AST__RUNTIME__RUNTIMEAUTOMATAASTAPPLICATION_H_
#define AST__RUNTIME__RUNTIMEAUTOMATAASTAPPLICATION_H_
#include "Ast.h"
#include "Runtime.h"
#include "AutomataData.h"
#include "TokenAutomata.h"
#include "AstAutomata.h"
#include <string>
#include <memory>
#include <mutex>
#include <vector>

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
  std::vector<AstGrammar> getGrammars();

private:
  std::shared_ptr<AutomataData> automataData;
  TokenAutomata *tokenAutomata;
  AstAutomata *astAutomata;

  static std::mutex cloneLock;
};

#endif // AST__RUNTIME__RUNTIMEAUTOMATAASTAPPLICATION_H_
