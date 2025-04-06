package titan.ast.grammar.syntax;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import titan.ast.AstContext;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.regexp.RegExp;
import titan.ast.grammar.regexp.RegExp.MatchingPattern;
import titan.ast.grammar.regexp.RegExp.RegExpCharSet;
import titan.ast.grammar.regexp.RegExp.RegExpCharSetType;
import titan.ast.grammar.regexp.RegExp.RegExpType;
import titan.ast.grammar.regexp.RelationshipQualifier;
import titan.ast.grammar.token.DerivedTerminalGrammarAutomataData;
import titan.ast.grammar.token.DerivedTerminalGrammarAutomataData.RootTerminalGrammarMap;

/**
 * 根据终结符正则构造产生式，并将产生式中的[]、''的基本正则转为token终结符正则.
 *
 * @author tian wei jun
 */
public class ProductionRuleBuilder {

  private final LinkedHashMap<String, Grammar> nonterminals;
  private LinkedHashMap<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap;
  private Map<RegExp, Grammar> sequenceCharsUnitRegExpTerminalMap;
  private Grammar nonterminal;

  /**
   * 构造方法.
   *
   * @param nonterminals 非终结符
   */
  public ProductionRuleBuilder(LinkedHashMap<String, Grammar> nonterminals) {
    this.nonterminals = nonterminals;
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
    initSequenceCharsUnitRegExpTerminalMap();
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
          case SEQUENCE_CHARS:
            sequenceCharsRegExp2TerminalGrammarUnitRegExp(productionRuleRegExp);
            break;
          case GRAMMAR:
          case EMPTY:
            break;
          case ONE_CHAR_OPTION_CHARSET:
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
   * @param sequenceCharsRegExp UNIT&&SEQUENCE_CHARS
   */
  private void sequenceCharsRegExp2TerminalGrammarUnitRegExp(RegExp sequenceCharsRegExp) {
    RegExp eleGrammarRegExp = cloneSequenceCharsRegExpForTerminalsMap(sequenceCharsRegExp);
    Grammar terminal = sequenceCharsUnitRegExpTerminalMap.get(eleGrammarRegExp);

    if (null == terminal) {
      throw new AstRuntimeException(
          String.format(
              "error in %s %s,no terminal match",
              this.nonterminal.name,
              new String(
                  sequenceCharsRegExp.text,
                  sequenceCharsRegExp.startOfText,
                  sequenceCharsRegExp.lengthOfText)));
    }
    // sets
    RegExp.RegExpCharSet grammarCharSet = new RegExp.RegExpCharSet();
    grammarCharSet.type = RegExp.RegExpCharSetType.GRAMMAR;
    grammarCharSet.grammar = terminal;
    sequenceCharsRegExp.sets.clear();
    sequenceCharsRegExp.sets.add(grammarCharSet);

    // unitType
    sequenceCharsRegExp.unitType = RegExp.RegExpUnitType.GRAMMAR;
  }

  private RegExp cloneSequenceCharsRegExpForTerminalsMap(RegExp sequenceCharsRegExp) {
    if (sequenceCharsRegExp.type == RegExp.RegExpType.UNIT
        && sequenceCharsRegExp.unitType == RegExp.RegExpUnitType.SEQUENCE_CHARS) {
      // 复制的字段和RegExp的equals字段一样
      RegExp cloner = new RegExp();
      cloner.type = sequenceCharsRegExp.type;
      cloner.isNot = sequenceCharsRegExp.isNot;
      cloner.repMinTimes.setTimes(1);
      cloner.repMaxTimes.setTimes(1);
      cloner.matchingPattern = sequenceCharsRegExp.matchingPattern;
      // children肯定没有，那children、relationshipOfChildren都是默认值，不用复制
      cloner.unitType = sequenceCharsRegExp.unitType;
      // sets
      for (RegExp.RegExpCharSet regExpCharSet : sequenceCharsRegExp.sets) {
        cloner.sets.add(regExpCharSet.clone());
      }
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
    for (RegExp child : regExp.children) {
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

  private void initSequenceCharsUnitRegExpTerminalMap() {
    AstContext astContext = AstContext.get();
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    sequenceCharsUnitRegExpTerminalMap = new LinkedHashMap<>(languageGrammar.terminals.size());
    buildSequenceCharsUnitRegExpTerminalMapByTerminals(languageGrammar);
    buildSequenceCharsUnitRegExpTerminalMapByDerivedTerminalGrammars(languageGrammar);
  }

  private void buildSequenceCharsUnitRegExpTerminalMapByTerminals(LanguageGrammar languageGrammar) {
    LinkedHashMap<String, Grammar> terminals = languageGrammar.terminals;
    // 普通的串字符
    for (Grammar terminal : terminals.values()) {
      RegExp terminalCompositeRegExp = terminal.regExp;
      if (terminalCompositeRegExp.children.size() == 1) {
        RegExp unitRegExp = terminalCompositeRegExp.children.getFirst();
        if (unitRegExp.type == RegExp.RegExpType.UNIT
            && unitRegExp.unitType == RegExp.RegExpUnitType.SEQUENCE_CHARS) {
          if (unitRegExp.repMinTimes.isNumberTimesAndEqual(1)
              && unitRegExp.repMaxTimes.isNumberTimesAndEqual(1)) {
            sequenceCharsUnitRegExpTerminalMap.put(unitRegExp, terminal);
          }
        }
      }
    }
  }

  /**
   * derivedTerminalGrammars,虽然已经处于terminals了， 正则为空，text中的正则可能是或关系，
   * buildSequenceCharsUnitRegExpTerminalMapByTerminals方法无法处理derivedTerminalGrammars, 特此特殊处理
   * derivedTerminalGrammars.
   *
   * @param languageGrammar languageGrammar
   */
  public void buildSequenceCharsUnitRegExpTerminalMapByDerivedTerminalGrammars(
      LanguageGrammar languageGrammar) {
    DerivedTerminalGrammarAutomataData derivedTerminalGrammarAutomataData =
        languageGrammar.derivedTerminalGrammarAutomataDetail.derivedTerminalGrammarAutomataData;
    if (derivedTerminalGrammarAutomataData.isEmpty()) {
      return;
    }
    for (RootTerminalGrammarMap rootTerminalGrammarMap :
        derivedTerminalGrammarAutomataData.rootTerminalGrammarMaps) {
      for (Entry<String, Grammar> entry : rootTerminalGrammarMap.textTerminalMap.entrySet()) {
        RegExp unitRegExp = new RegExp(null);
        unitRegExp.type = RegExpType.UNIT;
        unitRegExp.isNot = false;
        unitRegExp.repMinTimes.setTimes(1);
        unitRegExp.repMaxTimes.setTimes(1);
        unitRegExp.matchingPattern = MatchingPattern.UNBACKTRACKING_GREEDINESS;
        unitRegExp.children.clear();
        unitRegExp.relationshipOfChildren = RelationshipQualifier.AND;
        unitRegExp.unitType = RegExp.RegExpUnitType.SEQUENCE_CHARS;
        RegExpCharSet regExpCharSet = new RegExpCharSet();
        regExpCharSet.type = RegExpCharSetType.SEQUENCE_CHARS;
        String textOfSequenceChars = entry.getKey();
        regExpCharSet.chars = textOfSequenceChars.toCharArray();
        unitRegExp.sets.add(regExpCharSet);
        Grammar derivedTerminalGrammar = entry.getValue();
        sequenceCharsUnitRegExpTerminalMap.put(unitRegExp, derivedTerminalGrammar);
      }
    }
  }
}
