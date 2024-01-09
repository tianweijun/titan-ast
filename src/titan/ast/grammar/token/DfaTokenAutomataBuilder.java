package titan.ast.grammar.token;

import java.util.LinkedHashMap;
import java.util.Map;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.RegExp;

/**
 * 构造识别token的dfa.
 *
 * @auhor tian wei jun
 */
public class DfaTokenAutomataBuilder {

  final LanguageGrammar languageGrammar;

  public DfaTokenAutomataBuilder(LanguageGrammar languageGrammar) {
    this.languageGrammar = languageGrammar;
  }

  /**
   * fragment -> terminal -> nonterminal 因为正则转NFA简单直接没有过多冗余，那就节点NFA融合为一个NFA在转DFA及最小化.
   *
   * @return DFATokenAutomata
   */
  public DfaTokenAutomata build() {
    // fragment
    FragmentNfaBuilder fragmentNfaBuilder =
        new FragmentNfaBuilder(languageGrammar.terminalFragments);
    fragmentNfaBuilder.build();
    // terminal
    TerminalDfaBuilder terminalDfaBuilder =
        new TerminalDfaBuilder(languageGrammar.terminals, languageGrammar.terminalFragments);
    TokenDfa tokenDfa = terminalDfaBuilder.build();
    languageGrammar.tokenDfa = tokenDfa;

    buildUnitRegExpTerminalsMap();
    clearTransientObjects();

    DfaTokenAutomata dfaTokenAutomata = new DfaTokenAutomata(tokenDfa);
    languageGrammar.tokenAutomata = dfaTokenAutomata;

    return dfaTokenAutomata;
  }

  private void buildUnitRegExpTerminalsMap() {
    LinkedHashMap<String, Grammar> terminals = languageGrammar.terminals;
    Map<RegExp, Grammar> unitRegExpTerminalsMap = new LinkedHashMap<>(terminals.size());
    for (Grammar terminal : terminals.values()) {
      RegExp terminalCompositeRegExp = terminal.regExp;
      if (terminalCompositeRegExp.children.size() == 1) {
        RegExp unitRegExp = terminalCompositeRegExp.children.getFirst();
        if (unitRegExp.type == RegExp.RegExpType.UNIT
            && unitRegExp.unitType == RegExp.RegExpUnitType.SEQUENCE_CHARS) {
          if (unitRegExp.repMinTimes.isNumberTimesAndEqual(1)
              && unitRegExp.repMaxTimes.isNumberTimesAndEqual(1)) {
            unitRegExpTerminalsMap.put(unitRegExp, terminal);
          }
        }
      }
    }
    AstContext.get().unitRegExpTerminalsMap = unitRegExpTerminalsMap;
  }

  private void clearTransientObjects() {
    // fragment
    for (Grammar terminalFragment : languageGrammar.terminalFragments.values()) {
      terminalFragment.regExp = null;
      terminalFragment.attributes = null;
      terminalFragment.text = null;
    }
    // terminal
    for (Grammar terminal : languageGrammar.terminals.values()) {
      terminal.regExp = null;
      terminal.attributes = null;
      terminal.text = null;
    }
  }
}
