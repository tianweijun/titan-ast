package titan.ast.test.c;

import titan.ast.CommandLineAstApplication;

public class ClangShowGraphicalViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/c/C.grammar",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/c/helloworld.c",
      "-graphicalViewOfAst"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
