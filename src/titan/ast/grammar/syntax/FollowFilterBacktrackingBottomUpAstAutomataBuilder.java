package titan.ast.grammar.syntax;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.RegExp;
import titan.ast.grammar.RegExp.RegExpType;
import titan.ast.grammar.RegExp.RegExpUnitType;

/**
 * .
 *
 * @author tian wei jun
 */
public class FollowFilterBacktrackingBottomUpAstAutomataBuilder {
  LanguageGrammar languageGrammar;
  Grammar epsilon;
  LinkedHashMap<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap;

  HashSet<Grammar> emptyNonterminal;
  HashMap<ProductionRule, SyntaxDfa> productionRuleSyntaxDfaMap;
  HashMap<ProductionRule, Set<Grammar>> productionRuleFirstNonterminalMap;
  HashMap<ProductionRule, Set<Grammar>> productionRuleLastNonterminalMap;

  Map<Grammar, Set<Grammar>> nonterminalFirstwMap;
  Map<Grammar, Set<Grammar>> nonterminalFollowMap;

  /**
   * 构造函数.
   *
   * @param languageGrammar 语法所衍生的信息
   * @param nonterminalProductionRulesMap 产生式的nfa完全建立好（已链接）
   */
  public FollowFilterBacktrackingBottomUpAstAutomataBuilder(
      LanguageGrammar languageGrammar,
      LinkedHashMap<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap) {
    this.languageGrammar = languageGrammar;
    epsilon = languageGrammar.epsilon;
    this.nonterminalProductionRulesMap = nonterminalProductionRulesMap;
  }

  public FollowFilterBacktrackingBottomUpAstAutomata build() {
    setEmptyNonterminal();
    // setProductionRuleSyntaxDfaMap();
    setNonterminalFirstMap();
    setNonterminalFollowMap();
    return new FollowFilterBacktrackingBottomUpAstAutomata(
        languageGrammar.astDfa, languageGrammar.getStartGrammar(), nonterminalFollowMap);
  }

