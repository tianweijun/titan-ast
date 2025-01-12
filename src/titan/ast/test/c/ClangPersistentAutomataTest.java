package titan.ast.test.c;

import titan.ast.CommandLineAstApplication;

/**
 * 生成持久化自动机代码测试.
 *
 * @author tian wei jun
 */
public class ClangPersistentAutomataTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/c/C.grammar",
      "-languageOfPersistentAutomata",
      "java",
      "-persistentAutomataFilePath",
      "D://github-pro/titan/titan-ast/test/c/automata.data"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
