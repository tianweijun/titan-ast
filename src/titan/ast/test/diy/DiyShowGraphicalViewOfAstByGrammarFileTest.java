package titan.ast.test.diy;

import titan.ast.grammar.GrammarFileAutomataAstApplication;
import titan.ast.target.Ast;

public class DiyShowGraphicalViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/diy/diy.grammar",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/diy/diy.txt",
      "--graphicalViewOfAst"
    };

    // new CommandLineAstApplication().run(testArgs);

    GrammarFileAutomataAstApplication grammarFileAutomataAstApplication =
        new GrammarFileAutomataAstApplication();
    grammarFileAutomataAstApplication.setAstAutomataContext(testArgs[1]);

    Ast ast = grammarFileAutomataAstApplication.buildAst(testArgs[3]);
    grammarFileAutomataAstApplication.displayGraphicalViewOfAst(ast);
  }
}
