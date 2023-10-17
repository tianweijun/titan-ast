package titan.ast.test.java;

import titan.ast.logger.Logger;
import titan.ast.runtime.Ast;
import titan.ast.runtime.RuntimeAutomataAstApplication;
import titan.ast.test.StopWatch;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class JavaShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) {
    String automataFilePath = "D://github-pro/titan/titan-ast/test/java/automata.data";
    String sourceCodeFilePath = "D://github-pro/titan/titan-ast/test/java/Helloworld.java";

    StopWatch stopWatch = new StopWatch();

    /*
    stopWatch.start();
    String[] testArgs = {
        "-automataFilePath",
        automataFilePath,
        "-sourceFilePath",
        sourceCodeFilePath,
        "--graphicalViewOfAst"
    };
    new CommandLineAstApplication().run(testArgs);
    stopWatch.stop();
    Logger.info("build CommandLineAstApplication,time:", Long.toString(stopWatch.getMillTime()));
    */

    stopWatch.start();
    RuntimeAutomataAstApplication runtimeAstApplication = new RuntimeAutomataAstApplication();
    runtimeAstApplication.setContext(automataFilePath);
    stopWatch.stop();
    Logger.info("build runtimeAstApplication,time:", Long.toString(stopWatch.getMillTime()));

    stopWatch.start();
    Ast ast = runtimeAstApplication.buildAst(sourceCodeFilePath);
    stopWatch.stop();
    Logger.info("build ast by AutomataFile", String.format("time:%d", stopWatch.getMillTime()));
    runtimeAstApplication.displayGraphicalViewOfAst(ast);

    Logger.info("ShowGraphicalViewOfAstByAutomataFileTest", "run end");
  }
}
