package titan.ast.test;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;

/**
 * .
 *
 * @author tian wei jun
 */
public class AstWayGrammarFileAutomataAstApplicationTest {

  public static void main(String[] args) {
    CommandLineAstApplication commandLineAstApplication = new CommandLineAstApplication(args,
        new DefaultGrammarFileAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION));
    commandLineAstApplication.run();
  }
}
