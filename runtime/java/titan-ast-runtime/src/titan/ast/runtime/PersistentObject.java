package titan.ast.runtime;

import java.util.Map;
import java.util.Set;

/**
 * 持久化对象.
 *
 * @author tian wei jun
 */
public class PersistentObject {
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
  PersistentData persistentData;

  public PersistentObject(PersistentData persistentData) {
    this.persistentData = persistentData;
    init();
  }

  public void init() {
    try {
      // 按文件组织顺序获得各个部分数据，每个部分获取一次
      initStringPool();
      initGrammars();
      initKeyWordAutomata();
      initTokenDfa();
      initProductionRules();
      initAstAutomata();
    } catch (Exception e) {
      persistentData.compact();
      throw e;
    }
  }

  private void initAstAutomata() {
    astAutomataType = persistentData.getAstAutomataTypeByInputStream();
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

  private void initFollowFilterBacktrackingBottomUpAstAutomata() {
    startGrammar = persistentData.getGrammarByInputStream();
    astDfa = persistentData.getSyntaxDfaByInputStream();

    eofGrammar = persistentData.getGrammarByInputStream();
    nonterminalFollowMap = persistentData.getNonterminalFollowMapByInputStream();
  }

  private void initBacktrackingBottomUpAstAutomata() {
    startGrammar = persistentData.getGrammarByInputStream();
    astDfa = persistentData.getSyntaxDfaByInputStream();
  }

  private void initProductionRules() {
    persistentData.getProductionRulesByInputStream();
  }

  private void initTokenDfa() {
    tokenDfa = persistentData.getTokenDfaByInputStream();
  }

  private void initKeyWordAutomata() {
    keyWordAutomata = persistentData.getKeyWordAutomataByInputStream();
  }

  private void initGrammars() {
    persistentData.getGrammarsByInputStream();
  }

  private void initStringPool() {
    persistentData.getStringPoolByInputStream();
  }
}
