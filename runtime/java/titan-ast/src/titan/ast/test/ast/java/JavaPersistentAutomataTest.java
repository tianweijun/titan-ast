package titan.ast.test.ast.java;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;

/**
 * 生成持久化自动机代码测试.
 *
 * @author tian wei jun
 */
public class JavaPersistentAutomataTest {

  public static void main(String[] args) {

    String[] testArgs = {
      "-grammarFilePaths",
      "D://github-pro/titan/titan-ast/test/java/Java8Lexer.grammar",
      "D://github-pro/titan/titan-ast/test/java/Java8Parser.grammar",
      "-persistentAutomataFilePath",
      "D://github-pro/titan/titan-ast/test/java/automata.data"
    };
    new CommandLineAstApplication(testArgs,
        new DefaultGrammarFileAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();
  }
}
