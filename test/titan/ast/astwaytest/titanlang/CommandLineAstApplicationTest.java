package titan.ast.astwaytest.titanlang;

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
  private final String fileDirectory =
      "D:\\github-pro\\titan\\titan-ast\\test-resources\\titanlang\\";
  private final String[] grammarFilePaths =
      new String[] {
        fileDirectory + "titanLanguageAsciiLexer.txt",
        fileDirectory + "titanLanguageChineseLexer.txt",
        fileDirectory + "titanLanguageEncodingLexer.txt",
        fileDirectory + "titanLanguageNotTextTokenLexer.txt",
        fileDirectory + "titanLanguageNumberLiteralLexer.txt",
        fileDirectory + "titanLanguageCharsLiteralLexer.txt",
        fileDirectory + "titanLanguagePunctuationLexer.txt",
        fileDirectory + "titanLanguageIdentifierLexer.txt",
        fileDirectory + "titanLanguageParser.txt",
      };
  private final String automataFilePath = fileDirectory + "titanLanguageGrammar.automata";
  private final String sourceFilePath = fileDirectory + "helloworld.titan";

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
      grammarFilePaths[2],
      grammarFilePaths[3],
      grammarFilePaths[4],
      grammarFilePaths[5],
      grammarFilePaths[6],
      grammarFilePaths[7],
      grammarFilePaths[8],
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
      "-graphicalViewOfAst","utf-8"
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
      grammarFilePaths[2],
      grammarFilePaths[3],
      grammarFilePaths[4],
      grammarFilePaths[5],
      grammarFilePaths[6],
      grammarFilePaths[7],
      grammarFilePaths[8],
      "-sourceFilePath",
      sourceFilePath,
      "-graphicalViewOfAst",
      "utf-8"
    };

    runCommandLineAstApplication(testArgs);
  }
}