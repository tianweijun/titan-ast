//
// Created by tian wei jun on 2022/11/23 0023.
//

#include "RuntimeAutomataAstApplication.h"
#include "AstAutomataBuilder.h"
#include "PersistentData.h"
#include "PersistentObject.h"
#include "TokenAutomataBuilder.h"
#include <iostream>
#include <list>

std::mutex RuntimeAutomataAstApplication::cloneLock{};

RuntimeAutomataAstApplication::RuntimeAutomataAstApplication()
    : automataData(std::make_shared<AutomataData>()), tokenAutomata(nullptr),
      astAutomata(nullptr) {}

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

BuildAutomataResult
RuntimeAutomataAstApplication::setContext(const std::string *automataFilePath) {
  PersistentData persistentData;
  BuildAutomataResult buildAutomataResult =
      persistentData.init(automataFilePath);
  //初始化错误（可能原因：自动机文件不存在）
  if (!buildAutomataResult.isOk) {
    return buildAutomataResult;
  }
  PersistentObject persistentObject;
  buildAutomataResult = persistentObject.initByPersistentData(&persistentData);
  if (!buildAutomataResult.isOk) {
    return buildAutomataResult;
  }

  auto ptrAutomataData = this->automataData.get();
  // all heap data is moved
  persistentObject.setAutomataData(ptrAutomataData);

  TokenAutomataBuilder tokenAutomataBuilder;
  tokenAutomata = tokenAutomataBuilder.build(ptrAutomataData);

  AstAutomataBuilder astAutomataBuilder;
  astAutomata = astAutomataBuilder.build(ptrAutomataData);
  return {true, ""};
}

AstGeneratorResult *
RuntimeAutomataAstApplication::buildAst(const std::string *sourceCodeFilePath) {
  TokensResult *tokensResult = tokenAutomata->buildToken(sourceCodeFilePath);
  AstResult *astResult = nullptr;
  if (tokensResult->isOk()) {
    astResult = astAutomata->buildAst(tokensResult->getOkData());
  } else {
    astResult = AstResult::generateTokensErrorResult();
  }

  return new AstGeneratorResult(tokensResult, astResult);
}

RuntimeAutomataAstApplication *RuntimeAutomataAstApplication::clone() {
  auto *app = new RuntimeAutomataAstApplication();
  cloneDataToCloner(app);
  return app;
}

void RuntimeAutomataAstApplication::cloneDataToCloner(
    RuntimeAutomataAstApplication *cloner) {
  cloneLock.lock();
  //引用计数增加
  cloner->automataData = this->automataData;
  cloneLock.unlock();

  AutomataData *ptrAutomataData = cloner->automataData.get();

  TokenAutomataBuilder tokenAutomataBuilder;
  cloner->tokenAutomata = tokenAutomataBuilder.build(ptrAutomataData);

  AstAutomataBuilder astAutomataBuilder;
  cloner->astAutomata = astAutomataBuilder.build(ptrAutomataData);
}

std::vector<AstGrammar> RuntimeAutomataAstApplication::getGrammars() {
  std::vector<AstGrammar> grammars(automataData->sizeOfGramamrs);
  for (int indexOfGrammars = 0; indexOfGrammars < automataData->sizeOfGramamrs;
       indexOfGrammars++) {
    auto originalGrammar = automataData->grammars[indexOfGrammars];
    AstGrammar grammar(originalGrammar->name, originalGrammar->type);
    grammars[indexOfGrammars] = grammar;
  }
  return grammars;
}
