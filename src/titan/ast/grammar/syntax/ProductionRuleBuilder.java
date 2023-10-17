package titan.ast.grammar.syntax;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.RegExp;
import titan.ast.grammar.RelationshipQualifier;
import titan.ast.runtime.AstRuntimeException;

/**
 * 根据终结符正则构造产生式，并将产生式中的[]、''的基本正则转为token非终结符正则.
 *
 * @author tian wei jun
 */
public class ProductionRuleBuilder {

  private final LinkedHashMap<String, Grammar> terminals;
  private final LinkedHashMap<String, Grammar> nonterminals;
  private LinkedHashMap<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap;
  private Map<RegExp, Grammar> unitRegExpTerminalsMap;
  private Grammar nonterminal;

  /**
   * 构造方法.
   *
   * @param terminals 终结符
   * @param nonterminals 非终结符
   */
  public ProductionRuleBuilder(
      LinkedHashMap<String, Grammar> terminals, LinkedHashMap<String, Grammar> nonterminals) {
    this.terminals = terminals;
    this.nonterminals = nonterminals;
    initRegExpTerminalsMap();
  }

  private void initRegExpTerminalsMap() {
    unitRegExpTerminalsMap = new LinkedHashMap<>(terminals.size());
    for (Grammar terminal : terminals.values()) {
      RegExp terminalCompositeRegExp = terminal.regExp;
      if (terminalCompositeRegExp.children.size() == 1) {
        RegExp unitRegExp = terminalCompositeRegExp.children.getFirst();
        if (unitRegExp.type == RegExp.RegExpType.UNIT
            && (unitRegExp.unitType == RegExp.RegExpUnitType.SEQUENCE_CHARS
                || unitRegExp.unitType == RegExp.RegExpUnitType.ONE_CHAR_OPTION_CHARSET)) {
          if (unitRegExp.repMinTimes.isNumberTimesAndEqual(1)
              && unitRegExp.repMaxTimes.isNumberTimesAndEqual(1)) {
            unitRegExpTerminalsMap.put(unitRegExp, terminal);
          }
        }
      }
    }
  }

  /** 将所有从构造方法传递进来的非终结符生成对应的产生式. */
  public LinkedHashMap<Grammar, LinkedList<ProductionRule>> build() {
    nonterminalProductionRulesMap = new LinkedHashMap<>(nonterminals.size());
    for (Grammar nonterminal : nonterminals.values()) {
      build(nonterminal);
    }
    return nonterminalProductionRulesMap;
  }

  /** 1.根据正则生成产生式 2.处理产生式别名问题 3.所有正则替换为终结符和非终结符（复合正则、grammar引用的单元正则） */
  private void build(Grammar nonterminal) {
    this.nonterminal = nonterminal;
    // 1.根据正则生成产生式
    createProductionRule(nonterminal);
    // 2.处理产生式别名问题
    setAliasForProductionRule();
    // 3.所有正则替换为终结符和非终结符（复合正则、grammar引用的单元正则）
    formatRegExpForProductionRule();
  }

