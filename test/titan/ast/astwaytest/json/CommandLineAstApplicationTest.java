package titan.ast.astwaytest.json;

import titan.ast.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author june
 */
public class CommandLineAstApplicationTest {
  private final String grammarFilePath =
      "D://github-pro/titan/titan-ast/test-resources/json/json.grammar";
  private final String automataFilePath =
      "D://github-pro/titan/titan-ast/test-resources/json/automata.data";
  private final String sourceFilePath =
      "D://github-pro/titan/titan-ast/test-resources/json/titanLanguageConfig.json";

  public CommandLineAstApplicationTest() {}

  @BeforeClass
  public static void setUpClass() {}

  @AfterClass
  public static void tearDownClass() {}

  @Before
  public void setUp() {}

  @After
  public void tearDown() {}

  private void runCommandLineAstApplication(String[] args) {
    new CommandLineAstApplication(
            args,
            new DefaultGrammarAutomataAstApplicationBuilder(
                DefaultGrammarAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum
                    .AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION))
        .run();
  }

  @Test
  public void testPersistentAutomata() {
    System.out.println("testPersistentAutomata");
    String[] testArgs = {
      "-grammarFilePath", grammarFilePath, "-persistentAutomataFilePath", automataFilePath
    };
    runCommandLineAstApplication(testArgs);
  }

  @Test
  public void testShowGraphicalViewOfAstByAutomataFile() {
    System.out.println("testShowGraphicalViewOfAstByAutomataFile");
    String[] testArgs = {
      "-automataFilePath",
      automataFilePath,
      "-sourceFilePath",
      sourceFilePath,
      "-graphicalViewOfAst"
    };

    runCommandLineAstApplication(testArgs);
  }

  @Test
  public void testShowGraphicalViewOfAstByGrammarFile() {
    System.out.println("testShowGraphicalViewOfAstByGrammarFile");
    String[] testArgs = {
      "-grammarFilePath", grammarFilePath, "-sourceFilePath", sourceFilePath, "-graphicalViewOfAst"
    };

    runCommandLineAstApplication(testArgs);
  }
}