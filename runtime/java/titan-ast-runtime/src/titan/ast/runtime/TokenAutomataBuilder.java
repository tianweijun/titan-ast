package titan.ast.runtime;

/**
 * .
 *
 * @author tian wei jun
 */
public class TokenAutomataBuilder {

  public TokenAutomata build(AutomataData automataData) {
    KeyWordAutomata keyWordAutomata = automataData.keyWordAutomata;
    TokenDfa tokenDfa = automataData.tokenDfa;
    TokenAutomata tokenAutomata = null;
    if (keyWordAutomata.emptyOrNot == KeyWordAutomata.EMPTY) {
      tokenAutomata = new DfaTokenAutomata(tokenDfa);
    }
    if (keyWordAutomata.emptyOrNot == KeyWordAutomata.NOT_EMPTY) {
      tokenAutomata = new KeyWordDfaTokenAutomata(keyWordAutomata, tokenDfa);
    }

    return tokenAutomata;
  }
}
