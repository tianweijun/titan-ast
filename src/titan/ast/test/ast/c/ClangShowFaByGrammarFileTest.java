package titan.ast.test.ast.c;

import titan.ast.AstContext;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.impl.ast.AstWayGrammarAutomataAstApplication;
import titan.ast.output.FaGraphGuiOutputer;

public class ClangShowFaByGrammarFileTest {

  public static void main(String[] args) {
    String grammarFilePath = "D://github-pro/titan/titan-ast/test/c/C.grammar";

    AstWayGrammarAutomataAstApplication astWayGrammarFileAutomataAstApplication =
        new AstWayGrammarAutomataAstApplication(
        grammarFilePath);

    AstContext astContext = AstContext.get();
    TerminalGrammar terminalGrammar = astContext.languageGrammar.terminals.get("Identifier");
    new FaGraphGuiOutputer().outputTokenNfa(terminalGrammar.tokenNfa);
  }
}
