package titan.ast.test.json;

import titan.ast.CommandLineAstApplication;
import titan.ast.logger.Logger;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class JsonShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-automataFilePath",
      "D://github-pro/titan/titan-ast/test/json/automata.data",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/json/titanLanguageConfig.json",
      "-graphicalViewOfAst"
    };

    new CommandLineAstApplication().run(testArgs);
    Logger.info("[ShowGraphicalViewOfAstByAutomataFileTest]: run end");
  }
}
