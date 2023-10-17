package titan.ast.test.diy;

import java.util.List;
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
    grammarFileAutomataAstApplication.setContext(testArgs[1]);

    List<Ast> asts = grammarFileAutomataAstApplication.buildAsts(testArgs[3]);
    for (Ast ast : asts) {
      grammarFileAutomataAstApplication.displayGraphicalViewOfAst(ast);
    }
  }
}
