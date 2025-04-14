package titan.ast.test.ast.diy;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;

/**
 * 生成持久化自动机代码测试.
 *
 * @author tian wei jun
 */
public class DiyPersistentAutomataTest {

  public static void main(String[] args) {

    String[] testArgs = {
        "-grammarFilePaths",
        "D://github-pro/titan/titan-ast/test/diy/diy.grammar",
        "-persistentAutomataFilePath",
        "D://github-pro/titan/titan-ast/test/diy/automata.data"
    };
    new CommandLineAstApplication(testArgs,
        new DefaultGrammarFileAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();
  }
}
