package titan.ast.runtime;

/**
 * .
 *
 * @author tian wei jun
 */
class TokenAutomataBuilder {

  TokenAutomata build(AutomataData automataData) {
    DerivedTerminalGrammarAutomataData derivedTerminalGrammarAutomataData =
        automataData.derivedTerminalGrammarAutomataData;
    TokenDfa tokenDfa = automataData.tokenDfa;
    TokenAutomata tokenAutomata = null;
    if (derivedTerminalGrammarAutomataData.count == 0) {
      tokenAutomata = new DfaTokenAutomata(tokenDfa);
    } else if (derivedTerminalGrammarAutomataData.count == 1) {
      tokenAutomata =
          new SingleDerivedTerminalGrammarAutomata(derivedTerminalGrammarAutomataData, tokenDfa);
    } else {
      tokenAutomata =
          new DerivedTerminalGrammarAutomata(derivedTerminalGrammarAutomataData, tokenDfa);
    }
    return tokenAutomata;
  }
}
