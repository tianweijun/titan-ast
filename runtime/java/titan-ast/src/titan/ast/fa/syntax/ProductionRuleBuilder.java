package titan.ast.fa.syntax;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import titan.ast.AstContext;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.NonterminalGrammar;
import titan.ast.grammar.PrimaryGrammarContent;
import titan.ast.grammar.PrimaryGrammarContent.RegExpPrimaryGrammarContent;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.grammar.regexp.AndCompositeRegExp;
import titan.ast.grammar.regexp.GrammarRegExp;
import titan.ast.grammar.regexp.OrCompositeRegExp;
import titan.ast.grammar.regexp.ParenthesisRegExp;
import titan.ast.grammar.regexp.SequenceCharsRegExp;
import titan.ast.grammar.regexp.UnitRegExp;

/**
 * 构造产生式[正则（仅有grammarRegExp及其复合）,产生式别名].
 *
 * @author tian wei jun
 */
public class ProductionRuleBuilder {

  private final LinkedHashMap<String, NonterminalGrammar> nonterminals;
  LinkedHashMap<String, TerminalGrammar> terminals;
  private Map<String, TerminalGrammar> sequenceCharsRegExpTerminalMap;
  private LinkedHashMap<NonterminalGrammar, List<ProductionRule>> nonterminalProductionRulesMap;
  private NonterminalGrammar nonterminal;


  public ProductionRuleBuilder() {
    AstContext astContext = AstContext.get();
    this.nonterminals = astContext.languageGrammar.nonterminals;
    this.terminals = astContext.languageGrammar.terminals;
  }

  /**
   * 将所有从构造方法传递进来的非终结符生成对应的产生式.
   */
  public void build() {
    nonterminalProductionRulesMap = new LinkedHashMap<>(nonterminals.size());
    for (NonterminalGrammar nonterminal : nonterminals.values()) {
      build(nonterminal);
    }
    AstContext.get().nonterminalProductionRulesMap = nonterminalProductionRulesMap;
  }

  /**
   * 所有正则替换为终结符和非终结符（复合正则、grammar引用的单元正则）
   */
  private void build(NonterminalGrammar nonterminal) {
    this.nonterminal = nonterminal;
    // 根据正则生成产生式
    createProductionRule(nonterminal);
    // 所有正则替换为终结符和非终结符（复合正则、grammar引用的单元正则）
    initSequenceCharsRegExpTerminalMap();
    formatRegExpForProductionRule();
  }

  private void createProductionRule(NonterminalGrammar nonterminal) {
    RegExpPrimaryGrammarContent grammarContent = (RegExpPrimaryGrammarContent) nonterminal.primaryGrammarContent;
    OrCompositeRegExp orCompositeRegExp = grammarContent.orCompositeRegExp;

    List<ProductionRule> productionRules = new ArrayList<>(orCompositeRegExp.children.size());
    for (AndCompositeRegExp andCompositeRegExp : orCompositeRegExp.children) {
      ProductionRule productionRule = new ProductionRule();
      productionRule.grammar = nonterminal;
      productionRule.rule = andCompositeRegExp;
      productionRule.alias = andCompositeRegExp.alias;
      productionRules.add(productionRule);
    }
    nonterminalProductionRulesMap.put(nonterminal, productionRules);
  }

  /**
   * 所有正则替换为终结符和非终结符（复合正则、grammar引用的单元正则） .
   */
  private void formatRegExpForProductionRule() {
    for (List<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        format2GrammarRegExp(productionRule.rule);
      }
    }
  }

  /*
   * 将SequenceCharsRegExp转为GrammarRegExp
   *
   */
  private void format2GrammarRegExp(AndCompositeRegExp andCompositeRegExp) {
    ArrayList<UnitRegExp> unitRegExps = andCompositeRegExp.children;
    for (int i = 0; i < unitRegExps.size(); i++) {
      UnitRegExp unitRegExp = unitRegExps.get(i);
      switch (unitRegExp.type) {
        case PARENTHESIS -> {
          ParenthesisRegExp parenthesisRegExp = (ParenthesisRegExp) unitRegExp;
          for (AndCompositeRegExp child : parenthesisRegExp.orCompositeRegExp.children) {
            format2GrammarRegExp(child);
          }
        }
        case GRAMMAR -> {
          GrammarRegExp grammarRegExp = (GrammarRegExp) unitRegExp;
          grammarRegExp.grammar = getGrammarForGrammarRegExp(grammarRegExp.grammarName);
        }
        case SEQUENCE_CHARS -> {
          SequenceCharsRegExp sequenceCharsRegExp = (SequenceCharsRegExp) unitRegExp;
          unitRegExps.set(i, sequenceCharsRegExp2GrammarRegExp(sequenceCharsRegExp));
        }
        case ONE_CHAR_OPTION_CHARSET -> {
          throw new AstRuntimeException(String.format("nonterminal grammar(%s) :not support [xxx]", nonterminal.name));
        }
      }
    }
  }

  private GrammarRegExp sequenceCharsRegExp2GrammarRegExp(SequenceCharsRegExp sequenceCharsRegExp) {
    String text = sequenceCharsRegExp.chars;
    TerminalGrammar terminalGrammar = sequenceCharsRegExpTerminalMap.get(text);
    if (null == terminalGrammar) {
      //todo DerivedTerminalGrammar只放在DerivedTerminalGrammarAutomataDetail里
    }
    GrammarRegExp grammarRegExp = new GrammarRegExp(terminalGrammar.name);
    grammarRegExp.grammar = terminalGrammar;
    return grammarRegExp;
  }

  private void initSequenceCharsRegExpTerminalMap() {
    sequenceCharsRegExpTerminalMap = new LinkedHashMap<>(terminals.size());
    for (TerminalGrammar terminal : terminals.values()) {
      PrimaryGrammarContent primaryGrammarContent = terminal.primaryGrammarContent;
      if (primaryGrammarContent instanceof RegExpPrimaryGrammarContent regExpPrimaryGrammarContent) {
        OrCompositeRegExp orCompositeRegExp = regExpPrimaryGrammarContent.orCompositeRegExp;
        if (orCompositeRegExp.children.size() == 1) {
          AndCompositeRegExp andCompositeRegExp = orCompositeRegExp.children.get(0);
          if (andCompositeRegExp.children.size() == 1) {
            UnitRegExp unitRegExp = andCompositeRegExp.children.get(0);
            if (unitRegExp instanceof SequenceCharsRegExp sequenceCharsRegExp
                && sequenceCharsRegExp.repMinTimes.isNumberTimesAndEqual(1) &&
                sequenceCharsRegExp.repMaxTimes.isNumberTimesAndEqual(1)) {
              sequenceCharsRegExpTerminalMap.put(sequenceCharsRegExp.chars, terminal);
            }
          }
        }
      }
    }
  }

  private Grammar getGrammarForGrammarRegExp(String grammarName) {
    Grammar grammar = nonterminals.get(grammarName);
    if (null == grammar) {
      grammar = terminals.get(grammarName);
    }
    return grammar;
  }
}
