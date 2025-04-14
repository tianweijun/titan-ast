package titan.ast.test.c;

import titan.ast.AstContext;
import titan.ast.grammar.GrammarFileAutomataAstApplication;
import titan.ast.output.FaGraphGuiOutputer;

public class ClangShowFaByGrammarFileTest {

  public static void main(String[] args) {
    String grammarFilePath = "D://github-pro/titan/titan-ast/test/c/C.grammar";

    GrammarFileAutomataAstApplication grammarFileAutomataAstApplication =
        new GrammarFileAutomataAstApplication();
    grammarFileAutomataAstApplication.setAstAutomataContext(grammarFilePath);

    AstContext astContext = AstContext.get();
    new FaGraphGuiOutputer().outputTokenDfa(astContext.languageGrammar.tokenDfa);
  }
}
