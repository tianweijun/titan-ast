package titan.ast.test.diy;

import titan.ast.logger.Logger;
import titan.ast.runtime.AstGeneratorResult;
import titan.ast.runtime.AutomataDataIoException;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class DiyShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) throws AutomataDataIoException {
    String[] testArgs = {
      "-automataFilePath",
      "D://github-pro/titan/titan-ast/test/diy/automata.data",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/diy/diy.txt",
      "-graphicalViewOfAst"
    };
    RuntimeAutomataRichAstApplication runtimeAutomataAstApplication =
        new RuntimeAutomataRichAstApplication();
    runtimeAutomataAstApplication.setContext(testArgs[1]);

    AstGeneratorResult astGeneratorResult = runtimeAutomataAstApplication.buildAst(testArgs[3]);
    if (astGeneratorResult.isOk()) {
      runtimeAutomataAstApplication.displayGraphicalViewOfAst(astGeneratorResult.getOkAst());
    } else {
      Logger.info(astGeneratorResult.getErrorMsg());
    }

    Logger.info("[ShowGraphicalViewOfAstByAutomataFileTest]: run end");
  }
}
