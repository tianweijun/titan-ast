package titan.ast.test.c;

import titan.ast.logger.Logger;
import titan.ast.runtime.AutomataDataIoException;
import titan.ast.runtime.RichAstGeneratorResult;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class ClangShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) throws AutomataDataIoException {
    String automataFilePath = "D://github-pro/titan/titan-ast/test/c/automata.data";
    String sourceCodeFilePath = "D://github-pro/titan/titan-ast/test/c/helloworld.c";

    // String sourceCodeFilePath = "D:/test/c_runtime/helloworld.c";
    RuntimeAutomataRichAstApplication runtimeAstApplication =
        new RuntimeAutomataRichAstApplication();
    runtimeAstApplication.setContext(automataFilePath);

    RichAstGeneratorResult astGeneratorResult =
        runtimeAstApplication.buildRichAst(sourceCodeFilePath);

    if (astGeneratorResult.isOk()) {
      runtimeAstApplication.displayGraphicalViewOfAst(astGeneratorResult.getOkAst());
    } else {
      Logger.info(astGeneratorResult.getErrorMsg());
    }

    Logger.info("[ShowGraphicalViewOfAstByAutomataFileTest]: run end");
  }
}
