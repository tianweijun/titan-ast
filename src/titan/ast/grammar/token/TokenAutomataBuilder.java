package titan.ast.grammar.token;

import titan.ast.AstContext;

/**
 * .
 *
 * @author tian wei jun
 */
public class TokenAutomataBuilder {

  public void build() {
    DfaTokenAutomataBuilder dfaTokenAutomataBuilder =
        new DfaTokenAutomataBuilder(AstContext.get().languageGrammar);
    dfaTokenAutomataBuilder.build();
  }
}
