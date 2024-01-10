package titan.ast.runtime;


/**
 * .
 *
 * @author tian wei jun
 */
public class TokenAutomataBuilder {


  public TokenAutomata build(PersistentObject persistentObject) {
    KeyWordAutomata keyWordAutomata = persistentObject.keyWordAutomata;
    TokenDfa tokenDfa = persistentObject.tokenDfa;
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
