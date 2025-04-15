package titan.ast.test.ast.diy;

import titan.ast.AstContext;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.impl.ast.AstWayGrammarFileAutomataAstApplication;
import titan.ast.output.FaGraphGuiOutputer;

public class diyShowFaByGrammarFileTest {

  public static void main(String[] args) {
    String grammarFilePath = "D://github-pro/titan/titan-ast/test/c/C.grammar";

    AstWayGrammarFileAutomataAstApplication astWayGrammarFileAutomataAstApplication =
        new AstWayGrammarFileAutomataAstApplication(
        grammarFilePath);

    AstContext astContext = AstContext.get();
    TerminalGrammar terminalGrammar = astContext.languageGrammar.terminals.get("Identifier");
    new FaGraphGuiOutputer().outputTokenNfa(terminalGrammar.tokenNfa);
  }
}
