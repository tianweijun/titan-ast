package titan.ast.fa.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import titan.ast.fa.FaStateType;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.NonterminalGrammar;
import titan.ast.grammar.TerminalGrammar;

/**
 * .
 *
 * @author tian wei jun
 */
public class FollowFilterBacktrackingBottomUpAstAutomataBuilder {

  final LanguageGrammar languageGrammar;
  final Grammar epsilon;
  final SyntaxDfa astDfa;
  LinkedList<ProductionRule> productionRules;

  HashSet<NonterminalGrammar> emptyNonterminal;
  HashMap<Grammar, Set<Grammar>> prevNonterminalNextGrammarMap;
  HashMap<ProductionRule, Set<Grammar>> productionRuleLastNonterminalMap;

  Map<Grammar, Set<Grammar>> nonterminalFirstMap;
  Map<Grammar, Set<Grammar>> nonterminalFollowMap;

  public FollowFilterBacktrackingBottomUpAstAutomataBuilder(LanguageGrammar languageGrammar, SyntaxDfa astDfa,
      LinkedHashMap<NonterminalGrammar, List<ProductionRule>> nonterminalProductionRulesMap) {
    this.languageGrammar = languageGrammar;
    epsilon = languageGrammar.epsilon;
    this.astDfa = astDfa;
    this.productionRules = new LinkedList<>();
    for (List<ProductionRule> productionRules : nonterminalProductionRulesMap.values()) {
      this.productionRules.addAll(productionRules);
    }
  }

  public FollowFilterBacktrackingBottomUpAstAutomata build() {
    setEmptyNonterminal();
    setNonterminalFirstMap();
    setNonterminalFollowMap();
    return new FollowFilterBacktrackingBottomUpAstAutomata(
        astDfa,
        languageGrammar.getStartGrammar(),
        nonterminalFollowMap,
        languageGrammar.eof);
  }

  private void setEmptyNonterminal() {
    emptyNonterminal = new HashSet<>();
    for (ProductionRule productionRule : productionRules) {
      if (FaStateType.isClosingTag(productionRule.reducingDfa.start.type)) {
        emptyNonterminal.add(productionRule.grammar);
      }
    }
  }

  private Set<Grammar> first(Grammar nonterminal) {
    return nonterminalFirstMap.get(nonterminal);
  }


  private void setNonterminalFirstMap() {
    // init map
    Collection<NonterminalGrammar> nonterminals = languageGrammar.nonterminals.values();
    nonterminalFirstMap = new HashMap<>(nonterminals.size()+1);
    for (Grammar nonterminal : nonterminals) {
      nonterminalFirstMap.put(nonterminal, new HashSet<>());
    }
    nonterminalFirstMap.put(languageGrammar.augmentedNonterminal, new HashSet<>());
    while (addFirst()) {
    }
  }

  // first(X1X2X3X4X5...Xn) = first(X1)+(first(X2)[ε∈X1])
  // +(first(X3)[ε∈X2]) + ... + (first(Xn)[ε∈Xn-1])
  private boolean addFirst() {
    boolean modified = false;
    for (ProductionRule productionRule : productionRules) {
      NonterminalGrammar nonterminal = productionRule.grammar;
      Set<Grammar> nonterminalProductionRuleFirst = nonterminalFirstMap.get(nonterminal);
      SyntaxNfa syntaxNfa = productionRule.nfa;
      TreeSet<SyntaxNfaState> startNfaStates = getEpsilonClosure(syntaxNfa.start);
      for (SyntaxNfaState startNfaState : startNfaStates) {
        Set<Grammar> firstSuperset = startNfaState.edges.keySet();
        for (Grammar possibleFirst : firstSuperset) {
          if (possibleFirst instanceof TerminalGrammar && possibleFirst != epsilon) { // terminal
            modified = modified || nonterminalProductionRuleFirst.add(possibleFirst);
          } else if (possibleFirst instanceof NonterminalGrammar) {
            modified = modified || nonterminalProductionRuleFirst.addAll(first(possibleFirst));
          }
        }
      }
    }

    return modified;
  }

  // followRule1:follow(augmentedNonterminal) += {Eof}
  // followRule2:A->aBβ, follow(B) = first(β)/ε .
  // followRule3:A->aB,or A->aBβ(ε ∈ first(β)), follow(B) += follow(A).
  private void setNonterminalFollowMap() {
    // init map
    Collection<NonterminalGrammar> nonterminals = languageGrammar.nonterminals.values();
    nonterminalFollowMap = new HashMap<>(nonterminals.size()+1);
    for (Grammar nonterminal : nonterminals) {
      nonterminalFollowMap.put(nonterminal, new HashSet<>());
    }
    nonterminalFollowMap.put(languageGrammar.augmentedNonterminal, new HashSet<>());
    // followRule1:follow(augmentedNonterminal) = {Eof}
    nonterminalFollowMap.get(languageGrammar.augmentedNonterminal).add(languageGrammar.eof);

    setPrevNonterminalNextGrammarMap();
    setProductionRuleLastNonterminalMap();

    while (addFollow()) {
    }
  }

