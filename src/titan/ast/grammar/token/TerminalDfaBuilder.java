package titan.ast.grammar.token;

import java.util.LinkedHashMap;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.FaStateType;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.io.GrammarCharset;

/**
 * 构造终结符的dfa.
 *
 * @author tian wei jun
 */
public class TerminalDfaBuilder {

  private final LinkedHashMap<String, Grammar> terminals;
  private final LinkedHashMap<String, Grammar> terminalFragments;

  TerminalDfaBuilder(
      LinkedHashMap<String, Grammar> terminals, LinkedHashMap<String, Grammar> terminalFragments) {
    this.terminals = terminals;
    this.terminalFragments = terminalFragments;
  }

  /** 先构造所有终结符的nfa， 在将其转为dfa. */
  public TokenDfa build() {
    buildNfas();
    return buildDfa();
  }

  /**
   * 合并所有token的dfa为一个大的nfa，将nfa转为dfa，设置dfa的终态，最小化.
   *
   * @return tokens的 确定有限状态自动机
   */
  private TokenDfa buildDfa() {
    TokenNfa nfa = mergeNfasByTerminalNfas();
    TerminalTokenNfa2DfaConverter nfa2DfaConverter = new TerminalTokenNfa2DfaConverter(nfa);
    TokenDfa dfa = nfa2DfaConverter.convert();
    TerminalDfaBuilderPostProcessor postProcessor =
        new TerminalDfaBuilderPostProcessor(dfa, terminals);
    postProcessor.postProcessTerminalDfaBuilder();
    dfa = new TokenDfaOptimizer().optimize(dfa);
    if (FaStateType.isClosingTag(dfa.start.type)) { // 不允许空token
      Grammar emptyTerminal = dfa.start.terminal;
      throw new AstRuntimeException(
          String.format("empty token('%s') is not legal", emptyTerminal.name));
    }
    return dfa;
  }

  private TokenNfa mergeNfasByTerminalNfas() {
    TokenNfa nfa = new TokenNfa();
    TokenNfaState start = nfa.start;
    int epsilon = GrammarCharset.EPSILON;

    for (Grammar terminal : terminals.values()) {
      start.addEdge(epsilon, terminal.regExp.tokenNfa.start);
    }

    return nfa;
  }

  private void buildNfas() {
    TerminalNfaBuilder terminalNfaBuilder = new TerminalNfaBuilder(terminals, terminalFragments);
    terminalNfaBuilder.build();
  }
}
