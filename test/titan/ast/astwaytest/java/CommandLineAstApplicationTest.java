package titan.ast.astwaytest.java;

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
  private final String[] grammarFilePaths =
      new String[] {
        "D://github-pro/titan/titan-ast/test-resources/java/Java8Lexer.grammar",
        "D://github-pro/titan/titan-ast/test-resources/java/Java8Parser.grammar"
      };
  private final String automataFilePath =
      "D://github-pro/titan/titan-ast/test-resources/java/automata.data";
  private final String sourceFilePath =
      "D://github-pro/titan/titan-ast/test-resources/java/Helloworld.java";

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
      "-grammarFilePaths",
      grammarFilePaths[0],
      grammarFilePaths[1],
      "-persistentAutomataFilePath",
      automataFilePath
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
      "-grammarFilePaths",
      grammarFilePaths[0],
      grammarFilePaths[1],
      "-sourceFilePath",
      sourceFilePath,
      "-graphicalViewOfAst"
    };

    runCommandLineAstApplication(testArgs);
  }
}