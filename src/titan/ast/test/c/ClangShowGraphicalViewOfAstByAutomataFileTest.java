package titan.ast.test.c;

import titan.ast.logger.Logger;
import titan.ast.runtime.Ast;
import titan.ast.runtime.RuntimeAutomataAstApplication;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class ClangShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) {
    String automataFilePath = "D://github-pro/titan/titan-ast/test/c/automata.data";
    String sourceCodeFilePath = "D://github-pro/titan/titan-ast/test/c/helloworld.c";

    // String sourceCodeFilePath = "D:/test/c_runtime/helloworld.c";

    RuntimeAutomataAstApplication runtimeAstApplication = new RuntimeAutomataAstApplication();
    runtimeAstApplication.setContext(automataFilePath);

    Ast ast = runtimeAstApplication.buildAst(sourceCodeFilePath);

    runtimeAstApplication.displayGraphicalViewOfAst(ast);

    Logger.info("ShowGraphicalViewOfAstByAutomataFileTest", "run end");
  }
}
