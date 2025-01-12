package titan.ast.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.GrammarAction;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.LookaheadMatchingMode;
import titan.ast.grammar.NonterminalGrammar;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.grammar.syntax.AstAutomata;
import titan.ast.grammar.syntax.AstAutomataType;
import titan.ast.grammar.syntax.BacktrackingBottomUpAstAutomata;
import titan.ast.grammar.syntax.FollowFilterBacktrackingBottomUpAstAutomata;
import titan.ast.grammar.syntax.ProductionRule;
import titan.ast.grammar.syntax.SyntaxDfa;
import titan.ast.grammar.syntax.SyntaxDfaState;
import titan.ast.grammar.token.KeyWordAutomata;
import titan.ast.grammar.token.TokenDfaState;
import titan.ast.runtime.AutomataData;

/**
 * .
 *
 * @author tian wei jun
 */
public class AutomataDataBuilder {
  AutomataData automataData;
  // data
  AstContext astContext;
  HashMap<Grammar, titan.ast.runtime.Grammar> grammarMap;
  HashMap<ProductionRule, titan.ast.runtime.ProductionRule> productionRuleMap;

  public AutomataDataBuilder(AstContext astContext) {
    this.astContext = astContext;
  }

  /**
   * grammar file context has Built.
   *
   * @return AutomataData
   */
  public AutomataData build() {
    automataData = new AutomataData();
    // set grammar map
    setGrammarMap();
    // token dfa
    setKeyWordAutomata();
    setTokenDfa();

    // syntax dfa
    setProductionRule();
    setAstAutomata();
    return automataData;
  }

  private void setAstAutomata() {
    AstAutomata astAutomata = astContext.languageGrammar.astAutomata;
    AstAutomataType astAutomataType = astAutomata.getType();
    automataData.astAutomataType = AstAutomataType.toRuntimeAstAutomataType(astAutomataType);
    switch (astAutomataType) {
      case BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        setBacktrackingBottomUpAstAutomata();
        break;
      case FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        setFollowFilterBacktrackingBottomUpAstAutomata();
        break;
    }
  }

  private void setFollowFilterBacktrackingBottomUpAstAutomata() {
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    FollowFilterBacktrackingBottomUpAstAutomata astAutomata =
        (FollowFilterBacktrackingBottomUpAstAutomata) languageGrammar.astAutomata;
    automataData.startGrammar = grammarMap.get(astAutomata.startGrammar);
    automataData.astDfa = getSyntaxDfa(astAutomata.astDfa);

    automataData.eofGrammar = grammarMap.get(astAutomata.eof);
    automataData.nonterminalFollowMap = getNonterminalFollowMap(astAutomata.nonterminalFollowMap);
  }

  private Map<titan.ast.runtime.Grammar, Set<titan.ast.runtime.Grammar>> getNonterminalFollowMap(
      Map<Grammar, Set<Grammar>> nonterminalFollowMap) {
    Map<titan.ast.runtime.Grammar, Set<titan.ast.runtime.Grammar>> runtimeNonterminalFollowMap =
        new HashMap<>(nonterminalFollowMap.size());
    for (Entry<Grammar, Set<Grammar>> entry : nonterminalFollowMap.entrySet()) {
      Set<Grammar> follows = entry.getValue();
      Set<titan.ast.runtime.Grammar> runtimeFollows = new HashSet<>(follows.size());
      for (Grammar follow : follows) {
        runtimeFollows.add(grammarMap.get(follow));
      }
      runtimeNonterminalFollowMap.put(grammarMap.get(entry.getKey()), runtimeFollows);
    }
    return runtimeNonterminalFollowMap;
  }

  private void setBacktrackingBottomUpAstAutomata() {
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    BacktrackingBottomUpAstAutomata astAutomata =
        (BacktrackingBottomUpAstAutomata) languageGrammar.astAutomata;
    automataData.startGrammar = grammarMap.get(astAutomata.startGrammar);
    automataData.astDfa = getSyntaxDfa(astAutomata.astDfa);
  }

