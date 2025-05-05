package titan.ast.runtime;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author june
 */
public class RuntimeAutomataRichAstApplicationTest {

  public RuntimeAutomataRichAstApplicationTest() {}

  @BeforeClass
  public static void setUpClass() {}

  @AfterClass
  public static void tearDownClass() {}

  @Before
  public void setUp() {}

  @After
  public void tearDown() {}

  @Test
  public void testShowAstByRuntimeRichAstApplication() {
    System.out.println("testShowAstByRuntimeRichAstApplication");

    String automataFilePath =
        "D:\\github-pro\\titan\\titan-ast\\runtime\\java\\titan-ast-runtime\\test-resources\\titanAstGrammar.automata";
    String sourceCodeFilePath =
        "D:\\github-pro\\titan\\titan-ast\\runtime\\java\\titan-ast-runtime\\test-resources\\titanAstGrammar.txt";

    RuntimeAutomataRichAstApplication runtimeAstApplication =
        new RuntimeAutomataRichAstApplication();

    try {
      runtimeAstApplication.setContext(automataFilePath);
    } catch (AutomataDataIoException e) {
      System.out.println(e.getMessage());
      return;
    }
    RichAstGeneratorResult astGeneratorResult =
        runtimeAstApplication.buildRichAst(sourceCodeFilePath);
    if (astGeneratorResult.isOk()) {
      runtimeAstApplication.displayGraphicalViewOfAst(astGeneratorResult.getOkAst());
    } else {
      System.out.println(astGeneratorResult.getErrorMsg());
    }
    
  }
}