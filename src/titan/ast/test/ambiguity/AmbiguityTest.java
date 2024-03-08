package titan.ast.test.ambiguity;

import titan.ast.CommandLineAstApplication;

/**
 * 语法是否具有二义性测试.
 *
 * @author tian wei jun
 */
public class AmbiguityTest {
  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath", "D://github-pro/titan/titan-ast/test/json/json.grammar", "--isAmbiguous"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
