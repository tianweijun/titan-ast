package titan.ast.runtime;

import java.util.Map;
import java.util.Set;

/**
 * .
 *
 * @author tian wei jun
 */
class AutomataData {
  // meta data
  String[] stringPool;
  Grammar[] grammars;
  ProductionRule[] productionRules;

  // TokenAutomata
  DerivedTerminalGrammarAutomataData derivedTerminalGrammarAutomataData = null;
  TokenDfa tokenDfa = null;

  // AstAutomata
  AstAutomataType astAutomataType = AstAutomataType.BACKTRACKING_BOTTOM_UP_AST_AUTOMATA;
  Grammar startGrammar = null;
  SyntaxDfa astDfa = null;
  Grammar eofGrammar = null;
  Map<Grammar, Set<Grammar>> nonterminalFollowMap = null;
}
