package titan.ast.test.ast.java;

import titan.ast.logger.Logger;
import titan.ast.runtime.AutomataDataIoException;
import titan.ast.runtime.RichAstGeneratorResult;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class JavaShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) throws AutomataDataIoException {
    String automataFilePath = "D://github-pro/titan/titan-ast/test/java/automata.data";
    String sourceCodeFilePath = "D://github-pro/titan/titan-ast/test/java/Helloworld.java";


    RuntimeAutomataRichAstApplication runtimeAstApplication =
        new RuntimeAutomataRichAstApplication();
    runtimeAstApplication.setContext(automataFilePath);


    RichAstGeneratorResult richAstGeneratorResult =
        runtimeAstApplication.buildRichAst(sourceCodeFilePath);

    if (richAstGeneratorResult.isOk()) {
      runtimeAstApplication.displayGraphicalViewOfAst(richAstGeneratorResult.getOkAst());
    } else {
      Logger.info(richAstGeneratorResult.getErrorMsg());
    }

    Logger.info("[ShowGraphicalViewOfAstByAutomataFileTest]: run end");
  }
}
