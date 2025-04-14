package titan.ast.test.ast.visitor;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;

/**
 * .
 *
 * @author tian wei jun
 */
public class BuildingAstVisitorTest {
  public static void main(String[] args) {
    String[] testArgs =
        new String[] {
            "-grammarFilePaths",
            "D:\\github-pro\\titan\\titan-ast\\runtime\\java\\titan-ast\\src\\resources\\titanAstGrammar.txt",
            "-astVisitorFileDirectory",
            //"D:\\github-pro\\titan\\titan-ast\\runtime\\java\\titan-ast\\src\\titan\\ast\\impl\\ast\\contextast\\",
            "C:\\Users\\june\\Desktop\\s\\",
            "titan.ast.impl.ast.contextast"
        };

    new CommandLineAstApplication(testArgs,
        new DefaultGrammarFileAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();
  }
}
