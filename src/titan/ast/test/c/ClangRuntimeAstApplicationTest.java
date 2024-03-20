package titan.ast.test.c;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import titan.ast.runtime.Ast;
import titan.ast.runtime.RuntimeAutomataAstApplication;

/**
 * RuntimeAstApplicationTest.
 *
 * @author tian wei jun
 */
public class ClangRuntimeAstApplicationTest {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    multithreadedTest(3);
  }

  private static void multithreadedTest(int countOfTask)
      throws ExecutionException, InterruptedException {
    String automataFilePath = "D://github-pro/titan/titan-ast/test/c/automata.data";
    String sourceCodeFilePath = "D://github-pro/titan/titan-ast/test/c/helloworld.c";

    ExecutorService executorService = Executors.newFixedThreadPool(countOfTask);

    ArrayList<Future<Ast>> futures = new ArrayList<>(countOfTask);

    RuntimeAutomataAstApplication runtimeAstApplication =
        new RuntimeAutomataAstApplication(automataFilePath);
    ArrayList<RuntimeAutomataAstApplication> apps = new ArrayList<>(countOfTask);
    apps.add(runtimeAstApplication);
    for (int i = 1; i < countOfTask; i++) {
      apps.add(runtimeAstApplication.clone());
    }

    for (RuntimeAutomataAstApplication app : apps) {
      futures.add(
          executorService.submit(
              () -> {
                Ast ast = app.buildAst(sourceCodeFilePath);
                app.displayGraphicalViewOfAst(ast);
                return ast;
              }));
    }

    for (Future<Ast> future : futures) {
      future.get();
    }

    executorService.shutdown();
  }
}
