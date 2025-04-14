package titan.ast.test.nfareg;

import titan.ast.CommandLineAstApplication;

/**
 * 生成持久化自动机代码测试.
 *
 * @author tian wei jun
 */
public class NfaregPersistentAutomataTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/nfareg/nfareg.grammar",
      "-persistentAutomataFilePath",
      "D://github-pro/titan/titan-ast/test/nfareg/automata.data"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
