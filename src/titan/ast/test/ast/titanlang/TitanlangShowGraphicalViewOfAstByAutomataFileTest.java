package titan.ast.test.ast.titanlang;

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
public class TitanlangShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) throws AutomataDataIoException {
    String[] testArgs = {
        "-automataFilePath",
        "D://github-pro/titan/titan-ast/test/titanlang/titanLanguageGrammar.automata",
        "-sourceFilePath",
        "D://github-pro/titan/titan-ast/test/titanlang/helloworld.titan",
        "-graphicalViewOfAst","utf-8"
    };

    new CommandLineAstApplication(testArgs,
        new DefaultGrammarAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();
    Logger.info("[ShowGraphicalViewOfAstByAutomataFileTest]: run end");
  }
}
