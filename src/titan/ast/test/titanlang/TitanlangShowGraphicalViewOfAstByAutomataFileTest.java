package titan.ast.test.titanlang;

import titan.ast.logger.Logger;
import titan.ast.runtime.AutomataDataIoException;
import titan.ast.runtime.RichAstGeneratorResult;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;
import titan.ast.test.StopWatch;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class TitanlangShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) throws AutomataDataIoException {
    String automataFilePath = "D://github-pro/titan/titan-ast/test/titanlang/automata.data";
    String sourceCodeFilePath = "D://github-pro/titan/titan-ast/test/titanlang/helloworld.titan";

    StopWatch stopWatch = new StopWatch();

    stopWatch.start();
    RuntimeAutomataRichAstApplication runtimeAstApplication =
        new RuntimeAutomataRichAstApplication();
    runtimeAstApplication.setContext(automataFilePath);
    stopWatch.stop();
    Logger.info(String.format("build runtimeAstApplication,time:%s", stopWatch.getMillTime()));

    stopWatch.start();
    RichAstGeneratorResult richAstGeneratorResult =
        runtimeAstApplication.buildRichAst(sourceCodeFilePath);
    stopWatch.stop();
    Logger.info(
        String.format("build ast by AutomataFile,build ast time:%d", stopWatch.getMillTime()));

    if (richAstGeneratorResult.isOk()) {
      runtimeAstApplication.displayGraphicalViewOfAst(richAstGeneratorResult.getOkAst());
    } else {
      Logger.info(richAstGeneratorResult.getErrorMsg());
    }

    Logger.info("[ShowGraphicalViewOfAstByAutomataFileTest]: run end");
  }
}
