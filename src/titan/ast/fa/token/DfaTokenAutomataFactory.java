package titan.ast.fa.token;

import titan.ast.AstContext;

/**
 * .
 *
 * @author tian wei jun
 */
public class DfaTokenAutomataFactory {

  public static void create() {
    AstContext astContext = AstContext.get();
    DerivedTerminalGrammarAutomataData derivedTerminalGrammarAutomataData =
        astContext.languageGrammar.derivedTerminalGrammarAutomataDetail.derivedTerminalGrammarAutomataData;
    TokenDfa tokenDfa = astContext.tokenDfa;
    DfaTokenAutomata dfaTokenAutomata = null;
    if (derivedTerminalGrammarAutomataData.isEmpty()) {
      dfaTokenAutomata = new DfaTokenAutomata(tokenDfa);
    } else {
      dfaTokenAutomata =
          new DerivedTerminalGrammarAutomata(derivedTerminalGrammarAutomataData, tokenDfa);
    }
    astContext.tokenAutomata = dfaTokenAutomata;
  }
}
