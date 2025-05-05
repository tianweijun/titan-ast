package titan.ast.astwaytest.visitor;

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
      "D://github-pro/titan/titan-ast/test-resources/ast/titanAstGrammar.txt";
  private final String astVisitorFileDirectory = "C:\\Users\\june\\Desktop\\s\\";
  private final String astVisitorFilePackage = "titan.ast.impl.ast.contextast";

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
  public void testBuildingAstVisitor() {
    System.out.println("testBuildingAstVisitor");
    String[] testArgs = {
      "-grammarFilePaths",
      grammarFilePath,
      "-astVisitorFileDirectory",
      astVisitorFileDirectory,
      astVisitorFilePackage
    };
    runCommandLineAstApplication(testArgs);
  }
}