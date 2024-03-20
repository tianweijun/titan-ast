package titan.ast.runtime;

/**
 * Application for Test.
 *
 * @author tian wei jun
 */
public class Application {

  public static void main(String[] args) {
    String automataFilePath = null;
    String sourceCodeFilePath = null;
    if (null != args && args.length >= 2) {
      automataFilePath = args[0];
      sourceCodeFilePath = args[1];
    } else {
      return;
    }
    RuntimeAutomataAstApplication runtimeAstApplication =
        new RuntimeAutomataAstApplication(automataFilePath);

    Ast ast = runtimeAstApplication.buildAst(sourceCodeFilePath);
    runtimeAstApplication.displayGraphicalViewOfAst(ast);
  }
}
