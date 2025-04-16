package titan.ast.test.ast.diy;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;
import titan.ast.logger.Logger;
import titan.ast.runtime.AutomataDataIoException;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class DiyShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) throws AutomataDataIoException {
    String[] testArgs = {
      "-automataFilePath",
      "D://github-pro/titan/titan-ast/test/diy/automata.data",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/diy/diy.txt",
      "-graphicalViewOfAst","utf-8"
    };
    CommandLineAstApplication commandLineAstApplication = new CommandLineAstApplication(testArgs,
        new DefaultGrammarAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION));
    commandLineAstApplication.run();

    Logger.info("[ShowGraphicalViewOfAstByAutomataFileTest]: run end");
  }
}
