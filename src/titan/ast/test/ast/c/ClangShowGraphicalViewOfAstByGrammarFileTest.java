package titan.ast.test.ast.c;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;

public class ClangShowGraphicalViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
        "-grammarFilePath",
        "D://github-pro/titan/titan-ast/test/c/C.grammar",
        "-sourceFilePath",
        "D://github-pro/titan/titan-ast/test/c/helloworld.c",
        "-graphicalViewOfAst"
    };

    new CommandLineAstApplication(testArgs,
        new DefaultGrammarAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();
  }
}
