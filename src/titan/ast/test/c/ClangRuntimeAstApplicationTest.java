package titan.ast.test.c;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import titan.ast.logger.Logger;
import titan.ast.runtime.AutomataDataIoException;
import titan.ast.runtime.RichAstGeneratorResult;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

/**
 * RuntimeAstApplicationTest.
 *
 * @author tian wei jun
 */
public class ClangRuntimeAstApplicationTest {

  public static void main(String[] args)
      throws ExecutionException, InterruptedException, AutomataDataIoException {
    multithreadedTest(3);
  }

  private static void multithreadedTest(int countOfTask)
      throws ExecutionException, InterruptedException, AutomataDataIoException {
    String automataFilePath = "D://github-pro/titan/titan-ast/test/c/automata.data";
    String sourceCodeFilePath = "D://github-pro/titan/titan-ast/test/c/helloworld.c";

    ExecutorService executorService = Executors.newFixedThreadPool(countOfTask);

    ArrayList<Future<RichAstGeneratorResult>> futures = new ArrayList<>(countOfTask);

    RuntimeAutomataRichAstApplication runtimeAstApplication =
        new RuntimeAutomataRichAstApplication();
    runtimeAstApplication.setContext(automataFilePath);
    ArrayList<RuntimeAutomataRichAstApplication> apps = new ArrayList<>(countOfTask);
    apps.add(runtimeAstApplication);
    for (int i = 1; i < countOfTask; i++) {
      apps.add(runtimeAstApplication.clone());
    }

    for (RuntimeAutomataRichAstApplication app : apps) {
      futures.add(
          executorService.submit(
              () -> {
                RichAstGeneratorResult astGeneratorResult = app.buildRichAst(sourceCodeFilePath);
                if (astGeneratorResult.isOk()) {
                  app.displayGraphicalViewOfAst(astGeneratorResult.getOkAst());
                } else {
                  Logger.info(astGeneratorResult.getErrorMsg());
                }
                return astGeneratorResult;
              }));
    }

    for (Future<RichAstGeneratorResult> future : futures) {
      future.get();
    }

    executorService.shutdown();
  }
}
