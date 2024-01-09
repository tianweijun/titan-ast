package titan.ast.grammar.syntax;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;

/**
 * 生成并设置非终结符的nfa.
 *
 * @author tian wei jun
 */
public class NonterminalNfaBuilder {

  private final LinkedHashMap<String, Grammar> terminals;
  private final LinkedHashMap<String, Grammar> nonterminals;

  public NonterminalNfaBuilder(
      LinkedHashMap<String, Grammar> terminals, LinkedHashMap<String, Grammar> nonterminals) {
    this.terminals = terminals;
    this.nonterminals = nonterminals;
  }

  /** 1.建立正则 2.正则转产生式 3.产生式转nfa */
  public void build() {
    buildRegExp();
    buildProductionRule();
    productionRuleToNfa();
  }

  private void productionRuleToNfa() {
    Map<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap =
        AstContext.get().nonterminalProductionRulesMap;
    NonterminalProductionRuleNfaBuilder nonterminalProductionRuleNfaBuilder =
        new NonterminalProductionRuleNfaBuilder(nonterminalProductionRulesMap);
    nonterminalProductionRuleNfaBuilder.build();
  }

  private void buildProductionRule() {
    ProductionRuleBuilder productionRuleBuilder = new ProductionRuleBuilder(nonterminals);
    LinkedHashMap<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap =
        productionRuleBuilder.build();
    AstContext.get().nonterminalProductionRulesMap = nonterminalProductionRulesMap;
  }

  private void buildRegExp() {
    SyntaxRegExpBuilder syntaxRegExpBuilder = new SyntaxRegExpBuilder();
    syntaxRegExpBuilder.addTasks(nonterminals);
    syntaxRegExpBuilder.addSources(nonterminals);
    syntaxRegExpBuilder.addSources(terminals);
    syntaxRegExpBuilder.build();
  }
}
