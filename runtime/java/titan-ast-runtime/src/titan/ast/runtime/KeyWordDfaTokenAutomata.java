package titan.ast.runtime;

import java.io.InputStream;
import java.util.List;

/**
 * .
 *
 * @author tian wei jun
 */
public class KeyWordDfaTokenAutomata extends DfaTokenAutomata {
  KeyWordAutomata keyWordAutomata;

  /**
   * 初始化oneTokenStringBuilder、eof、dfa.
   *
   * @param keyWordAutomata 关键字有限自动机
   * @param dfa 识别token的 确定有限状态自动机
   */
  public KeyWordDfaTokenAutomata(KeyWordAutomata keyWordAutomata, TokenDfa dfa) {
    super(dfa);
    this.keyWordAutomata = keyWordAutomata;
  }

  @Override
  public List<Token> buildToken(InputStream byteInputStream) {
    List<Token> ret = super.buildToken(byteInputStream);
    return keyWordAutomata.buildToken(ret);
  }
}
