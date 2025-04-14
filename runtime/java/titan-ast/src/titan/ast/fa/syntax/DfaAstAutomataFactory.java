package titan.ast.fa.syntax;

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

  public static void create() {
    AstContext.get().astAutomata =
        new DfaAstAutomataFactory(AstAutomataType.FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA)
            .build();
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
        astContext.astDfa, languageGrammar.getStartGrammar());
  }

  private FollowFilterBacktrackingBottomUpAstAutomata
      getFollowFilterBacktrackingBottomUpAstAutomata(AstContext astContext) {
    return new FollowFilterBacktrackingBottomUpAstAutomataBuilder(
        astContext.languageGrammar,astContext.astDfa,astContext.nonterminalProductionRulesMap)
        .build();
  }
}
