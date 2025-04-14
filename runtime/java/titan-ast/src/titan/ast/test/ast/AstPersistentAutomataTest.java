package titan.ast.test.ast;

import titan.ast.CommandLineAstApplication;

/**
 * 生成持久化自动机代码测试.
 *
 * @author tian wei jun
 */
public class AstPersistentAutomataTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/ast/titanAstGrammar.txt",
      "-persistentAutomataFilePath",
      "D://github-pro/titan/titan-ast/test/ast/titanAstGrammar.automata"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
