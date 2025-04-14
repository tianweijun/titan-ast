package titan.ast.test.nfareg;

import titan.ast.CommandLineAstApplication;
import titan.ast.logger.Logger;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class NfaregShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-automataFilePath",
      "D://github-pro/titan/titan-ast/test/nfareg/automata.data",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/nfareg/helloworld.txt",
      "-graphicalViewOfAst"
    };

    new CommandLineAstApplication().run(testArgs);
    Logger.info("[ShowGraphicalViewOfAstByAutomataFileTest]: run end");
  }
}
