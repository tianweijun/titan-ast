package titan.ast.test.ast.json;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;

public class JsonShowGraphicalViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/json/json.grammar",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/json/titanLanguageConfig.json",
      "-graphicalViewOfAst"
    };

    new CommandLineAstApplication(testArgs,
        new DefaultGrammarAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();
  }
}
