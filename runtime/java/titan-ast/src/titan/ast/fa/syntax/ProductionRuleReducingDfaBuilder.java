package titan.ast.fa.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import titan.ast.AstContext;
import titan.ast.fa.FaStateType;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.grammar.regexp.AndCompositeRegExp;
import titan.ast.grammar.regexp.GrammarRegExp;
import titan.ast.grammar.regexp.OrCompositeRegExp;
import titan.ast.grammar.regexp.ParenthesisRegExp;
import titan.ast.grammar.regexp.UnitRegExp;

/**
 * .
 *
 * @author tian wei jun
 */
public class ProductionRuleReducingDfaBuilder {

  private final Nfa2SyntaxDfaConverter nfa2DfaConverter;
  RegExp2SyntaxNfaConverter regExp2SyntaxNfaConverter;

  public ProductionRuleReducingDfaBuilder() {
    regExp2SyntaxNfaConverter = new RegExp2SyntaxNfaConverter();
    nfa2DfaConverter = new Nfa2SyntaxDfaConverter();
  }

  public static void build() {
    ProductionRuleReducingDfaBuilder productionRuleReducingDfaBuilder =
        new ProductionRuleReducingDfaBuilder();
    SyntaxDfaOptimizer syntaxDfaOptimizer = new SyntaxDfaOptimizer();
    for (List<ProductionRule> productionRules :
        AstContext.get().nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        SyntaxDfa reducingDfa = productionRuleReducingDfaBuilder.build(productionRule);
        productionRule.reducingDfa = syntaxDfaOptimizer.optimize(reducingDfa);
      }
    }
  }

  public SyntaxDfa build(ProductionRule productionRule) {
    AndCompositeRegExp andCompositeRegExp = productionRule.rule;
    AndCompositeRegExp reducingRegExp = regExp2ReducingRegExp(andCompositeRegExp);

    SyntaxNfa reducingNfa = regExp2SyntaxNfaConverter.convert(reducingRegExp, productionRule);
    reducingNfa.end.type = FaStateType.appendClosingTag(reducingNfa.end.type);
    return nfa2DfaConverter.convert(reducingNfa);
  }

  /**
   * 倒序克隆 .
   *
   * @param andCompositeRegExp 已经是完整构建好的,不依赖regExp.text,同时单元正则的unitType只能是GRAMMAR
   * @return reducingRegExp
   */
  private AndCompositeRegExp regExp2ReducingRegExp(AndCompositeRegExp andCompositeRegExp) {
    return reducingCloneAndCompositeRegExp(andCompositeRegExp);
  }

  private AndCompositeRegExp reducingCloneAndCompositeRegExp(AndCompositeRegExp andCompositeRegExp) {
    AndCompositeRegExp reducingRegExp = new AndCompositeRegExp();
    reducingRegExp.children = new ArrayList<>(andCompositeRegExp.children.size());
    ListIterator<UnitRegExp> unitRegExpListIterator = andCompositeRegExp.children.listIterator(
        andCompositeRegExp.children.size());
    while (unitRegExpListIterator.hasPrevious()) {
      UnitRegExp unitRegExp = unitRegExpListIterator.previous();
      switch (unitRegExp.type) {
        case PARENTHESIS -> {
          reducingRegExp.children.add(reducingCloneParenthesisRegExp((ParenthesisRegExp) unitRegExp));
        }
        case GRAMMAR -> {
          reducingRegExp.children.add(reducingCloneGrammarRegExp((GrammarRegExp) unitRegExp));
        }
      }
    }
    reducingRegExp.setRepeatTimes(andCompositeRegExp.repMinTimes, andCompositeRegExp.repMaxTimes);
    return reducingRegExp;
  }

  private ParenthesisRegExp reducingCloneParenthesisRegExp(ParenthesisRegExp parenthesisRegExp) {
    ParenthesisRegExp reducingRegExp = new ParenthesisRegExp(
        reducingCloneOrCompositeRegExp(parenthesisRegExp.orCompositeRegExp));
    reducingRegExp.setRepeatTimes(parenthesisRegExp.repMinTimes, parenthesisRegExp.repMaxTimes);
    return reducingRegExp;
  }

  private OrCompositeRegExp reducingCloneOrCompositeRegExp(OrCompositeRegExp orCompositeRegExp) {
    OrCompositeRegExp reducingRegExp = new OrCompositeRegExp();
    reducingRegExp.children = new ArrayList<>(orCompositeRegExp.children.size());
    ListIterator<AndCompositeRegExp> andCompositeRegExpIt = orCompositeRegExp.children.listIterator(
        orCompositeRegExp.children.size());
    while (andCompositeRegExpIt.hasPrevious()) {
      AndCompositeRegExp andCompositeRegExp = andCompositeRegExpIt.previous();
      reducingRegExp.children.add(reducingCloneAndCompositeRegExp(andCompositeRegExp));
    }
    reducingRegExp.setRepeatTimes(orCompositeRegExp.repMinTimes, orCompositeRegExp.repMaxTimes);
    return reducingRegExp;
  }

  private GrammarRegExp reducingCloneGrammarRegExp(GrammarRegExp grammarRegExp) {
    GrammarRegExp reducingRegExp = new GrammarRegExp(grammarRegExp.grammarName);
    reducingRegExp.grammar = grammarRegExp.grammar;
    reducingRegExp.setRepeatTimes(grammarRegExp.repMinTimes, grammarRegExp.repMaxTimes);
    return reducingRegExp;
  }
}
