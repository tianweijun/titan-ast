package titan.ast.persistence;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import titan.ast.AstContext;
import titan.ast.fa.syntax.ProductionRule;
import titan.ast.fa.syntax.SyntaxDfa;
import titan.ast.fa.syntax.SyntaxDfaState;
import titan.ast.fa.token.DerivedTerminalGrammarAutomataData;
import titan.ast.fa.token.DerivedTerminalGrammarAutomataData.RootTerminalGrammarMap;
import titan.ast.fa.token.TokenDfa;
import titan.ast.fa.token.TokenDfaState;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.util.StringUtils;

/**
 * 持久化数据.
 *
 * @author tian wei jun
 */
public class PersistentData {

  // ----------data start-----------
  public LinkedHashMap<String, Integer> stringPool;
  // countOfGrammars-name-type-action
  public LinkedHashMap<Grammar, Integer> grammarIntegerMap;
  // countOfTokenDfaStates-type-weight-terminal-countOfEdges-[ch,dest]{countOfEdges}
  // tokenDfa.start==tokenDfaStates[0]
  // public int[] tokenDfaStates = null;
  // public Grammar start;
  public LinkedHashMap<ProductionRule, Integer> productionRuleIntegerMap;
  // KeyWordAutomata
  // AstAutomata
  // ----------data end-----------
  AstContext astContext;

  public PersistentData(AstContext astContext) {
    this.astContext = astContext;
  }

  public int[] initTokenDfaStates(TokenDfa tokenDfa) {
    // countOfTokenDfaStates-(type-weight-terminal-countOfEdges-[ch,dest]{countOfEdges})
    LinkedHashSet<TokenDfaState> states = tokenDfa.getStates();
    LinkedHashMap<TokenDfaState, Integer> tokenDfaStateIntegerMap =
        new LinkedHashMap<>(states.size());
    int intSymbolOfTokenDfaState = 0;
    int capacity = 1; // countOfTokenDfaStates
    for (TokenDfaState state : states) {
      capacity += 4; // type-weight-terminal-countOfEdges
      capacity += state.edges.size() * 2;
      tokenDfaStateIntegerMap.put(state, intSymbolOfTokenDfaState++);
    }
    int[] tokenDfaStates = new int[capacity];
    int indexOfTokenDfaState = 0;
    tokenDfaStates[indexOfTokenDfaState++] = states.size(); // countOfTokenDfaStates
    for (TokenDfaState state : states) {
      // type-weight-terminal-countOfEdges-[ch,dest]{countOfEdges}
      tokenDfaStates[indexOfTokenDfaState++] = state.type;
      tokenDfaStates[indexOfTokenDfaState++] = state.weight;
      if (null == state.terminal) {
        tokenDfaStates[indexOfTokenDfaState++] = -1;
      } else {
        tokenDfaStates[indexOfTokenDfaState++] = grammarIntegerMap.get(state.terminal);
      }
      tokenDfaStates[indexOfTokenDfaState++] = state.edges.size();
      for (Entry<Integer, TokenDfaState> entry : state.edges.entrySet()) {
        tokenDfaStates[indexOfTokenDfaState++] = entry.getKey();
        tokenDfaStates[indexOfTokenDfaState++] = tokenDfaStateIntegerMap.get(entry.getValue());
      }
    }
    return tokenDfaStates;
  }

  public void initGrammars() {
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    LinkedHashMap<String, TerminalGrammar> derivedTerminalGrammars =
        languageGrammar.derivedTerminalGrammarAutomataDetail.derivedTerminalGrammarAutomataData.derivedTerminalGrammars;
    int countOfGrammars =
        languageGrammar.terminals.size() + 2 + languageGrammar.nonterminals.size() + 1 + derivedTerminalGrammars.size();
    grammarIntegerMap = new LinkedHashMap<>(countOfGrammars);
    int indexOfGrammar = 0;

    for (Grammar terminal : languageGrammar.terminals.values()) {
      grammarIntegerMap.put(terminal, indexOfGrammar++);
    }
    grammarIntegerMap.put(languageGrammar.epsilon, indexOfGrammar++);
    grammarIntegerMap.put(languageGrammar.eof, indexOfGrammar++);

    for (Grammar nonterminal : languageGrammar.nonterminals.values()) {
      grammarIntegerMap.put(nonterminal, indexOfGrammar++);
    }
    grammarIntegerMap.put(languageGrammar.augmentedNonterminal, indexOfGrammar++);

    for (Grammar derivedTerminalGrammar : derivedTerminalGrammars.values()) {
      grammarIntegerMap.put(derivedTerminalGrammar, indexOfGrammar++);
    }
  }

