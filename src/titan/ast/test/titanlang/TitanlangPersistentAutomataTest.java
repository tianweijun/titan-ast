package titan.ast.test.titanlang;

import titan.ast.CommandLineAstApplication;

/**
 * 生成持久化自动机代码测试.
 *
 * @author tian wei jun
 */
public class TitanlangPersistentAutomataTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePaths",
      "D://github-pro/titan/titan-ast/test/titanlang/titanLanguageLexer.grammar",
      "D://github-pro/titan/titan-ast/test/titanlang/titanLanguageParser.grammar",
      "-persistentAutomataFilePath",
      "D://github-pro/titan/titan-ast/test/titanlang/automata.data"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
