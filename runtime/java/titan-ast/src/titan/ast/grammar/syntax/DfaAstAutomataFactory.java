package titan.ast.grammar.syntax;

import titan.ast.AstContext;
import titan.ast.grammar.LanguageGrammar;

/**
 * .
 *
 * @author tian wei jun
 */
public class DfaAstAutomataFactory {

  private AstAutomataType desiredType;

  public DfaAstAutomataFactory(AstAutomataType type) {
    this.desiredType = type;
  }

  public void setDesiredType(AstAutomataType desiredType) {
    this.desiredType = desiredType;
  }

  public AstAutomata build() {
    AstAutomata astAutomata = null;

    AstContext astContext = AstContext.get();
    switch (desiredType) {
      case BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        astAutomata = getBacktrackingBottomUpAstAutomata(astContext);
        break;
      case FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        astAutomata = getFollowFilterBacktrackingBottomUpAstAutomata(astContext);
    }
    return astAutomata;
  }

  private AstAutomata getBacktrackingBottomUpAstAutomata(AstContext astContext) {
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    return new BacktrackingBottomUpAstAutomata(
        languageGrammar.astDfa, languageGrammar.getStartGrammar());
  }

  private FollowFilterBacktrackingBottomUpAstAutomata
      getFollowFilterBacktrackingBottomUpAstAutomata(AstContext astContext) {
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    return new FollowFilterBacktrackingBottomUpAstAutomataBuilder(
            languageGrammar, astContext.nonterminalProductionRulesMap)
        .build();
  }
}
