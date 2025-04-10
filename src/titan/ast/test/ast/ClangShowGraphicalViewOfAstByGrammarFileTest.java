package titan.ast.test.ast;

import titan.ast.CommandLineAstApplication;

public class ClangShowGraphicalViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/ast/titanAstGrammar.txt",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/c/C.grammar",
      "-graphicalViewOfAst"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