  private void setPrevNonterminalNextGrammarMap() {
    // init map
    Collection<NonterminalGrammar> nonterminals = languageGrammar.nonterminals.values();
    prevNonterminalNextGrammarMap = new HashMap<>(nonterminals.size() + 1);
    for (Grammar nonterminal : nonterminals) {
      prevNonterminalNextGrammarMap.put(nonterminal, new HashSet<>());
    }
    prevNonterminalNextGrammarMap.put(languageGrammar.augmentedNonterminal, new HashSet<>());

    for (SyntaxDfaState state : astDfa.getStates()) {
      for (Entry<Grammar, SyntaxDfaState> prevEdgeEntry : state.edges.entrySet()) {
        Grammar preGrammar = prevEdgeEntry.getKey();
        if (preGrammar instanceof NonterminalGrammar) {
          Set<Grammar> prevNonterminalNextGrammars = prevNonterminalNextGrammarMap.get(preGrammar);
          SyntaxDfaState nextGrammarState = prevEdgeEntry.getValue();
          TreeSet<SyntaxDfaState> nextGrammarStates = getEpsilonClosureByDfaState(nextGrammarState);
          for (SyntaxDfaState nextState : nextGrammarStates) {
            for (Grammar nextGrammar : nextState.edges.keySet()) {
              if (nextGrammar != epsilon) {
                prevNonterminalNextGrammars.add(nextGrammar);
              }
            }
          }
        }
      }
    }
  }

  private boolean addFollow() {
    boolean modified = false;
    // followRule2:A->aBβ, follow(B) = first(β)/ε .
    modified = modified || addFollowByRule2();
    // followRule3:A->aB,or A->aBβ(ε ∈ first(β)), follow(B) += follow(A).
    modified = modified || addFollowByRule3();

    return modified;
  }

  private boolean addFollowByRule2() {
    boolean modified = false;
    for (Grammar prevGrammar : prevNonterminalNextGrammarMap.keySet()) {
      modified =
          modified
              || doAddFollowByRule2(prevGrammar, prevNonterminalNextGrammarMap.get(prevGrammar));
    }
    return modified;
  }

  private boolean doAddFollowByRule2(Grammar preGrammar, Set<Grammar> nextGrammars) {
    boolean modified = false;
    Set<Grammar> preGrammarFollow = nonterminalFollowMap.get(preGrammar);
    for (Grammar nextGrammar : nextGrammars) {
      if (nextGrammar instanceof TerminalGrammar) {
        modified = modified || preGrammarFollow.add(nextGrammar);
      } else if (nextGrammar instanceof NonterminalGrammar) {
        modified = modified || preGrammarFollow.addAll(first(nextGrammar));
      }
    }
    return modified;
  }

  private void setProductionRuleLastNonterminalMap() {
    productionRuleLastNonterminalMap = new HashMap<>(productionRules.size());

    for (ProductionRule productionRule : productionRules) {
      productionRuleLastNonterminalMap.put(
          productionRule, getLastNonterminalOfProductionRule(productionRule));
    }
  }

  // followRule3:A->aB,or A->aBβ(ε ∈ first(β)), follow(B) += follow(A).
  private boolean addFollowByRule3() {
    boolean modified = false;

    for (ProductionRule productionRule : productionRules) {
      NonterminalGrammar nonterminal = productionRule.grammar;
      Set<Grammar> followA = nonterminalFollowMap.get(nonterminal);
      Set<Grammar> lastNonterminalsOfProductionRule =
          productionRuleLastNonterminalMap.get(productionRule);
      for (Grammar lastNonterminalOfProductionRule : lastNonterminalsOfProductionRule) {
        Set<Grammar> followB = nonterminalFollowMap.get(lastNonterminalOfProductionRule);
        modified =  modified  || followB.addAll(followA);
      }
    }

    return modified;
  }

  private Set<Grammar> getLastNonterminalOfProductionRule(ProductionRule productionRule) {
    Set<Grammar> lastNonterminalOfProductionRule = new HashSet<>();

    Collection<SyntaxDfaState> startEpsilonClosureOfReducingDfa = null;

    if (emptyNonterminal.isEmpty()) {
      startEpsilonClosureOfReducingDfa = new ArrayList<>(1);
      startEpsilonClosureOfReducingDfa.add(productionRule.reducingDfa.start);
    } else {
      startEpsilonClosureOfReducingDfa =
          getEpsilonClosureByDfaState(productionRule.reducingDfa.start);
    }

    for (SyntaxDfaState startState : startEpsilonClosureOfReducingDfa) {
      for (Grammar lastGrammar : startState.edges.keySet()) {
        if (lastGrammar instanceof NonterminalGrammar) {
          lastNonterminalOfProductionRule.add(lastGrammar);
        }
      }
    }

    return lastNonterminalOfProductionRule;
  }

