package titan.ast.fa.syntax;

import java.util.List;
import java.util.Map;
import java.util.Set;
import titan.ast.AstContext;
import titan.ast.fa.FaStateType;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.NonterminalGrammar;

/**
 * ProductionRule转为nfa.
 *
 * @author tian wei jun
 */
public class ProductionRuleNfaBuilder {
  final Grammar epsilon;
  private final Map<NonterminalGrammar, List<ProductionRule>> nonterminalProductionRulesMap;
  RegExp2SyntaxNfaConverter regExp2SyntaxNfaConverter;

  public ProductionRuleNfaBuilder() {
    this.nonterminalProductionRulesMap = AstContext.get().nonterminalProductionRulesMap;
    regExp2SyntaxNfaConverter = new RegExp2SyntaxNfaConverter();
    this.epsilon = AstContext.get().languageGrammar.epsilon;
  }

  public void build() {
    // build all
    for (List<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        SyntaxNfa nfa = regExp2SyntaxNfaConverter.convert(productionRule.rule, productionRule);
        nfa.end.type = FaStateType.appendClosingTag(nfa.end.type);
        productionRule.nfa = nfa;
      }
    }
    // link all,for refucing to nonterminal and shifting it
    linkNonterminalInputGrammarEndge();
  }

  private void linkNonterminalInputGrammarEndge() {
    for (List<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        fromOfNonterminalInputGrammarEndgeToItStart(productionRule.nfa.getStates());
      }
    }
  }

  private void fromOfNonterminalInputGrammarEndgeToItStart(Set<SyntaxNfaState> states) {
    for (SyntaxNfaState state : states) {
      for (Grammar inputSymbol : state.edges.keySet()) {
        if (inputSymbol instanceof NonterminalGrammar nonterminalGrammar) {
          List<ProductionRule> productionRules = nonterminalProductionRulesMap.get(nonterminalGrammar);
          for (ProductionRule productionRule : productionRules) {
            state.addEdge(epsilon,productionRule.nfa.start);
          }
        }
      }
    }
  }
}