  public void initStringPool() {
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    DerivedTerminalGrammarAutomataData derivedTerminalGrammarAutomataData =
        languageGrammar.derivedTerminalGrammarAutomataDetail.derivedTerminalGrammarAutomataData;
    int capacity =
        languageGrammar.terminals.size()
            + 2
            + languageGrammar.nonterminals.size()
            + 1
            + derivedTerminalGrammarAutomataData.derivedTerminalGrammars.size()
            + derivedTerminalGrammarAutomataData.sizeOfTextTerminalMap();
    stringPool = new LinkedHashMap<>(capacity);
    int indexOfString = 0;
    // terminals
    for (Grammar terminal : languageGrammar.terminals.values()) {
      String name = terminal.name;
      if (!stringPool.containsKey(name)) {
        stringPool.put(name, indexOfString++);
      }
    }
    if (!stringPool.containsKey(languageGrammar.epsilon.name)) {
      stringPool.put(languageGrammar.epsilon.name, indexOfString++);
    }
    if (!stringPool.containsKey(languageGrammar.eof.name)) {
      stringPool.put(languageGrammar.eof.name, indexOfString++);
    }
    // nonterminals
    for (Grammar nonterminal : languageGrammar.nonterminals.values()) {
      String name = nonterminal.name;
      if (!stringPool.containsKey(name)) {
        stringPool.put(name, indexOfString++);
      }
    }
    if (!stringPool.containsKey(languageGrammar.augmentedNonterminal.name)) {
      stringPool.put(languageGrammar.augmentedNonterminal.name, indexOfString++);
    }
    // (grammarName,text) of derivedTerminalGrammar
    for (TerminalGrammar terminalGrammar : derivedTerminalGrammarAutomataData.derivedTerminalGrammars.values()) {
      String name = terminalGrammar.name;
      if (!stringPool.containsKey(name)) {
        stringPool.put(name, indexOfString++);
      }
    }
    if (!derivedTerminalGrammarAutomataData.isEmpty()) {
      //text
      for (RootTerminalGrammarMap rootTerminalGrammarMap :
          derivedTerminalGrammarAutomataData.rootTerminalGrammarMaps) {
        for (Entry<String, TerminalGrammar> entry : rootTerminalGrammarMap.textTerminalMap.entrySet()) {
          String text = entry.getKey();
          if (!stringPool.containsKey(text)) {
            stringPool.put(text, indexOfString++);
          }
        }
      }
    }

    // productionRule alias
    for (List<ProductionRule> productionRules :
        astContext.nonterminalProductionRulesMap.values()) {
      for (ProductionRule productionRule : productionRules) {
        String str = productionRule.alias;
        if (StringUtils.isNotBlank(str) && !stringPool.containsKey(str)) {
          stringPool.put(str, indexOfString++);
        }
      }
    }
  }

  public void initProductionRules() {
    Collection<List<ProductionRule>> productionRuleCollection =
        astContext.nonterminalProductionRulesMap.values();
    int capacity = 0;
    for (List<ProductionRule> productionRules : productionRuleCollection) {
      capacity += productionRules.size();
    }
    productionRuleIntegerMap = new LinkedHashMap<>(capacity);
    int intSymbolOfProductionRule = 0;
    for (List<ProductionRule> productionRules : productionRuleCollection) {
      for (ProductionRule productionRule : productionRules) {
        productionRuleIntegerMap.put(productionRule, intSymbolOfProductionRule++);
      }
    }
  }

  public int[] getSyntaxDfaStates(SyntaxDfa syntaxDfa) {
    // countOfSyntaxDfaStates-(type-countOfEdges-[ch,dest]{countOfEdges}-countOfProductions-productions)
    LinkedHashSet<SyntaxDfaState> states = syntaxDfa.getStates();
    LinkedHashMap<SyntaxDfaState, Integer> syntaxDfaStateIntegerMap =
        new LinkedHashMap<>(states.size());
    int intSymbolOfSyntaxDfaState = 0;
    int capacity = 1; // countOfTokenDfaStates
    for (SyntaxDfaState state : states) {
      capacity += 2; // type-countOfEdges
      capacity += state.edges.size() * 2;
      capacity += 1; // countOfProductions
      capacity += state.closingProductionRules.size();
      syntaxDfaStateIntegerMap.put(state, intSymbolOfSyntaxDfaState++);
    }
    int[] syntaxDfaStates = new int[capacity];
    int indexOfSyntaxDfaState = 0;
    syntaxDfaStates[indexOfSyntaxDfaState++] = states.size(); // countOfSyntaxDfaStates
    for (SyntaxDfaState state : states) {
      syntaxDfaStates[indexOfSyntaxDfaState++] = state.type;
      // edges
      syntaxDfaStates[indexOfSyntaxDfaState++] = state.edges.size();
      for (Entry<Grammar, SyntaxDfaState> entry : state.edges.entrySet()) {
        syntaxDfaStates[indexOfSyntaxDfaState++] = grammarIntegerMap.get(entry.getKey());
        syntaxDfaStates[indexOfSyntaxDfaState++] = syntaxDfaStateIntegerMap.get(entry.getValue());
      }
      // productionRules
      syntaxDfaStates[indexOfSyntaxDfaState++] = state.closingProductionRules.size();
      for (ProductionRule productionRule : state.closingProductionRules) {
        syntaxDfaStates[indexOfSyntaxDfaState++] = productionRuleIntegerMap.get(productionRule);
      }
    }
    return syntaxDfaStates;
  }
}
