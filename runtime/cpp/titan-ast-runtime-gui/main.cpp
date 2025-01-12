#include "gui/AstGuiOutputer.h"
#include <list>
#include <string>
#include <thread>
#include <iostream>

void testSinglethreadedApp(std::string automataFilePath,
                           std::string sourceCodeFilePath);
void testMultithreadedApp(std::string automataFilePath,
                          std::string sourceCodeFilePath, int taskCount);

int main(int argc, char *argv[]) {
  const std::string automataFilePath = argv[1];
  const std::string sourceCodeFilePath = argv[2];
  const std::string isSingleTask = argv[3];
  if (isSingleTask == "true") {
    testSinglethreadedApp(automataFilePath, sourceCodeFilePath);
  } else {
    testMultithreadedApp(automataFilePath, sourceCodeFilePath, 3);
  }
  return 0;
}

void testSinglethreadedApp(std::string automataFilePath,
                           std::string sourceCodeFilePath) {
  /*
    const std::string automataFilePath =
        "D:/github-pro/titan/titan-ast/test/c/automata.data";
    const std::string sourceCodeFilePath =
        "D:/github-pro/titan/titan-ast/test/c/helloworld.c";
        */

  RuntimeAutomataRichAstApplication runtimeAstApplication;
  BuildAutomataResult buildAutomataResult =
      runtimeAstApplication.setContext(&automataFilePath);
  //初始化错误（可能原因：自动机文件不存在，文件数损坏）
  if (!buildAutomataResult.isOk) {
    std::cout << buildAutomataResult.msg << std::endl;
    return;
  }

  /*
  auto grammars = runtimeAstApplication.getGrammars();
  for(AstGrammar g : grammars){
   std::cout<<g.name<<std::endl;
  }*/

  const RichAstGeneratorResult *astGeneratorResult =
      runtimeAstApplication.buildRichAst(&sourceCodeFilePath);
  if (astGeneratorResult->isOk()) {
    AstGuiOutputer astGuiOutputer(astGeneratorResult->getOkAst());
    astGuiOutputer.output();
    astGuiOutputer.waitToClose();
  } else {
    std::cout << astGeneratorResult->getErrorMsg() << std::endl;
  }

  delete astGeneratorResult;
}

void doTask(AstGeneratorResult **result,
            RuntimeAutomataAstApplication *runtimeAstApplication,
            const std::string *sourceCodeFilePath) {
  *result = runtimeAstApplication->buildAst(sourceCodeFilePath);
}

void doRichTask(RichAstGeneratorResult **result,
                RuntimeAutomataAstApplication *runtimeAstApplication,
                const std::string *sourceCodeFilePath) {
  *result = ((RuntimeAutomataRichAstApplication *)runtimeAstApplication)
                ->buildRichAst(sourceCodeFilePath);
}

void testMultithreadedApp(std::string automataFilePath,
                          std::string sourceCodeFilePath, int taskCount) {

  auto *runtimeAstApplication = new RuntimeAutomataRichAstApplication();
  auto buildAutomataResult =
      runtimeAstApplication->setContext(&automataFilePath);
  //初始化错误（可能原因：自动机文件不存在，文件数损坏）
  if (!buildAutomataResult.isOk) {
    std::cout << buildAutomataResult.msg << std::endl;
    return;
  }

  auto **asts = new RichAstGeneratorResult *[taskCount];
  for (int i = 0; i < taskCount; i++) {
    asts[i] = nullptr;
  }

  auto **threads = new std::thread *[taskCount];
  auto **apps = new RuntimeAutomataAstApplication *[taskCount];

  // first app
  apps[0] = runtimeAstApplication;
  // first thread
  threads[0] = new std::thread(doRichTask, &(asts[0]), runtimeAstApplication,
                               &sourceCodeFilePath);
  // create other threads and apps
  for (int i = 1; i < taskCount; i++) {
    apps[i] = runtimeAstApplication->clone();
    threads[i] =
        new std::thread(doRichTask, &(asts[i]), apps[i], &sourceCodeFilePath);
  }

  // wait task done
  for (int i = 0; i < taskCount; i++) {
    threads[i]->join();
  }
  // tasks are done
  for (int i = 0; i < taskCount; i++) {
    delete threads[i];
  }
  delete[] threads;

  for (int i = 0; i < taskCount; i++) {
    delete apps[i];
  }
  delete[] apps;

  // show asts result
  for (int i = 0; i < taskCount; i++) {
    auto astGeneratorResult = asts[i];
    if (astGeneratorResult->isOk()) {
      printf("thread-%d:ast is ok.\n", i);
    } else {
      std::cout << "thread-" << i
                << ":ast is not ok,error:" << astGeneratorResult->getErrorMsg()
                << std::endl;
    }
  }

  // future AstGuiOutputer改为支持多线程多个diaolog显示，将其放入doTask中
  if (asts[1]->isOk()) {
    AstGuiOutputer astGuiOutputer(asts[1]->getOkAst());
    astGuiOutputer.output();
    astGuiOutputer.waitToClose();
  }

  // delete asts
  for (int i = 0; i < taskCount; i++) {
    delete asts[i];
  }
  delete[] asts;
}