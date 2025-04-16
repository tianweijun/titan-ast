package titan.ast.test.ast.java;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;

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

    new CommandLineAstApplication(testArgs,
        new DefaultGrammarAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();
  }
}
