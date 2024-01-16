package titan.ast.runtime;

import java.util.Map;
import java.util.Set;

/**
 * 持久化对象.
 *
 * @author tian wei jun
 */
public class PersistentObject {
  KeyWordAutomata keyWordAutomata = null;
  TokenDfa tokenDfa = null;
  AstAutomata astAutomata = null;

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
    AstAutomataType astAutomataType = persistentData.getAstAutomataTypeByInputStream();
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
    Grammar startGrammar = persistentData.getGrammarByInputStream();
    SyntaxDfa astDfa = persistentData.getSyntaxDfaByInputStream();

    Grammar eofGrammar = persistentData.getGrammarByInputStream();
    Map<Grammar, Set<Grammar>> nonterminalFollowMap =
        persistentData.getNonterminalFollowMapByInputStream();
    astAutomata =
        new FollowFilterBacktrackingBottomUpAstAutomata(
            astDfa, startGrammar, nonterminalFollowMap, eofGrammar);
  }

  private void initBacktrackingBottomUpAstAutomata() {
    Grammar startGrammar = persistentData.getGrammarByInputStream();
    SyntaxDfa astDfa = persistentData.getSyntaxDfaByInputStream();
    astAutomata = new BacktrackingBottomUpAstAutomata(astDfa, startGrammar);
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
