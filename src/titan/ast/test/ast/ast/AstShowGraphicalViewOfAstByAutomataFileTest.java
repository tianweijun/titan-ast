package titan.ast.test.ast.ast;

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
public class AstShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) throws AutomataDataIoException {
    String[] testArgs = {
        "-automataFilePath",
        "D://github-pro/titan/titan-ast/test/ast/titanAstGrammar.automata",
        "-sourceFilePath",
        "D://github-pro/titan/titan-ast/test/ast/titanAstGrammar.txt",
        "-graphicalViewOfAst"
    };

    new CommandLineAstApplication(testArgs,
        new DefaultGrammarFileAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();

    Logger.info("[ShowGraphicalViewOfAstByAutomataFileTest]: run end");
  }
}
