package titan.ast.test.titanlang;

import titan.ast.CommandLineAstApplication;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class TitanlangShowGraphicalViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePaths",
      "D://github-pro/titan/titan-ast/test/titanlang/titanLanguageLexer.grammar",
      "D://github-pro/titan/titan-ast/test/titanlang/titanLanguageParser.grammar",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/titanlang/helloworld.titan",
      "--graphicalViewOfAst"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