  private TreeSet<SyntaxDfaState> getEpsilonClosureByDfaState(SyntaxDfaState state) {
    ArrayList<SyntaxDfaState> states = new ArrayList<>(1);
    states.add(state);

    return getEpsilonClosureByDfaStates(states);
  }

  private TreeSet<SyntaxDfaState> getEpsilonClosureByDfaStates(Collection<SyntaxDfaState> states) {
    int prevCountStates = 0;
    TreeSet<SyntaxDfaState> epsilonClosure = getPartOfEpsilonClosureByDfaStates(states);
    int nextCountStates = epsilonClosure.size();
    while (nextCountStates > prevCountStates) {
      epsilonClosure = getPartOfEpsilonClosureByDfaStates(epsilonClosure);
      prevCountStates = nextCountStates;
      nextCountStates = epsilonClosure.size();
    }
    return epsilonClosure;
  }

  private TreeSet<SyntaxDfaState> getPartOfEpsilonClosureByDfaStates(
      Collection<SyntaxDfaState> states) {
    TreeSet<SyntaxDfaState> beBeuildedEpsilonClosure = new TreeSet<>();
    HashSet<SyntaxDfaState> waitToBuildEpsilonClosure = new HashSet<>(states);

    while (!waitToBuildEpsilonClosure.isEmpty()) {
      Iterator<SyntaxDfaState> waitToBuildEpsilonClosureIt = waitToBuildEpsilonClosure.iterator();
      SyntaxDfaState beBuildedState = waitToBuildEpsilonClosureIt.next();
      waitToBuildEpsilonClosure.remove(beBuildedState);
      beBeuildedEpsilonClosure.add(beBuildedState);

      Set<SyntaxDfaState> beBuildedStateEpsilonToStates = new HashSet<>();
      for (Grammar empty : emptyNonterminal) {
        SyntaxDfaState syntaxDfaState = beBuildedState.edges.get(empty);
        if (null != syntaxDfaState) {
          beBuildedStateEpsilonToStates.add(syntaxDfaState);
        }
      }
      for (SyntaxDfaState beBuildedStateEpsilonToState : beBuildedStateEpsilonToStates) {
        // 新状态，没被设置
        if (!beBeuildedEpsilonClosure.contains(beBuildedStateEpsilonToState)) {
          waitToBuildEpsilonClosure.add(beBuildedStateEpsilonToState);
        }
      }
    }
    return beBeuildedEpsilonClosure;
  }

  private TreeSet<SyntaxNfaState> getEpsilonClosure(SyntaxNfaState state) {
    ArrayList<SyntaxNfaState> states = new ArrayList<>(1);
    states.add(state);

    return getEpsilonClosure(states);
  }

  private TreeSet<SyntaxNfaState> getEpsilonClosure(Collection<SyntaxNfaState> states) {
    int prevCountStates = 0;
    TreeSet<SyntaxNfaState> epsilonClosure = getPartOfEpsilonClosure(states);
    int nextCountStates = epsilonClosure.size();
    while (nextCountStates > prevCountStates) {
      epsilonClosure = getPartOfEpsilonClosure(epsilonClosure);
      prevCountStates = nextCountStates;
      nextCountStates = epsilonClosure.size();
    }
    return epsilonClosure;
  }

  private TreeSet<SyntaxNfaState> getPartOfEpsilonClosure(Collection<SyntaxNfaState> states) {
    TreeSet<SyntaxNfaState> beBeuildedEpsilonClosure = new TreeSet<>();
    HashSet<SyntaxNfaState> waitToBuildEpsilonClosure = new HashSet<>(states);

    while (!waitToBuildEpsilonClosure.isEmpty()) {
      Iterator<SyntaxNfaState> waitToBuildEpsilonClosureIt = waitToBuildEpsilonClosure.iterator();
      SyntaxNfaState beBuildedState = waitToBuildEpsilonClosureIt.next();
      waitToBuildEpsilonClosure.remove(beBuildedState);
      beBeuildedEpsilonClosure.add(beBuildedState);

      Set<SyntaxNfaState> beBuildedStateEpsilonToStates = beBuildedState.edges.get(epsilon);
      if (null == beBuildedStateEpsilonToStates) {
        beBuildedStateEpsilonToStates = new HashSet<>();
      }
      for (Grammar empty : emptyNonterminal) {
        Set<SyntaxNfaState> syntaxNfaStates = beBuildedState.edges.get(empty);
        if (null != syntaxNfaStates) {
          beBuildedStateEpsilonToStates.addAll(syntaxNfaStates);
        }
      }
      for (SyntaxNfaState beBuildedStateEpsilonToState : beBuildedStateEpsilonToStates) {
        // 新状态，没被设置
        if (!beBeuildedEpsilonClosure.contains(beBuildedStateEpsilonToState)) {
          waitToBuildEpsilonClosure.add(beBuildedStateEpsilonToState);
        }
      }
    }
    return beBeuildedEpsilonClosure;
  }
}
