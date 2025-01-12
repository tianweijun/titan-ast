package titan.ast.runtime;

/**
 * CommandLineAstApplication.
 *
 * @author tian wei jun
 */
public class CommandLineAstApplication {

  public static void main(String[] args) {
    if (null != args && args.length >= 2) {
      String automataFilePath = args[0];
      String sourceCodeFilePath = args[1];
      String charsetName = null;
      if (args.length >= 3) {
        charsetName = args[2];
      }
      showAstByRuntimeRichAstApplication(automataFilePath, sourceCodeFilePath, charsetName);
      return;
    }
    Logger.info("<automataFilePath> <sourceCodeFilePath> [charsetName]");
  }

  private static void showAstByRuntimeRichAstApplication(
      String automataFilePath, String sourceCodeFilePath, String charsetName) {
    RuntimeAutomataRichAstApplication runtimeAstApplication =
        new RuntimeAutomataRichAstApplication();
    try {
      runtimeAstApplication.setContext(automataFilePath);
    } catch (AutomataDataIoException e) {
      Logger.info(e.getMessage());
      return;
    }
    RichAstGeneratorResult astGeneratorResult =
        runtimeAstApplication.buildRichAst(sourceCodeFilePath);
    if (astGeneratorResult.isOk()) {
      runtimeAstApplication.displayGraphicalViewOfAst(astGeneratorResult.getOkAst(), charsetName);
    } else {
      Logger.info(astGeneratorResult.getErrorMsg());
    }
  }
}
