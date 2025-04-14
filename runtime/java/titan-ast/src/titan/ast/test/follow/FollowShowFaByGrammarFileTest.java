package titan.ast.test.follow;

import java.util.ArrayList;
import java.util.List;
import titan.ast.AstContext;
import titan.ast.grammar.GrammarFileAutomataAstApplication;
import titan.ast.output.FaGraphGuiOutputer;

public class FollowShowFaByGrammarFileTest {

  public static void main(String[] args) {
    String parserGrammarFile = "D://github-pro/titan/titan-ast/test/follow/follow.grammar";
    List<String> grammarFilePaths = new ArrayList<>(2);
    grammarFilePaths.add(parserGrammarFile);

    GrammarFileAutomataAstApplication grammarFileAutomataAstApplication =
        new GrammarFileAutomataAstApplication();
    grammarFileAutomataAstApplication.setAstAutomataContext(grammarFilePaths);

    AstContext astContext = AstContext.get();
    new FaGraphGuiOutputer().outputSyntaxDfa(astContext.languageGrammar.astDfa);
  }
}
