package titan.ast.test.json;

import titan.ast.CommandLineAstApplication;

public class JsonShowGraphicalViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/json/json.grammar",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/json/titanLanguageConfig.json",
      "-graphicalViewOfAst"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
