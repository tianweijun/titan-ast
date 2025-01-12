package titan.ast.grammar.syntax;

import java.util.LinkedList;
import java.util.Map;
import titan.ast.grammar.FaStateType;
import titan.ast.grammar.Grammar;

/**
 * ProductionRule转为nfa.
 *
 * @author tian wei jun
 */
public class NonterminalProductionRuleNfaBuilder {

  RegExp2SyntaxNfaConverter regExp2SyntaxNfaConverter;
  private final Map<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap;

  NonterminalProductionRuleNfaBuilder(
      Map<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap) {
    this.nonterminalProductionRulesMap = nonterminalProductionRulesMap;
    regExp2SyntaxNfaConverter = new RegExp2SyntaxNfaConverter();
  }

  public void build() {
    createAllProductionRuleSyntaxNfa();
    buildAllProductionRuleSyntaxNfa();
    linkToReducingBranch();
  }

  private void linkToReducingBranch() {
    ProductionRuleSyntaxNfaReducingBranchLinker reducingBranchLinker =
        new ProductionRuleSyntaxNfaReducingBranchLinker(nonterminalProductionRulesMap);
    reducingBranchLinker.link();
  }

  private void createAllProductionRuleSyntaxNfa() {
    for (LinkedList<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        SyntaxNfa productionRuleSyntaxNfa = new SyntaxNfa(productionRule);
        productionRuleSyntaxNfa.start.type = FaStateType.OPENING_TAG.getValue();
        productionRuleSyntaxNfa.end.type = FaStateType.CLOSING_TAG.getValue();

        productionRule.rule.syntaxNfa = productionRuleSyntaxNfa;
      }
    }
  }

  private void buildAllProductionRuleSyntaxNfa() {
    for (LinkedList<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        buildSyntaxNfaByProductionRule(productionRule);
      }
    }
  }

  /**
   * 生成并设置产生式正则的nfa.
   *
   * @param productionRule 产生式
   */
  private void buildSyntaxNfaByProductionRule(ProductionRule productionRule) {
    regExp2SyntaxNfaConverter.convert(productionRule.rule, productionRule);
  }
}
