package titan.ast.grammar.syntax;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;

/**
 * .
 *
 * @author tian wei jun
 */
public class ProductionRuleSyntaxNfaReducingBranchLinker {
  Grammar epsilon;
  Map<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap;

  public ProductionRuleSyntaxNfaReducingBranchLinker(
      Map<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap) {
    this.epsilon = AstContext.get().languageGrammar.epsilon;
    this.nonterminalProductionRulesMap = nonterminalProductionRulesMap;
  }

  public void link() {
    int oldCountStates = -1;
    int newCountStates = 0;
    while (newCountStates > oldCountStates) {
      oldCountStates = newCountStates;
      newCountStates = doLink();
    }
  }

  private int doLink() {
    int countStates = 0;
    for (LinkedList<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        SyntaxNfa syntaxNfa = productionRule.rule.syntaxNfa;
        doLink(syntaxNfa.getStates());
        countStates += syntaxNfa.getStates().size();
      }
    }
    return countStates;
  }

  private void doLink(Set<SyntaxNfaState> froms) {
    for (SyntaxNfaState from : froms) {
      Set<SyntaxNfaState> reducingBranchStartStates = new HashSet<>();
      for (Grammar inputSymbol : from.edges.keySet()) {
        addReducingBranchStartStates(reducingBranchStartStates, inputSymbol);
      }
      from.addEdge(epsilon, reducingBranchStartStates);
    }
  }

  private void addReducingBranchStartStates(
      Set<SyntaxNfaState> reducingBranchStartStates, Grammar inputSymbol) {
    if (inputSymbol.isNonterminal()) {
      LinkedList<ProductionRule> productionRules = nonterminalProductionRulesMap.get(inputSymbol);
      for (ProductionRule productionRule : productionRules) {
        SyntaxNfaState reducingBranchStartState = productionRule.rule.syntaxNfa.start;
        reducingBranchStartStates.add(reducingBranchStartState);
      }
    }
  }
}
