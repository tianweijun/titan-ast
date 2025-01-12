package titan.ast.test.nfareg;

import titan.ast.CommandLineAstApplication;

public class NfaregShowViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/nfareg/nfareg.grammar",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/nfareg/helloworld.txt",
      "-graphicalViewOfAst",
      "utf-8"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
