//
// Created by tian wei jun on 2022/11/23 0023.
//

#include "RuntimeAutomataAstApplication.h"
#include "AstRuntimeException.h"
#include "TokenAutomataBuilder.h"
#include "AstAutomataBuilder.h"
#include "PersistentData.h"
#include "PersistentObject.h"
#include <list>

std::mutex RuntimeAutomataAstApplication::cloneLock{};

RuntimeAutomataAstApplication::RuntimeAutomataAstApplication()
    : automataData(std::make_shared<AutomataData>()),
      tokenAutomata(nullptr), astAutomata(nullptr){}

RuntimeAutomataAstApplication::~RuntimeAutomataAstApplication() {
  delete tokenAutomata;
  tokenAutomata = nullptr;
  delete astAutomata;
  astAutomata = nullptr;

  cloneLock.lock();
  //引用计数减一
  automataData.reset();
  cloneLock.unlock();
}

void RuntimeAutomataAstApplication::setContext(
    const std::string *automataFilePath) {
  AstRuntimeExceptionResolver::clearExceptions();

  PersistentData persistentData(automataFilePath);
  //初始化错误（可能原因：自动机文件不存在）
  if (AstRuntimeExceptionResolver::hasThrewException()) {
    return;
  }
  PersistentObject persistentObject;
  persistentObject.initByPersistentData(&persistentData);

  auto ptrAutomataData = this->automataData.get();
  //all heap data is moved
  persistentObject.setAutomataData(ptrAutomataData);

  TokenAutomataBuilder tokenAutomataBuilder;
  tokenAutomata = tokenAutomataBuilder.build(ptrAutomataData);

  AstAutomataBuilder astAutomataBuilder;
  astAutomata = astAutomataBuilder.build(ptrAutomataData);
}

const Ast *
RuntimeAutomataAstApplication::buildAst(const std::string *sourceCodeFilePath) {
  AstRuntimeExceptionResolver::clearExceptions();

  std::list<Token *> *tokens = tokenAutomata->buildToken(sourceCodeFilePath);
  // byteBufferedInputStream初始化错误（可能原因：源文件不存在）
  // text is not a token
  if (AstRuntimeExceptionResolver::hasThrewException()) {
    if (tokens) {
      for (auto &token : *tokens) {
        delete token;
      }
      delete tokens;
    }
    return nullptr;
  }
  const Ast *ast = astAutomata->buildAst(tokens);
  //回收堆中的token
  for (auto token : *tokens) {
    delete token;
  }
  delete tokens;
  // clone ast error
  if (AstRuntimeExceptionResolver::hasThrewException()) {
    delete ast;
    ast = nullptr;
  }
  return ast;
}

RuntimeAutomataAstApplication *RuntimeAutomataAstApplication::clone() {
  auto *app = new RuntimeAutomataAstApplication();

  cloneLock.lock();
  //引用计数增加
  app->automataData = this->automataData;
  cloneLock.unlock();

  AutomataData *ptrAutomataData = app->automataData.get();

  TokenAutomataBuilder tokenAutomataBuilder;
  app->tokenAutomata = tokenAutomataBuilder.build(ptrAutomataData);

  AstAutomataBuilder astAutomataBuilder;
  app->astAutomata = astAutomataBuilder.build(ptrAutomataData);
  return app;
}

std::vector<AstGrammar> RuntimeAutomataAstApplication::getGrammars() {
  std::vector<AstGrammar> grammars(automataData->sizeOfGramamrs);
  for(int indexOfGrammars = 0 ; indexOfGrammars<automataData->sizeOfGramamrs;indexOfGrammars++){
    auto originalGrammar = automataData->grammars[indexOfGrammars];
    AstGrammar grammar(originalGrammar->name,originalGrammar->type);
    grammars[indexOfGrammars] = grammar;
  }
  return grammars;
}
