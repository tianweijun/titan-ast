package titan.ast.test.diy;

import titan.ast.CommandLineAstApplication;
import titan.ast.logger.Logger;
import titan.ast.test.StopWatch;

/**
 * 生成持久化自动机代码测试.
 *
 * @author tian wei jun
 */
public class DiyPersistentAutomataTest {

  public static void main(String[] args) {
    StopWatch stopWatch = new StopWatch();

    stopWatch.start();
    String[] testArgs = {
      "-grammarFilePaths",
      "D://github-pro/titan/titan-ast/test/diy/diy.grammar",
      "-persistentAutomataFilePath",
      "D://github-pro/titan/titan-ast/test/diy/automata.data"
    };
    new CommandLineAstApplication().run(testArgs);
    stopWatch.stop();
    Logger.info(String.format("time:%s", stopWatch.getSecondsTime()));
  }
}