  /** 所有正则替换为终结符和非终结符（复合正则、grammar引用的单元正则） . */
  private void formatRegExpForProductionRule() {
    for (LinkedList<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        RegExp productionRuleRegExp = productionRule.rule;
        unitRegExpToGrammarUnitRegExp(productionRuleRegExp);
      }
    }
  }

  /**
   * [UNIT&&(SEQUENCE_CHARS||ONE_CHAR_OPTION_CHARSET)]单元正则转为grammar{1,1}引用的单元正则).
   *
   * @param productionRuleRegExp [UNIT&&(SEQUENCE_CHARS||ONE_CHAR_OPTION_CHARSET)]
   */
  private void unitRegExpToGrammarUnitRegExp(RegExp productionRuleRegExp) {
    switch (productionRuleRegExp.type) {
      case COMPOSITE:
        for (RegExp child : productionRuleRegExp.children) {
          unitRegExpToGrammarUnitRegExp(child);
        }
        break;
      case NFA:
        throw new AstRuntimeException(
            String.format(
                "there are 'nfa regexp' in %s %s,no terminal match",
                nonterminal.name,
                new String(
                    productionRuleRegExp.text,
                    productionRuleRegExp.startOfText,
                    productionRuleRegExp.lengthOfText)));
      case UNIT:
        switch (productionRuleRegExp.unitType) {
          case GRAMMAR:
            break;
          case ONE_CHAR_OPTION_CHARSET:
          case SEQUENCE_CHARS:
            sequenceCharsOrOneCharOptionCharsetUnit2TerminalGrammarUnitRegExp(productionRuleRegExp);
            break;
          case EMPTY:
            break;
          case HELPER_OR:
          case HELPER_ALIAS:
            throw new AstRuntimeException(
                String.format(
                    "error in %s %s,no terminal match",
                    nonterminal.name,
                    new String(
                        productionRuleRegExp.text,
                        productionRuleRegExp.startOfText,
                        productionRuleRegExp.lengthOfText)));
          default:
        }
        break;
      default:
    }
  }

  /**
   * 设置unitType,sets.
   *
   * @param unit [UNIT&&(SEQUENCE_CHARS||ONE_CHAR_OPTION_CHARSET)]
   */
  private void sequenceCharsOrOneCharOptionCharsetUnit2TerminalGrammarUnitRegExp(RegExp unit) {
    RegExp eleGrammarRegExp = cloneSequenceCharsOrOneCharOptionCharsetUnitRegExp(unit);
    Grammar terminal = unitRegExpTerminalsMap.get(eleGrammarRegExp);

    if (null == terminal) {
      throw new AstRuntimeException(
          String.format(
              "error in %s %s,no terminal match",
              this.nonterminal.name, new String(unit.text, unit.startOfText, unit.lengthOfText)));
    }
    // sets
    RegExp.RegExpCharSet grammarCharSet = new RegExp.RegExpCharSet();
    grammarCharSet.type = RegExp.RegExpCharSetType.GRAMMAR;
    grammarCharSet.grammar = terminal;
    unit.sets.clear();
    unit.sets.add(grammarCharSet);

    // unitType
    unit.unitType = RegExp.RegExpUnitType.GRAMMAR;
  }

  private RegExp cloneSequenceCharsOrOneCharOptionCharsetUnitRegExp(RegExp unitRegExp) {
    if (unitRegExp.type == RegExp.RegExpType.UNIT
        && (unitRegExp.unitType == RegExp.RegExpUnitType.SEQUENCE_CHARS
            || unitRegExp.unitType == RegExp.RegExpUnitType.ONE_CHAR_OPTION_CHARSET)) {
      // 复制的字段和RegExp的equals字段一样
      RegExp cloner = new RegExp();
      cloner.type = unitRegExp.type;
      cloner.isNot = unitRegExp.isNot;
      cloner.repMinTimes.setTimes(1);
      cloner.repMaxTimes.setTimes(1);
      cloner.matchingPattern = unitRegExp.matchingPattern;
      // children肯定没有，那children、relationshipOfChildren都是默认值，不用复制
      cloner.unitType = unitRegExp.unitType;
      // sets
      for (RegExp.RegExpCharSet regExpCharSet : unitRegExp.sets) {
        cloner.sets.add(regExpCharSet.clone());
      }
      cloner.relationshipOfChars = unitRegExp.relationshipOfChars;
      return cloner;
    }
    return null;
  }

  /** 1.顶层alias设置别名 2.去掉所有别名 */
  private void setAliasForProductionRule() {
    for (LinkedList<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        // 1.顶层alias设置别名
        setAlias(productionRule);
        // 2.去掉所有别名
        RegExp.deleteAlias(productionRule.rule);
      }
    }
  }

  private void setAlias(ProductionRule productionRule) {
    RegExp regExp = productionRule.rule;
    Iterator<RegExp> regExpChildrenIt = regExp.children.iterator();
    while (regExpChildrenIt.hasNext()) {
      RegExp child = regExpChildrenIt.next();
      if (child.isHelperAliasRegExpUnit()) {
        productionRule.alias = new String(child.sets.getFirst().chars);
      }
    }
  }

  private void createProductionRule(Grammar nonterminal) {
    LinkedList<ProductionRule> productionRules = new LinkedList<>();

    RegExp nonterminalRegExp = nonterminal.regExp;
    if (nonterminalRegExp.relationshipOfChildren == RelationshipQualifier.AND) {
      ProductionRule productionRule = new ProductionRule();
      productionRule.grammar = nonterminal;
      productionRule.rule = nonterminalRegExp;
      productionRules.add(productionRule);
    }
    if (nonterminalRegExp.relationshipOfChildren == RelationshipQualifier.OR) {
      // 或关系的话，每个或运算符号两边的内容被合并成一个复合正则,故非终结符正则的每个孩子都是一个产生式正则
      for (RegExp childOfNonterminalRegExp : nonterminal.regExp.children) {
        ProductionRule productionRule = new ProductionRule();
        productionRule.grammar = nonterminal;
        productionRule.rule = childOfNonterminalRegExp;
        productionRules.add(productionRule);
      }
    }
    nonterminalProductionRulesMap.put(nonterminal, productionRules);
  }
}
