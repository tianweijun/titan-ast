package titan.ast.runtime;

/**
 * .
 *
 * @author tian wei jun
 */
public class AstAutomataBuilder {

  public AstAutomata clone(AstAutomata baseAstAutomata) {
    AstAutomata astAutomata = null;

    switch (baseAstAutomata.getType()) {
      case BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        astAutomata =
            getBacktrackingBottomUpAstAutomata((BacktrackingBottomUpAstAutomata) baseAstAutomata);
        break;
      case FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        astAutomata =
            getDesiredFollowFilterAstAutomata(
                (FollowFilterBacktrackingBottomUpAstAutomata) baseAstAutomata);
        break;
    }
    return astAutomata;
  }

  private AstAutomata getBacktrackingBottomUpAstAutomata(
      BacktrackingBottomUpAstAutomata baseAstAutomata) {
    return new BacktrackingBottomUpAstAutomata(
        baseAstAutomata.astDfa, baseAstAutomata.startGrammar);
  }

  private AstAutomata getDesiredFollowFilterAstAutomata(
      FollowFilterBacktrackingBottomUpAstAutomata baseAstAutomata) {
    return new FollowFilterBacktrackingBottomUpAstAutomata(
        baseAstAutomata.astDfa,
        baseAstAutomata.startGrammar,
        baseAstAutomata.nonterminalFollowMap,
        baseAstAutomata.eof);
  }
}