  private void setProductionRule() {
    Collection<LinkedList<ProductionRule>> productionRuleCollection =
        astContext.nonterminalProductionRulesMap.values();
    int capacity = 0;
    for (LinkedList<ProductionRule> productionRules : productionRuleCollection) {
      capacity += productionRules.size();
    }
    // init ProductionRule and map
    productionRuleMap = new LinkedHashMap<>(capacity);
    for (LinkedList<ProductionRule> productionRules : productionRuleCollection) {
      for (ProductionRule productionRule : productionRules) {
        titan.ast.runtime.ProductionRule runtimeProductionRule =
            new titan.ast.runtime.ProductionRule();
        runtimeProductionRule.grammar = grammarMap.get(productionRule.grammar);
        runtimeProductionRule.alias = productionRule.alias;
        productionRuleMap.put(productionRule, runtimeProductionRule);
      }
    }
    // set reducingDfa
    for (LinkedList<ProductionRule> productionRules : productionRuleCollection) {
      for (ProductionRule productionRule : productionRules) {
        titan.ast.runtime.ProductionRule runtimeProductionRule =
            productionRuleMap.get(productionRule);
        runtimeProductionRule.reducingDfa = getSyntaxDfa(productionRule.reducingDfa);
      }
    }
  }

  private titan.ast.runtime.SyntaxDfa getSyntaxDfa(SyntaxDfa astDfa) {
    LinkedHashSet<SyntaxDfaState> states = astDfa.getStates();
    // map and property
    HashMap<SyntaxDfaState, titan.ast.runtime.SyntaxDfaState> statesMap =
        new HashMap<>(states.size());
    int indexOfStates = 0;
    for (SyntaxDfaState state : states) {
      titan.ast.runtime.SyntaxDfaState runtimeState =
          new titan.ast.runtime.SyntaxDfaState(indexOfStates++);
      runtimeState.type = state.type;
      statesMap.put(state, runtimeState);
    }
    // edges
    for (SyntaxDfaState state : states) {
      titan.ast.runtime.SyntaxDfaState runtimeState = statesMap.get(state);
      runtimeState.edges = new HashMap<>(state.edges.size());
      for (Entry<Grammar, SyntaxDfaState> entry : state.edges.entrySet()) {
        runtimeState.edges.put(grammarMap.get(entry.getKey()), statesMap.get(entry.getValue()));
      }
    }
    // closingProductionRules
    for (SyntaxDfaState state : states) {
      titan.ast.runtime.SyntaxDfaState runtimeState = statesMap.get(state);
      runtimeState.closingProductionRules = new ArrayList<>(state.closingProductionRules.size());
      for (ProductionRule closingProductionRule : state.closingProductionRules) {
        runtimeState.closingProductionRules.add(productionRuleMap.get(closingProductionRule));
      }
    }
    // ast dfa
    titan.ast.runtime.SyntaxDfa runtimeAstDfa = new titan.ast.runtime.SyntaxDfa();
    runtimeAstDfa.start = statesMap.get(astDfa.start);
    return runtimeAstDfa;
  }

  private void setTokenDfa() {
    LinkedHashSet<TokenDfaState> states = astContext.languageGrammar.tokenDfa.getStates();
    // map and property
    HashMap<TokenDfaState, titan.ast.runtime.TokenDfaState> statesMap =
        new HashMap<>(states.size());
    for (TokenDfaState state : states) {
      titan.ast.runtime.TokenDfaState runtimeState = new titan.ast.runtime.TokenDfaState();
      runtimeState.type = state.type;
      runtimeState.weight = state.weight;
      runtimeState.terminal = grammarMap.get(state.terminal);
      statesMap.put(state, runtimeState);
    }
    // edges
    for (TokenDfaState state : states) {
      titan.ast.runtime.TokenDfaState runtimeState = statesMap.get(state);
      runtimeState.edges = new HashMap<>(state.edges.size());
      for (Entry<Integer, TokenDfaState> entry : state.edges.entrySet()) {
        runtimeState.edges.put(entry.getKey(), statesMap.get(entry.getValue()));
      }
    }
    // token dfa
    titan.ast.runtime.TokenDfa tokenDfa = new titan.ast.runtime.TokenDfa();
    tokenDfa.start = statesMap.get(astContext.languageGrammar.tokenDfa.start);
    automataData.tokenDfa = tokenDfa;
  }

