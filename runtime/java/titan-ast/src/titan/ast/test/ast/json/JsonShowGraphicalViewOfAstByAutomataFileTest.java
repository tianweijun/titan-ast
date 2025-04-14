package titan.ast.test.ast.json;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;
import titan.ast.logger.Logger;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class JsonShowGraphicalViewOfAstByAutomataFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-automataFilePath",
      "D://github-pro/titan/titan-ast/test/json/automata.data",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/json/titanLanguageConfig.json",
      "-graphicalViewOfAst"
    };

    new CommandLineAstApplication(testArgs,
        new DefaultGrammarFileAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();
    Logger.info("[ShowGraphicalViewOfAstByAutomataFileTest]: run end");
  }
}
