//
// Created by tian wei jun on 2022/11/23 0023.
//

#include "PersistentAutomataAstApplication.h"
#include "AstRuntimeException.h"
#include "TokenAutomataBuilder.h"
#include <list>

std::mutex PersistentAutomataAstApplication::cloneLock{};

PersistentAutomataAstApplication::PersistentAutomataAstApplication()
    : persistentObject(std::make_shared<PersistentObject>()),
      tokenAutomata(nullptr), astAutomata(nullptr) {}

PersistentAutomataAstApplication::PersistentAutomataAstApplication(
    const std::string *persistentDataFilePath)
    : PersistentAutomataAstApplication() {
  buildContext(persistentDataFilePath);
}
PersistentAutomataAstApplication::~PersistentAutomataAstApplication() {
  delete tokenAutomata;
  tokenAutomata = nullptr;
  delete astAutomata;
  astAutomata = nullptr;

  cloneLock.lock();
  //引用计数减一
  persistentObject.reset();
  cloneLock.unlock();
}

void PersistentAutomataAstApplication::buildContext(
    const std::string *persistentDataFilePath) {
  auto *persistentData = new PersistentData(persistentDataFilePath);
  //初始化错误（可能原因：自动机文件不存在）
  if (AstRuntimeExceptionResolver::hasThrewException()) {
    delete persistentData;
    return;
  }
  persistentObject.reset(new PersistentObject(persistentData));
  PersistentObject *ptrPersistentObject = persistentObject.get();
  TokenAutomataBuilder tokenAutomataBuilder;
  tokenAutomata = tokenAutomataBuilder.build(ptrPersistentObject);
  astAutomata = new BacktrackingBottomUpAstAutomata(
      ptrPersistentObject->astDfa, ptrPersistentObject->startGrammar,
      ptrPersistentObject->persistentData->grammars,
      ptrPersistentObject->persistentData->sizeOfGramamrs);
}

const Ast *PersistentAutomataAstApplication::buildAst(
    const std::string *sourceCodeFilePath) const {
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

const PersistentAutomataAstApplication *
PersistentAutomataAstApplication::clone() const {
  auto *app = new PersistentAutomataAstApplication();
  cloneLock.lock();
  //引用计数增加
  app->persistentObject = this->persistentObject;
  cloneLock.unlock();

  PersistentObject *ptrPersistentObject = app->persistentObject.get();
  TokenAutomataBuilder tokenAutomataBuilder;
  app->tokenAutomata = tokenAutomataBuilder.build(ptrPersistentObject);
  app->astAutomata = new BacktrackingBottomUpAstAutomata(
      ptrPersistentObject->astDfa, ptrPersistentObject->startGrammar,
      ptrPersistentObject->persistentData->grammars,
      ptrPersistentObject->persistentData->sizeOfGramamrs);
  return app;
}
const std::list<Ast *> *PersistentAutomataAstApplication::buildAsts(
    const std::string *sourceCodeFilePath) const {
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
  const std::list<Ast *> *asts = astAutomata->buildAsts(tokens);
  //回收堆中的token
  for (auto token : *tokens) {
    delete token;
  }
  delete tokens;
  // clone ast error
  if (AstRuntimeExceptionResolver::hasThrewException()) {
    for (auto ast : *asts) {
      delete ast;
    }
    delete asts;
    asts = nullptr;
  }
  return asts;
}