  private void setKeyWordAutomata() {
    titan.ast.runtime.KeyWordAutomata keyWordAutomata = new titan.ast.runtime.KeyWordAutomata();
    automataData.keyWordAutomata = keyWordAutomata;

    KeyWordAutomata grammarKeyWordAutomata =
        astContext.languageGrammar.keyWordAutomataDetail.keyWordAutomata;
    // empty or not
    keyWordAutomata.emptyOrNot = grammarKeyWordAutomata.emptyOrNot;
    if (grammarKeyWordAutomata.emptyOrNot == KeyWordAutomata.EMPTY) {
      return;
    }
    // rootKeyWord
    keyWordAutomata.rootKeyWord = grammarMap.get(grammarKeyWordAutomata.rootKeyWord);
    // textTerminalMap
    HashMap<String, Grammar> textTerminalMap = grammarKeyWordAutomata.textTerminalMap;
    HashMap<String, titan.ast.runtime.Grammar> runtimeTextTerminalMap =
        new HashMap<>(textTerminalMap.size());
    keyWordAutomata.textTerminalMap = runtimeTextTerminalMap;

    for (Entry<String, Grammar> entry : textTerminalMap.entrySet()) {
      runtimeTextTerminalMap.put(entry.getKey(), grammarMap.get(entry.getValue()));
    }
  }

  private void setGrammarMap() {
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    int countOfGrammars =
        languageGrammar.terminals.size() + 2 + languageGrammar.nonterminals.size();
    grammarMap = new LinkedHashMap<>(countOfGrammars);
    int indexOfGrammar = 0;

    for (Grammar terminal : languageGrammar.terminals.values()) {
      grammarMap.put(terminal, creatRuntimeTerminal((TerminalGrammar) terminal, indexOfGrammar++));
    }
    grammarMap.put(
        languageGrammar.epsilon,
        creatRuntimeTerminal((TerminalGrammar) languageGrammar.epsilon, indexOfGrammar++));
    grammarMap.put(
        languageGrammar.eof,
        creatRuntimeTerminal((TerminalGrammar) languageGrammar.eof, indexOfGrammar++));

    for (Grammar nonterminal : languageGrammar.nonterminals.values()) {
      grammarMap.put(
          nonterminal, creatRuntimeNonterminal((NonterminalGrammar) nonterminal, indexOfGrammar++));
    }
  }

  private titan.ast.runtime.NonterminalGrammar creatRuntimeNonterminal(
      NonterminalGrammar nonterminal, int indexOfGrammar) {
    titan.ast.runtime.NonterminalGrammar runtimeNonterminal =
        new titan.ast.runtime.NonterminalGrammar(indexOfGrammar);
    runtimeNonterminal.type = titan.ast.runtime.GrammarType.NONTERMINAL;
    runtimeNonterminal.name = nonterminal.name;
    runtimeNonterminal.action = GrammarAction.toRuntimeAction(nonterminal.action);
    return runtimeNonterminal;
  }

  private titan.ast.runtime.Grammar creatRuntimeTerminal(
      TerminalGrammar terminal, int indexOfGrammar) {
    titan.ast.runtime.TerminalGrammar runtimeTerminal =
        new titan.ast.runtime.TerminalGrammar(indexOfGrammar);
    runtimeTerminal.type = titan.ast.runtime.GrammarType.TERMINAL;
    runtimeTerminal.name = terminal.name;
    runtimeTerminal.action = GrammarAction.toRuntimeAction(terminal.action);
    runtimeTerminal.lookaheadMatchingMode =
        LookaheadMatchingMode.toRuntimeLookaheadMatchingMode(terminal.lookaheadMatchingMode);
    return runtimeTerminal;
  }
}
