package titan.ast.grammar.token;

import java.util.LinkedHashMap;
import java.util.Map;
import titan.ast.grammar.FaStateType;
import titan.ast.grammar.Grammar;

/**
 * 构造终结符的nfa并设置.
 *
 * @author tian wei jun
 */
public class TerminalNfaBuilder {

  LinkedHashMap<String, Grammar> terminals;
  private final Map<String, Grammar> terminalFragments;

  public TerminalNfaBuilder(
      LinkedHashMap<String, Grammar> terminals, LinkedHashMap<String, Grammar> terminalFragments) {
    this.terminals = terminals;
    this.terminalFragments = terminalFragments;
  }

  public void build() {
    buildNfa();
    setClosingState();
  }

  /**
   * Epsilon在nfa中有轻微不可逆滑向作用，要求所有节点的等价终态滑向一个共同的终态， 且仅有一个初态（也有可能滑向多个等价初态）， 同时也可以换个角度说nfa只有一个初态和一个终态.
   */
  private void setClosingState() {
    for (Map.Entry<String, Grammar> entry : terminals.entrySet()) {
      Grammar terminal = entry.getValue();
      TokenNfaState terminalEndState = terminal.regExp.tokenNfa.end;
      terminalEndState.type = FaStateType.appendClosingTag(terminalEndState.type);
      terminalEndState.terminal = terminal;
    }
  }

  public void buildNfa() {
    Reg2TokenNfaConverter reg2TokenNfaConverter = new Reg2TokenNfaConverter();
    reg2TokenNfaConverter.addTasks(terminals);
    reg2TokenNfaConverter.addDependentFragmentGrammars(terminalFragments);
    reg2TokenNfaConverter.convert();
  }
}
