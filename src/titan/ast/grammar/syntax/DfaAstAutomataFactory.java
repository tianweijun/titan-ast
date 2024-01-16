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
        astAutomata = getDesiredFollowFilterAstAutomata(astContext);
        break;
    }
    return astAutomata;
  }

  private AstAutomata getBacktrackingBottomUpAstAutomata(AstContext astContext) {
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    return new BacktrackingBottomUpAstAutomata(
        languageGrammar.astDfa, languageGrammar.getStartGrammar());
  }

  private AstAutomata getDesiredFollowFilterAstAutomata(AstContext astContext) {
    AstAutomata astAutomata = null;

    LanguageGrammar languageGrammar = astContext.languageGrammar;
    FollowFilterBacktrackingBottomUpAstAutomata followFilterAstAutomata =
        new FollowFilterBacktrackingBottomUpAstAutomataBuilder(
                languageGrammar, astContext.nonterminalProductionRulesMap)
            .build();
    if (!followFilterAstAutomata.nonterminalFollowMap.isEmpty()) {
      astAutomata = followFilterAstAutomata;
    } else {
      astAutomata =
          new BacktrackingBottomUpAstAutomata(
              languageGrammar.astDfa, languageGrammar.getStartGrammar());
    }
    return astAutomata;
  }
}
