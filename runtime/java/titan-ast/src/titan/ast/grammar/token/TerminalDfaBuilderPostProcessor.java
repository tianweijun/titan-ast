package titan.ast.grammar.token;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.FaStateType;
import titan.ast.grammar.Grammar;

/**
 * nfa转dfa后的后置处理器，主要是按照权重设置收敛的token为终态.
 *
 * @author tian wei jun
 */
class TerminalDfaBuilderPostProcessor {

  private final TokenDfa dfa;
  private final LinkedHashMap<String, Grammar> terminals;

  TerminalDfaBuilderPostProcessor(TokenDfa dfa, LinkedHashMap<String, Grammar> terminals) {
    this.dfa = dfa;
    this.terminals = terminals;
  }

  void postProcessTerminalDfaBuilder() {
    setWeightAndClosingTagOfState();
  }

  /** 选择一个优先级最高的设置为终态和权重. */
  private void setWeightAndClosingTagOfState() {
    Integer weight = terminals.size();
    HashMap<Grammar, Integer> terminalWeightMap = new HashMap<>(terminals.size());
    for (Grammar terminal : terminals.values()) {
      terminalWeightMap.put(terminal, weight--);
    }
    for (TokenDfaState dfaState : dfa.getStates()) {
      List<TokenNfaState> closingNfaStates =
          dfaState.nfaStates.stream()
              .filter(nfaState -> FaStateType.isClosingTag(nfaState.type))
              .collect(Collectors.toList());
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

  /**
   * 验证状态.
   *
   * @param state tokens的 确定有限状态自动机的 状态
   */
  private void validate(TokenDfaState state) {
    TreeSet<TokenNfaState> nfaStates = state.nfaStates;

    if (null == nfaStates || nfaStates.isEmpty()) {
      throw new AstRuntimeException("TokenDfaState is empty.");
    }
  }
}
