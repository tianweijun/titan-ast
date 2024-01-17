//
// Created by tian wei jun on 2022/11/23 0023.
//

#include "RuntimeAutomataAstApplication.h"
#include "AstRuntimeException.h"
#include <list>

RuntimeAutomataAstApplication::RuntimeAutomataAstApplication()
    : persistentAutomataAstApplication(nullptr) {}

RuntimeAutomataAstApplication::~RuntimeAutomataAstApplication() {
  delete persistentAutomataAstApplication;
  persistentAutomataAstApplication = nullptr;
}

void RuntimeAutomataAstApplication::setContext(
    const std::string *automataFilePath) {
  AstRuntimeExceptionResolver::clearExceptions();
  persistentAutomataAstApplication =
      new PersistentAutomataAstApplication(automataFilePath);
}

const Ast *
RuntimeAutomataAstApplication::buildAst(const std::string *sourceCodeFilePath) {
  AstRuntimeExceptionResolver::clearExceptions();
  return persistentAutomataAstApplication->buildAst(sourceCodeFilePath);
}

RuntimeAutomataAstApplication *RuntimeAutomataAstApplication::clone() {
  auto *app = new RuntimeAutomataAstApplication();
  app->persistentAutomataAstApplication =
      persistentAutomataAstApplication->clone();
  return app;
}
