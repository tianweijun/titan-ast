package titan.ast.fa.token;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import titan.ast.fa.FaStateType;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.TerminalGrammar;

/**
 * nfa转dfa后的后置处理器，主要是按照权重设置收敛的token为终态.
 *
 * @author tian wei jun
 */
class TokenDfaBuilderPostProcessor {

  private final TokenDfa dfa;
  private final LinkedHashMap<String, TerminalGrammar> terminals;

  TokenDfaBuilderPostProcessor(TokenDfa dfa, LinkedHashMap<String, TerminalGrammar> terminals) {
    this.dfa = dfa;
    this.terminals = terminals;
  }

  void postProcessTerminalDfaBuilder() {
    setWeightAndClosingTagOfState();
  }

  /**
   * 选择一个优先级最高的设置为终态和权重.
   */
  private void setWeightAndClosingTagOfState() {
    int weight = terminals.size();
    HashMap<TerminalGrammar, Integer> terminalWeightMap = new HashMap<>(terminals.size());
    for (TerminalGrammar terminal : terminals.values()) {
      terminalWeightMap.put(terminal, weight--);
    }
    for (TokenDfaState dfaState : dfa.getStates()) {
      List<TokenNfaState> closingNfaStates =
          dfaState.nfaStates.stream()
              .filter(nfaState -> FaStateType.isClosingTag(nfaState.type))
              .toList();
      if (!closingNfaStates.isEmpty()) {
        Integer maxWeight = -1;
        TokenNfaState maxTokenNfaState = null;
        for (TokenNfaState closingNfaState : closingNfaStates) {
          Integer weightOfClosingNfaState = terminalWeightMap.get(closingNfaState.terminal);
          if (weightOfClosingNfaState > maxWeight) {
            maxWeight = weightOfClosingNfaState;
            maxTokenNfaState = closingNfaState;
          }
        }
        dfaState.type = FaStateType.appendClosingTag(dfaState.type);
        dfaState.weight = maxWeight;
        dfaState.terminal = maxTokenNfaState.terminal;
      }
    }
  }
}
