package titan.ast.test.diy;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.GrammarFileAutomataAstApplication;
import titan.ast.grammar.syntax.ProductionRule;
import titan.ast.grammar.syntax.SyntaxDfa;
import titan.ast.output.FaGraphGuiOutputer;

public class DiyShowFaByGrammarFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/diy/diy.grammar",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/diy/diy.txt",
      // "-graphicalViewOfAst"
    };

    GrammarFileAutomataAstApplication grammarFileAutomataAstApplication =
        new GrammarFileAutomataAstApplication();
    grammarFileAutomataAstApplication.setAstAutomataContext(testArgs[1]);

    AstContext astContext = AstContext.get();

    LinkedHashMap<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap =
        astContext.nonterminalProductionRulesMap;
    for (Map.Entry<Grammar, LinkedList<ProductionRule>> entry :
        nonterminalProductionRulesMap.entrySet()) {
      Grammar grammar = entry.getKey();
      LinkedList<ProductionRule> productionRules = entry.getValue();
      for (ProductionRule productionRule : productionRules) {
        if (grammar.name.equals("diy")) {
          new FaGraphGuiOutputer().outputSyntaxDfa(productionRule.reducingDfa);
        }
      }
    }

    SyntaxDfa astDfa = astContext.languageGrammar.astDfa;
    // new FaGraphGuiOutputer().outputSyntaxDfa(astDfa);
  }
}
