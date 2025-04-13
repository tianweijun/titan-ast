package titan.ast.fa.token;

import java.util.LinkedHashMap;
import titan.ast.AstContext;
import titan.ast.AstRuntimeException;
import titan.ast.fa.FaStateType;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.TerminalGrammar;

/**
 * 构造终结符的dfa.
 *
 * @author tian wei jun
 */
public class TokenDfaBuilder {

  LinkedHashMap<String, TerminalGrammar> terminals;

  public TokenDfaBuilder() {
    terminals = AstContext.get().languageGrammar.terminals;
  }

  /**
   * 合并所有token的dfa为一个大的nfa，将nfa转为dfa，设置dfa的终态，最小化.
   */
  public void buildDfa() {
    TokenNfa nfa = mergeNfasByTerminalNfas();
    TokenNfa2DfaConverter nfa2DfaConverter = new TokenNfa2DfaConverter(nfa);
    TokenDfa dfa = nfa2DfaConverter.convert();
    TokenDfaBuilderPostProcessor postProcessor =
        new TokenDfaBuilderPostProcessor(dfa, terminals);
    postProcessor.postProcessTerminalDfaBuilder();
    dfa = new TokenDfaOptimizer().optimize(dfa);
    if (FaStateType.isClosingTag(dfa.start.type)) { // 不允许空token
      Grammar emptyTerminal = dfa.start.terminal;
      throw new AstRuntimeException(
          String.format("empty token('%s') is not legal", emptyTerminal.name));
    }
    AstContext.get().tokenDfa = dfa;
  }

  private TokenNfa mergeNfasByTerminalNfas() {
    TokenNfa nfa = new TokenNfa();
    TokenNfaState start = nfa.start;
    int epsilon = TokenNfa.EPSILON;

    for (TerminalGrammar terminal : terminals.values()) {
      start.addEdge(epsilon, terminal.tokenNfa.start);
    }
    return nfa;
  }
}
