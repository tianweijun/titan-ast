package titan.ast.test.ast.titanlang;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;
import titan.ast.logger.Logger;
import titan.ast.runtime.AutomataDataIoException;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class TitanlangShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) throws AutomataDataIoException {
    String[] testArgs = {
        "-automataFilePath",
        "D://github-pro/titan/titan-ast/test/titanlang/automata.data",
        "-sourceFilePath",
        "D://github-pro/titan/titan-ast/test/titanlang/helloworld.titan",
        "-graphicalViewOfAst"
    };

    new CommandLineAstApplication(testArgs,
        new DefaultGrammarFileAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();
    Logger.info("[ShowGraphicalViewOfAstByAutomataFileTest]: run end");
  }
}
