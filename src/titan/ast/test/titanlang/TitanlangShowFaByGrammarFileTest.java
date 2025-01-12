package titan.ast.test.titanlang;

import java.util.ArrayList;
import java.util.List;
import titan.ast.AstContext;
import titan.ast.grammar.GrammarFileAutomataAstApplication;
import titan.ast.output.FaGraphGuiOutputer;

public class TitanlangShowFaByGrammarFileTest {

  public static void main(String[] args) {
    String lexerGrammarFile =
        "D://github-pro/titan/titan-ast/test/titanlang/titanLanguageLexer.txt";
    String parserGrammarFile =
        "D://github-pro/titan/titan-ast/test/titanlang/titanLanguageParser.txt";
    List<String> grammarFilePaths = new ArrayList<>(2);
    grammarFilePaths.add(lexerGrammarFile);
    grammarFilePaths.add(parserGrammarFile);

    GrammarFileAutomataAstApplication grammarFileAutomataAstApplication =
        new GrammarFileAutomataAstApplication();
    grammarFileAutomataAstApplication.setAstAutomataContext(grammarFilePaths);

    AstContext astContext = AstContext.get();
    new FaGraphGuiOutputer().outputSyntaxDfa(astContext.languageGrammar.astDfa);
  }
}
