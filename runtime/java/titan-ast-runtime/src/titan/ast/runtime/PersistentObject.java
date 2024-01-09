package titan.ast.runtime;

/**
 * 持久化对象.
 *
 * @author tian wei jun
 */
public class PersistentObject {
  KeyWordAutomata keyWordAutomata = null;
  TokenDfa tokenDfa = null;
  Grammar startGrammar = null;
  SyntaxDfa astDfa = null;

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
      initStartGrammar();
      initProductionRules();
      initAstDfa();
    } catch (Exception e) {
      persistentData.compact();
      throw e;
    }
  }

  private void initAstDfa() {
    astDfa = persistentData.getSyntaxDfaByInputStream();
  }

  private void initProductionRules() {
    persistentData.getProductionRulesByInputStream();
  }

  private void initStartGrammar() {
    startGrammar = persistentData.getStartGrammarByInputStream();
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
