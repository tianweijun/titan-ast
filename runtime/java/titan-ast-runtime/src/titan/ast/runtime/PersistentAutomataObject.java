package titan.ast.runtime;

import java.util.Map;
import java.util.Set;

/**
 * 持久化对象.
 *
 * @author tian wei jun
 */
class PersistentAutomataObject {
  // TokenAutomata
  KeyWordAutomata keyWordAutomata = null;
  TokenDfa tokenDfa = null;

  // AstAutomata
  AstAutomataType astAutomataType = AstAutomataType.BACKTRACKING_BOTTOM_UP_AST_AUTOMATA;
  Grammar startGrammar = null;
  SyntaxDfa astDfa = null;
  Grammar eofGrammar = null;
  Map<Grammar, Set<Grammar>> nonterminalFollowMap = null;

  // meta data
  PersistentAutomataData persistentAutomataData;

  PersistentAutomataObject(PersistentAutomataData persistentAutomataData) {
    this.persistentAutomataData = persistentAutomataData;
  }

  void init() throws AutomataDataIoException {
    initStringPool();
    initGrammars();
    initKeyWordAutomata();
    initTokenDfa();
    initProductionRules();
    initAstAutomata();
  }

  private void initAstAutomata() throws AutomataDataIoException {
    astAutomataType = persistentAutomataData.getAstAutomataTypeByInputStream();
    switch (astAutomataType) {
      case BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        initBacktrackingBottomUpAstAutomata();
        break;
      case FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        initFollowFilterBacktrackingBottomUpAstAutomata();
        break;
      default:
    }
  }

  private void initFollowFilterBacktrackingBottomUpAstAutomata() throws AutomataDataIoException {
    startGrammar = persistentAutomataData.getGrammarByInputStream();
    astDfa = persistentAutomataData.getSyntaxDfaByInputStream();

    eofGrammar = persistentAutomataData.getGrammarByInputStream();
    nonterminalFollowMap = persistentAutomataData.getNonterminalFollowMapByInputStream();
  }

  private void initBacktrackingBottomUpAstAutomata() throws AutomataDataIoException {
    startGrammar = persistentAutomataData.getGrammarByInputStream();
    astDfa = persistentAutomataData.getSyntaxDfaByInputStream();
  }

  private void initProductionRules() throws AutomataDataIoException {
    persistentAutomataData.getProductionRulesByInputStream();
  }

  private void initTokenDfa() throws AutomataDataIoException {
    tokenDfa = persistentAutomataData.getTokenDfaByInputStream();
  }

  private void initKeyWordAutomata() throws AutomataDataIoException {
    keyWordAutomata = persistentAutomataData.getKeyWordAutomataByInputStream();
  }

  private void initGrammars() throws AutomataDataIoException {
    persistentAutomataData.getGrammarsByInputStream();
  }

  private void initStringPool() throws AutomataDataIoException {
    persistentAutomataData.getStringPoolByInputStream();
  }

  AutomataData toAutomataData() {
    AutomataData automataData = new AutomataData();
    // meta data
    automataData.stringPool = persistentAutomataData.stringPool;
    automataData.grammars = persistentAutomataData.grammars;
    automataData.productionRules = persistentAutomataData.productionRules;
    // TokenAutomata
    automataData.keyWordAutomata = keyWordAutomata;
    automataData.tokenDfa = tokenDfa;
    // AstAutomata
    automataData.astAutomataType = astAutomataType;
    automataData.startGrammar = startGrammar;
    automataData.astDfa = astDfa;
    automataData.eofGrammar = eofGrammar;
    automataData.nonterminalFollowMap = nonterminalFollowMap;
    return automataData;
  }
}
