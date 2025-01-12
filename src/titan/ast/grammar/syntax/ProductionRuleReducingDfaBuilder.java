package titan.ast.grammar.syntax;

import titan.ast.grammar.FaStateType;
import titan.ast.grammar.regexp.RegExp;

/**
 * .
 *
 * @author tian wei jun
 */
public class ProductionRuleReducingDfaBuilder {
  RegExp2SyntaxNfaConverter regExp2SyntaxNfaConverter;
  private final NonterminalNfa2DfaConverter nfa2DfaConverter;

  public ProductionRuleReducingDfaBuilder() {
    regExp2SyntaxNfaConverter = new RegExp2SyntaxNfaConverter();
    nfa2DfaConverter = new NonterminalNfa2DfaConverter();
  }

  public SyntaxDfa build(ProductionRule productionRule) {
    RegExp regExp = productionRule.rule;
    RegExp reducingRegExp = regExp2ReducingRegExp(regExp);

    SyntaxNfa syntaxNfa = new SyntaxNfa(productionRule);
    syntaxNfa.start.type = FaStateType.OPENING_TAG.getValue();
    syntaxNfa.end.type = FaStateType.CLOSING_TAG.getValue();
    reducingRegExp.syntaxNfa = syntaxNfa;
    SyntaxNfa reducingNfa = regExp2SyntaxNfaConverter.convert(reducingRegExp, productionRule);

    return nfa2DfaConverter.convert(reducingNfa);
  }

  /**
   * 倒序克隆 .
   *
   * @param regExp 已经是完整构建好的,不依赖regExp.text,同时单元正则的unitType只能是GRAMMAR
   * @return reducingRegExp
   */
  private RegExp regExp2ReducingRegExp(RegExp regExp) {
    return reducingCloneRegExp(regExp, null);
  }

  private RegExp reducingCloneRegExp(RegExp baseRegExp, RegExp parent) {
    RegExp reducingRegExp = new RegExp();
    reducingRegExp.parent = parent;
    reducingRegExp.type = baseRegExp.type;
    reducingRegExp.isNot = baseRegExp.isNot;
    reducingRegExp.repMinTimes.setTimes(baseRegExp.repMinTimes);
    reducingRegExp.repMaxTimes.setTimes(baseRegExp.repMaxTimes);
    reducingRegExp.matchingPattern = baseRegExp.matchingPattern;
    for (RegExp childOfBaseRegExp : baseRegExp.children) { // parent的child是正序的
      reducingCloneRegExp(childOfBaseRegExp, reducingRegExp);
    }
    if (null != parent) {
      // parent的child是正序的，要实现倒序克隆的话，将孩子添加到最前面即是逆序
      parent.children.addFirst(reducingRegExp);
    }
    reducingRegExp.relationshipOfChildren = baseRegExp.relationshipOfChildren;
    reducingRegExp.unitType = baseRegExp.unitType;
    reducingRegExp.sets.addAll(baseRegExp.sets);
    return reducingRegExp;
  }
}
