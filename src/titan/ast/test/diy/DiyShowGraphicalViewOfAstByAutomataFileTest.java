package titan.ast.test.diy;

import java.util.List;
import titan.ast.logger.Logger;
import titan.ast.runtime.Ast;
import titan.ast.runtime.RuntimeAutomataAstApplication;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class DiyShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-automataFilePath",
      "D://github-pro/titan/titan-ast/test/diy/automata.data",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/diy/diy.txt",
      "--graphicalViewOfAst"
    };
    RuntimeAutomataAstApplication runtimeAutomataAstApplication =
        new RuntimeAutomataAstApplication();
    runtimeAutomataAstApplication.setContext(testArgs[1]);

    List<Ast> asts = runtimeAutomataAstApplication.buildAsts(testArgs[3]);
    for (Ast ast : asts) {
      runtimeAutomataAstApplication.displayGraphicalViewOfAst(ast);
    }

    Logger.info("ShowGraphicalViewOfAstByAutomataFileTest", "run end");
  }
}
