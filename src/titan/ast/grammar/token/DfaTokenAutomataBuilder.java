package titan.ast.grammar.token;

import titan.ast.grammar.LanguageGrammar;

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
    fragmentNfaBuilder.buildNfa();
    // terminal
    TerminalDfaBuilder terminalDfaBuilder =
        new TerminalDfaBuilder(languageGrammar.terminals, languageGrammar.terminalFragments);
    languageGrammar.tokenDfa = terminalDfaBuilder.build();

    DfaTokenAutomata dfaTokenAutomata = DfaTokenAutomataFactory.create(languageGrammar);
    languageGrammar.tokenAutomata = dfaTokenAutomata;

    return dfaTokenAutomata;
  }
}
