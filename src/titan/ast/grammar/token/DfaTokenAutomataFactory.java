package titan.ast.grammar.token;

import titan.ast.grammar.LanguageGrammar;

/**
 * .
 *
 * @author tian wei jun
 */
public class DfaTokenAutomataFactory {

  public static DfaTokenAutomata create(LanguageGrammar languageGrammar) {
    KeyWordAutomata keyWordAutomata = languageGrammar.keyWordAutomataDetail.keyWordAutomata;
    TokenDfa tokenDfa = languageGrammar.tokenDfa;
    DfaTokenAutomata dfaTokenAutomata = null;
    if (keyWordAutomata.emptyOrNot == KeyWordAutomata.EMPTY) {
      dfaTokenAutomata = new DfaTokenAutomata(tokenDfa);
    }
    if (keyWordAutomata.emptyOrNot == KeyWordAutomata.NOT_EMPTY) {
      dfaTokenAutomata = new KeyWordDfaTokenAutomata(keyWordAutomata, tokenDfa);
    }

    return dfaTokenAutomata;
  }
}
