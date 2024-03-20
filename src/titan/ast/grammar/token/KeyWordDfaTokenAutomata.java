package titan.ast.grammar.token;

/**
 * 识别token的dfa,增加了keyword的判定，只保留依赖的数据，真正的实现在runtime里面.
 *
 * @author tian wei jun
 */
public class KeyWordDfaTokenAutomata extends DfaTokenAutomata {

  KeyWordAutomata keyWordAutomata;

  /**
   * 初始化oneTokenStringBuilder、eof、dfa.
   *
   * @param keyWordAutomata keyWord自动机
   * @param dfa 识别token的 确定有限状态自动机
   */
  public KeyWordDfaTokenAutomata(KeyWordAutomata keyWordAutomata, TokenDfa dfa) {
    super(dfa);
    this.keyWordAutomata = keyWordAutomata;
  }
}
