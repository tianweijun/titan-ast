package titan.ast.grammar.syntax;

/**
 * .
 *
 * @author tian wei jun
 */
public enum AstAutomataType {
  BACKTRACKING_BOTTOM_UP_AST_AUTOMATA,
  FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA;

  public static titan.ast.runtime.AstAutomataType toRuntimeAstAutomataType(
      AstAutomataType astAutomataType) {
    titan.ast.runtime.AstAutomataType runtimeAstAutomataType =
        titan.ast.runtime.AstAutomataType.BACKTRACKING_BOTTOM_UP_AST_AUTOMATA;
    switch (astAutomataType) {
      case BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        runtimeAstAutomataType =
            titan.ast.runtime.AstAutomataType.BACKTRACKING_BOTTOM_UP_AST_AUTOMATA;
        break;
      case FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        runtimeAstAutomataType =
            titan.ast.runtime.AstAutomataType.FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA;
        break;
    }
    return runtimeAstAutomataType;
  }
}
