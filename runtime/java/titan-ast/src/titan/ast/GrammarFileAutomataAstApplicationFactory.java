package titan.ast;

import titan.ast.impl.ast.AstWayGrammarFileAutomataAstApplication;

/**
 * .
 *
 * @author tian wei jun
 */
public class GrammarFileAutomataAstApplicationFactory {

  public static GrammarFileAutomataAstApplication create() {
    return new AstWayGrammarFileAutomataAstApplication();
  }
}
