package titan.ast.test.java;

import titan.ast.CommandLineAstApplication;

public class JavaShowGraphicalViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePaths",
      "D://github-pro/titan/titan-ast/test/java/Java8Lexer.grammar",
      "D://github-pro/titan/titan-ast/test/java/Java8Parser.grammar",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/java/Helloworld.java",
      "-graphicalViewOfAst"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
