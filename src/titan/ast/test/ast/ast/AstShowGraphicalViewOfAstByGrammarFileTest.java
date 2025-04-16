package titan.ast.test.ast.ast;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;

public class AstShowGraphicalViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/ast/titanAstGrammar.txt",
      "-sourceFilePath",
        "D://github-pro/titan/titan-ast/test/ast/titanAstGrammar.txt",
      "-graphicalViewOfAst"
    };

    new CommandLineAstApplication(testArgs,
        new DefaultGrammarAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();
  }
}
