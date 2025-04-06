package titan.ast.grammar.token;

import titan.ast.grammar.LanguageGrammar;

/**
 * .
 *
 * @author tian wei jun
 */
public class DfaTokenAutomataFactory {

  public static DfaTokenAutomata create(LanguageGrammar languageGrammar) {
    DerivedTerminalGrammarAutomataData derivedTerminalGrammarAutomataData =
        languageGrammar.derivedTerminalGrammarAutomataDetail.derivedTerminalGrammarAutomataData;
    TokenDfa tokenDfa = languageGrammar.tokenDfa;
    DfaTokenAutomata dfaTokenAutomata = null;
    if (derivedTerminalGrammarAutomataData.isEmpty()) {
      dfaTokenAutomata = new DfaTokenAutomata(tokenDfa);
    } else {
      dfaTokenAutomata =
          new DerivedTerminalGrammarAutomata(derivedTerminalGrammarAutomataData, tokenDfa);
    }
    return dfaTokenAutomata;
  }
}
