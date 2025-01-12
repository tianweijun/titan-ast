package titan.ast.test.json;

import titan.ast.CommandLineAstApplication;

/**
 * 生成持久化自动机代码测试.
 *
 * @author tian wei jun
 */
public class JsonPersistentAutomataTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/json/json.grammar",
      "-persistentAutomataFilePath",
      "D://github-pro/titan/titan-ast/test/json/automata.data"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
