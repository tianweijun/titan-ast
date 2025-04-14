package titan.ast.grammar.syntax;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;

/**
 * 生成非终结符的dfa.
 *
 * @author tian wei jun
 */
public class NonterminalDfaBuilder {

  public SyntaxDfa buildAstDfa() {
    buildProductionRuleReducingDfas();
    buildProductionRuleNfas();
    return buildAstDfaByNonterminals();
  }

  private void buildProductionRuleReducingDfas() {
    ProductionRuleReducingDfaBuilder productionRuleReducingDfaBuilder =
        new ProductionRuleReducingDfaBuilder();
    SyntaxDfaOptimizer syntaxDfaOptimizer = new SyntaxDfaOptimizer();
    for (LinkedList<ProductionRule> productionRules :
        AstContext.get().nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        SyntaxDfa reducingDfa = productionRuleReducingDfaBuilder.build(productionRule);
        productionRule.reducingDfa = syntaxDfaOptimizer.optimize(reducingDfa);
      }
    }
  }

  private SyntaxDfa buildAstDfaByNonterminals() {
    SyntaxNfa nfa = getAugmentedNonterminalNfa();
    NonterminalNfa2DfaConverter nfa2DfaConverter = new NonterminalNfa2DfaConverter();
    SyntaxDfa dfa = nfa2DfaConverter.convert(nfa);
    dfa = new SyntaxDfaOptimizer().optimize(dfa);
    return dfa;
  }

  private SyntaxNfa getAugmentedNonterminalNfa() {
    AstContext astContext = AstContext.get();
    LinkedHashMap<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap =
        astContext.nonterminalProductionRulesMap;
    Grammar augmentedNonterminal = astContext.languageGrammar.augmentedNonterminal;
    return nonterminalProductionRulesMap.get(augmentedNonterminal).getFirst().rule.syntaxNfa;
  }

  private void buildProductionRuleNfas() {
    LanguageGrammar languageGrammar = AstContext.get().languageGrammar;
    NonterminalNfaBuilder nonterminalNfaBuilder =
        new NonterminalNfaBuilder(languageGrammar.terminals, languageGrammar.nonterminals);
    nonterminalNfaBuilder.productionRuleToNfa();
  }
}
