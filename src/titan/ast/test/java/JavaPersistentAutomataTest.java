package titan.ast.test.java;

import titan.ast.CommandLineAstApplication;
import titan.ast.logger.Logger;
import titan.ast.test.StopWatch;

/**
 * 生成持久化自动机代码测试.
 *
 * @author tian wei jun
 */
public class JavaPersistentAutomataTest {

  public static void main(String[] args) {
    StopWatch stopWatch = new StopWatch();

    stopWatch.start();
    String[] testArgs = {
      "-grammarFilePaths",
      "D://github-pro/titan/titan-ast/test/java/Java8Lexer.grammar",
      "D://github-pro/titan/titan-ast/test/java/Java8Parser.grammar",
      "-persistentAutomataFilePath",
      "D://github-pro/titan/titan-ast/test/java/automata.data"
    };
    new CommandLineAstApplication().run(testArgs);
    stopWatch.stop();
    Logger.info(String.format("time:%s", stopWatch.getSecondsTime()));
  }
}
