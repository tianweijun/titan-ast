package titan.ast.test.runtime;

import titan.ast.grammar.GrammarFileAutomataAstApplication;
import titan.ast.runtime.Ast;
import titan.ast.runtime.RuntimeAutomataAstApplication;

/**
 * .
 *
 * @author tian wei jun
 */
public class RuntimeAutomataAstApplicationTest {

  public static void main(String[] args) {
    String grammarFilePath = "D://github-pro/titan/titan-ast/test/c/C.grammar";
    GrammarFileAutomataAstApplication grammarFileAutomataAstApplication =
        new GrammarFileAutomataAstApplication();
    grammarFileAutomataAstApplication.setAstAutomataContext(grammarFilePath);

    RuntimeAutomataAstApplication runtimeAutomataAstApplication =
        grammarFileAutomataAstApplication.buildRuntimeAutomataAstApplication();
    grammarFileAutomataAstApplication.clear(); // helper gc

    String sourceCodeFilePath = "D://github-pro/titan/titan-ast/test/c/helloworld.c";
    Ast ast = runtimeAutomataAstApplication.buildAst(sourceCodeFilePath);
    runtimeAutomataAstApplication.displayGraphicalViewOfAst(ast);
  }
}