  private void setEmptyNonterminal() {
    emptyNonterminal = new HashSet<>();
    for (LinkedList<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        RegExp regExp = productionRule.rule;
        LinkedList<RegExp> children = regExp.children;
        if (children.size() == 1 && children.getFirst().isEmpty()) {
          emptyNonterminal.add(productionRule.grammar);
        }
      }
    }
  }

  private void setProductionRuleSyntaxDfaMap() {
    int sizeOfProductionRule = 0;
    for (LinkedList<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      sizeOfProductionRule += productionRules.size();
    }
    productionRuleSyntaxDfaMap = new HashMap<>(sizeOfProductionRule);

    NonterminalNfa2DfaConverter nfa2DfaConverter = new NonterminalNfa2DfaConverter();
    for (LinkedList<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        SyntaxNfa syntaxNfa = productionRule.rule.syntaxNfa;
        SyntaxDfa syntaxDfa = nfa2DfaConverter.convert(syntaxNfa);
        productionRuleSyntaxDfaMap.put(productionRule, syntaxDfa);
      }
    }
  }

  private void setNonterminalFirstMap() {
    // init map
    Collection<Grammar> nonterminals = languageGrammar.nonterminals.values();
    nonterminalFirstwMap = new HashMap<>(nonterminals.size());
    for (Grammar nonterminal : nonterminals) {
      nonterminalFirstwMap.put(nonterminal, new HashSet<>());
    }
    while (addFirst()) {}
  }

  // first(X1X2X3X4X5...Xn) = first(X1)+(first(X2)[ε∈X1])
  // +(first(X3)[ε∈X2]) + ... + (first(Xn)[ε∈Xn-1])
  private boolean addFirst2() {
    boolean modified = false;
    for (Entry<ProductionRule, SyntaxDfa> entry : productionRuleSyntaxDfaMap.entrySet()) {
      ProductionRule productionRule = entry.getKey();
      Grammar nonterminal = productionRule.grammar;
      Set<Grammar> nonterminalProductionRuleFirst = nonterminalFirstwMap.get(nonterminal);

      SyntaxDfa syntaxDfa = entry.getValue();

      for (Grammar first : syntaxDfa.start.edges.keySet()) {
        if (first.isTerminal()) { // terminal
          modified = modified || nonterminalProductionRuleFirst.add(first);
        }
        if (first.isNonterminal()) { // nonterminal
          modified =
              modified || nonterminalProductionRuleFirst.addAll(nonterminalFirstwMap.get(first));
        }
      }
    }
    return modified;
  }

  // first(X1X2X3X4X5...Xn) = first(X1)+(first(X2)[ε∈X1])
  // +(first(X3)[ε∈X2]) + ... + (first(Xn)[ε∈Xn-1])
  private boolean addFirst() {
    boolean modified = false;
    for (Entry<Grammar, LinkedList<ProductionRule>> entry :
        nonterminalProductionRulesMap.entrySet()) {
      Grammar nonterminal = entry.getKey();
      Set<Grammar> nonterminalProductionRuleFirst = nonterminalFirstwMap.get(nonterminal);
      for (ProductionRule productionRule : entry.getValue()) {
        SyntaxNfa syntaxNfa = productionRule.rule.syntaxNfa;
        TreeSet<SyntaxNfaState> startNfaStates = getEpsilonClosure(syntaxNfa.start);
        for (SyntaxNfaState startNfaState : startNfaStates) {
          Set<Grammar> firstSuperset = startNfaState.edges.keySet();
          for (Grammar possibleFirst : firstSuperset) {
            if (possibleFirst.isTerminal() && possibleFirst != epsilon) { // terminal
              modified = modified || nonterminalProductionRuleFirst.add(possibleFirst);
            }
            if (possibleFirst.isNonterminal()) {
              Set<Grammar> possibleFirstSet = nonterminalFirstwMap.get(possibleFirst);
              modified = modified || nonterminalProductionRuleFirst.addAll(possibleFirstSet);
            }
          }
        }
      }
    }
    return modified;
  }

  private TreeSet<SyntaxNfaState> getEpsilonClosure(SyntaxNfaState state) {
    TreeSet<SyntaxNfaState> beBeuildedEpsilonClosure = new TreeSet<>();
    HashSet<SyntaxNfaState> waitToBuildEpsilonClosure = new HashSet<>();
    waitToBuildEpsilonClosure.add(state);

    while (!waitToBuildEpsilonClosure.isEmpty()) {
      Iterator<SyntaxNfaState> waitToBuildEpsilonClosureIt = waitToBuildEpsilonClosure.iterator();
      SyntaxNfaState beBuildedState = waitToBuildEpsilonClosureIt.next();
      waitToBuildEpsilonClosure.remove(beBuildedState);
      beBeuildedEpsilonClosure.add(beBuildedState);

      Set<SyntaxNfaState> beBuildedStateEpsilonToStates = beBuildedState.edges.get(epsilon);
      for (Grammar empty : emptyNonterminal) {
        Set<SyntaxNfaState> syntaxNfaStates = beBuildedState.edges.get(empty);
        if (null != syntaxNfaStates) {
          beBuildedStateEpsilonToStates.addAll(syntaxNfaStates);
        }
      }
      if (null != beBuildedStateEpsilonToStates) {
        for (SyntaxNfaState beBuildedStateEpsilonToState : beBuildedStateEpsilonToStates) {
          // 新状态，没被设置
          if (!beBeuildedEpsilonClosure.contains(beBuildedStateEpsilonToState)) {
            waitToBuildEpsilonClosure.add(beBuildedStateEpsilonToState);
          }
        }
      }
    }
    return beBeuildedEpsilonClosure;
  }

  // followRule2:A->aBβ, follow(B) = first(β)/ε .
  // followRule3:A->aB,or A->aBβ(ε ∈ first(β)), follow(B) += follow(A).
  private void setNonterminalFollowMap() {
    // init map
    Collection<Grammar> nonterminals = languageGrammar.nonterminals.values();
    nonterminalFollowMap = new HashMap<>(nonterminals.size());
    for (Grammar nonterminal : nonterminals) {
      nonterminalFollowMap.put(nonterminal, new HashSet<>());
    }

    setProductionRuleLastNonterminalMap();
    while (addFollow()) {}
  }

  private boolean addFollow() {
    boolean modified = false;
    // followRule2:A->aBβ, follow(B) = first(β)/ε .
    modified = modified || addFollowByRule2();
    // followRule3:A->aB,or A->aBβ(ε ∈ first(β)), follow(B) += follow(A).
    // modified = modified || addFollowByRule3();
    return modified;
  }

  // followRule2:A->aBβ, follow(B) = first(β)/ε .
  private boolean addFollowByRule2() {
    boolean modified = false;
    LinkedHashSet<SyntaxDfaState> states = languageGrammar.astDfa.getStates();
    // A->aBβ
    for (SyntaxDfaState state : states) {
      for (Entry<Grammar, SyntaxDfaState> edgeEntry : state.edges.entrySet()) {
        Grammar preGrammar = edgeEntry.getKey();
        if (preGrammar.isNonterminal()) {
          Set<Grammar> preGrammarFollow = nonterminalFollowMap.get(preGrammar);
          SyntaxDfaState nextGrammarState = edgeEntry.getValue();
          for (Grammar nextGrammar : nextGrammarState.edges.keySet()) {
            if (nextGrammar.isTerminal()) {
              modified = modified || preGrammarFollow.add(nextGrammar);
            }
            if (nextGrammar.isNonterminal()) {
              modified = modified || preGrammarFollow.addAll(first(nextGrammar));
            }
          }
        }
      }
    }
    // A->a(Bβ)*
    /*
    for (SyntaxDfaState state : states) {
      HashSet<Grammar> loopGrammars = new HashSet<>();
      for (Entry<Grammar, SyntaxDfaState> edgeEntry : state.edges.entrySet()) {
        Grammar loopGrammar = edgeEntry.getKey();
        SyntaxDfaState loopGrammarToState = edgeEntry.getValue();
        if (loopGrammarToState == state) { // loop
          loopGrammars.add(loopGrammar);
        }
      }
      for (Grammar preGrammar : loopGrammars) {
        if (preGrammar.isNonterminal()) {
          Set<Grammar> preGrammarFollow = nonterminalFollowMap.get(preGrammar);
          for (Grammar nextGrammar : loopGrammars) {
            if (nextGrammar.isTerminal()) {
              modified = modified || preGrammarFollow.add(nextGrammar);
            }
            if (nextGrammar.isNonterminal()) {
              modified = modified || preGrammarFollow.addAll(first(nextGrammar));
            }
          }
        }
      }
    }*/
    return modified;
  }

  private void setProductionRuleLastNonterminalMap() {
    int sizeOfProductionRule = 0;
    for (LinkedList<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      sizeOfProductionRule += productionRules.size();
    }
    productionRuleLastNonterminalMap = new HashMap<>(sizeOfProductionRule);

    for (LinkedList<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        productionRuleLastNonterminalMap.put(
            productionRule, getLastNonterminalOfProductionRule(productionRule));
      }
    }
  }

  // followRule3:A->aB,or A->aBβ(ε ∈ first(β)), follow(B) += follow(A).
  private boolean addFollowByRule3() {
    boolean modified = false;
    for (Entry<Grammar, LinkedList<ProductionRule>> entry :
        nonterminalProductionRulesMap.entrySet()) {
      Grammar nonterminal = entry.getKey();
      Set<Grammar> nonterminalFollow = nonterminalFollowMap.get(nonterminal);
      for (ProductionRule productionRule : entry.getValue()) {
        Set<Grammar> lastNonterminalsOfProductionRule =
            productionRuleLastNonterminalMap.get(productionRule);
        for (Grammar lastNonterminalOfProductionRule : lastNonterminalsOfProductionRule) {
          modified =
              modified
                  || nonterminalFollowMap
                      .get(lastNonterminalOfProductionRule)
                      .addAll(nonterminalFollow);
        }
      }
    }
    return modified;
  }

  private Set<Grammar> getLastNonterminalOfProductionRule(ProductionRule productionRule) {
    Set<Grammar> lastNonterminalOfProductionRule = new HashSet<>();

    RegExp regExp = productionRule.rule;
    setLastNonterminalOfProductionRule(regExp, lastNonterminalOfProductionRule);
    lastNonterminalOfProductionRule.remove(productionRule.grammar);

    return lastNonterminalOfProductionRule;
  }

  /**
   * .
   *
   * @param regExp 正则
   * @param lastNonterminalOfProductionRule 保存最后一个非终结符的集合
   * @return isPrevLastNonterminalEmpty
   */
  private boolean setLastNonterminalOfProductionRule(
      RegExp regExp, Set<Grammar> lastNonterminalOfProductionRule) {
    // 右 左 根遍历
    // 右 左 遍历
    boolean isPrevLastNonterminalEmpty = false;
    LinkedList<RegExp> children = regExp.children;
    ListIterator<RegExp> childrenIt = children.listIterator(children.size());
    while (childrenIt.hasPrevious()) {
      RegExp child = childrenIt.previous();
      boolean isChildEmpty =
          setLastNonterminalOfProductionRule(child, lastNonterminalOfProductionRule);
      // and ，孩子不连续空则结束；or,有一个孩子为空即可
      switch (regExp.relationshipOfChildren) {
        case AND:
          isPrevLastNonterminalEmpty = isChildEmpty;
          break;
        case OR:
          isPrevLastNonterminalEmpty = isPrevLastNonterminalEmpty || isChildEmpty;
          break;
        default:
      }
      if (!isPrevLastNonterminalEmpty) {
        return false;
      }
    }
    // 根遍历，根：COMPOSITE，为空则继续尝试
    if (regExp.type == RegExpType.COMPOSITE) {
      return regExp.repMinTimes.isZeroTimes();
    }
    // 根遍历，unit empty根，为空则继续尝试
    if (regExp.unitType == RegExpUnitType.EMPTY) {
      return true;
    }
    // 根遍历，根：unit grammar，是非终结符则添加，为空则继续尝试
    Grammar grammar = regExp.sets.getFirst().grammar;
    if (grammar.isTerminal()) { // terminal grammar
      // 为空则继续尝试
      return regExp.repMinTimes.isZeroTimes();
    }
    // nonterminal grammar
    lastNonterminalOfProductionRule.add(grammar);
    // 为空则继续尝试
    return regExp.repMinTimes.isZeroTimes() || nonterminalHasEmptyProductionRule(grammar);
  }

  private boolean nonterminalHasEmptyProductionRule(Grammar nonterminal) {
    return emptyNonterminal.contains(nonterminal);
  }

  private Set<Grammar> first(Grammar nonterminal) {
    return nonterminalFirstwMap.get(nonterminal);
  }
}
