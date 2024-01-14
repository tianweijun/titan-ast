package titan.ast.grammar.syntax;

import titan.ast.AstContext;
import titan.ast.grammar.LanguageGrammar;

/**
 * .
 *
 * @author tian wei jun
 */
public class DfaAstAutomataFactory {

  private AstAutomataType type;

  public DfaAstAutomataFactory(AstAutomataType type) {
    this.type = type;
  }

  public void setType(AstAutomataType type) {
    this.type = type;
  }

  public AstAutomata build() {
    AstContext astContext = AstContext.get();
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    AstAutomata astAutomata = null;
    switch (type) {
      case BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        astAutomata =
            new BacktrackingBottomUpAstAutomata(
                languageGrammar.astDfa, languageGrammar.getStartGrammar());
        break;
      case FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        /*
        astAutomata =
            new FollowFilterBacktrackingBottomUpAstAutomataBuilder(
                    languageGrammar, astContext.nonterminalProductionRulesMap)
                .build();*/
        astAutomata =
            new BacktrackingBottomUpAstAutomata(
                languageGrammar.astDfa, languageGrammar.getStartGrammar());

        break;
    }
    return astAutomata;
  }
}
