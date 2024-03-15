package titan.ast.runtime;

import java.util.Map;
import java.util.Set;

/**
 * .
 *
 * @author tian wei jun
 */
public class AutomataData {
  // meta data
  public String[] stringPool;
  public Grammar[] grammars;
  public ProductionRule[] productionRules;

  // TokenAutomata
  public KeyWordAutomata keyWordAutomata = null;
  public TokenDfa tokenDfa = null;

  // AstAutomata
  public AstAutomataType astAutomataType = AstAutomataType.BACKTRACKING_BOTTOM_UP_AST_AUTOMATA;
  public Grammar startGrammar = null;
  public SyntaxDfa astDfa = null;
  public Grammar eofGrammar = null;
  public Map<Grammar, Set<Grammar>> nonterminalFollowMap = null;
}
