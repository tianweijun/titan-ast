#include "gui/AstGuiOutputer.h"
#include <list>
#include <string>
#include <thread>

void testSinglethreadedApp();

void doTask(Ast **result, RuntimeAutomataAstApplication *runtimeAstApplication,
            std::string const *sourceCodeFilePath);
void testMultithreadedApp(int taskCount);

int main(int argc, char *argv[]) {
  //testSinglethreadedApp();
  testMultithreadedApp(3);
  return 0;
}

void doTask(Ast **result, RuntimeAutomataAstApplication *runtimeAstApplication,
            std::string const *sourceCodeFilePath) {
  *result = const_cast<Ast *>(runtimeAstApplication->buildAst(sourceCodeFilePath));
  AstRuntimeExceptionResolver::destory();
}

void testMultithreadedApp(int taskCount) {

  const std::string automataFilePath = "D:/github-pro/titan/titan-ast/test/c/automata.data";
  const std::string sourceCodeFilePath = "D:/github-pro/titan/titan-ast/test/c/helloworld.c";

  //const std::string automataFilePath = "D:/github-pro/titan/titan-ast/test/diy/automata.data";
  //const std::string sourceCodeFilePath = "D:/github-pro/titan/titan-ast/test/diy/diy.txt";

  auto *runtimeAstApplication = new RuntimeAutomataAstApplication();
  runtimeAstApplication->setContext(&automataFilePath);
  //初始化错误（可能原因：自动机文件不存在，文件数损坏）
  if (AstRuntimeExceptionResolver::hasThrewException()) {
    AstRuntimeExceptionResolver::destory();
    return;
  }

  Ast **asts = new Ast *[taskCount];
  for (int i = 0; i < taskCount; i++) {
    asts[i] = nullptr;
  }

  auto **threads = new std::thread *[taskCount];
  auto **apps = new RuntimeAutomataAstApplication *[taskCount];

  //first app
  apps[0] = runtimeAstApplication;
  //first thread
  threads[0] = new std::thread(doTask, &(asts[0]), runtimeAstApplication, &sourceCodeFilePath);
  //create other threads and apps
  for (int i = 1; i < taskCount; i++) {
    apps[i] = runtimeAstApplication->clone();
    threads[i] = new std::thread(doTask, &(asts[i]), apps[i], &sourceCodeFilePath);
  }

  //wait task done
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

  //show asts result
  for (int i = 0; i < taskCount; i++) {
    Ast* ast = asts[i];
    if (ast) {
      printf("thread-%d:ast is ok.\n",i);
    } else {
      printf("thread-%d:ast is not ok.\n",i);
    }
  }

  //future AstGuiOutputer改为支持多线程多个diaolog显示，将其放入doTask中
  if (asts[1]) {
    AstGuiOutputer astGuiOutputer(asts[1]);
    astGuiOutputer.output();
    astGuiOutputer.waitToClose();
  }

  //delete asts
  for (int i = 0; i < taskCount; i++) {
    delete asts[i];
  }
  delete[] asts;
  AstRuntimeExceptionResolver::destory();
}

void testSinglethreadedApp() {
  //const std::string automataFilePath = "D:/github-pro/titan/titan-ast/test/json/automata.data";
  //const std::string sourceCodeFilePath = "D:/github-pro/titan/titan-ast/test/json/titanLanguageConfig.json";

  //const std::string automataFilePath = "D:/github-pro/titan/titan-ast/test/diy/automata.data";
  //const std::string sourceCodeFilePath = "D:/github-pro/titan/titan-ast/test/diy/diy.txt";

  const std::string automataFilePath = "D:/github-pro/titan/titan-ast/test/c/automata.data";
  const std::string sourceCodeFilePath = "D:/github-pro/titan/titan-ast/test/c/helloworld.c";

  //const std::string sourceCodeFilePath = "D:/test/c_runtime/helloworld.c";

  RuntimeAutomataAstApplication runtimeAstApplication;
  runtimeAstApplication.setContext(&automataFilePath);
  //初始化错误（可能原因：自动机文件不存在，文件数损坏）
  if (AstRuntimeExceptionResolver::hasThrewException()) {
    AstRuntimeExceptionResolver::destory();
    return;
  }
  const Ast *ast = runtimeAstApplication.buildAst(&sourceCodeFilePath);
  if (ast) {
    AstGuiOutputer astGuiOutputer(ast);
    astGuiOutputer.output();
    astGuiOutputer.waitToClose();
  } else {
    AstRuntimeExceptionResolver::clearExceptions();
  }

  delete ast;
  AstRuntimeExceptionResolver::destory();
}