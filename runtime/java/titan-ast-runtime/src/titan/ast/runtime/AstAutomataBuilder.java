package titan.ast.runtime;

/**
 * .
 *
 * @author tian wei jun
 */
class AstAutomataBuilder {

  AstAutomata build(AutomataData automataData) {
    AstAutomata astAutomata = null;
    switch (automataData.astAutomataType) {
      case BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        astAutomata =
            new BacktrackingBottomUpAstAutomata(automataData.astDfa, automataData.startGrammar);
        break;
      case FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        astAutomata =
            new FollowFilterBacktrackingBottomUpAstAutomata(
                automataData.astDfa,
                automataData.startGrammar,
                automataData.nonterminalFollowMap,
                automataData.eofGrammar);
        break;
    }
    return astAutomata;
  }
}
