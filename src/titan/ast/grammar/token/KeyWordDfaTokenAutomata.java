package titan.ast.grammar.token;

import java.io.InputStream;
import java.util.List;
import titan.ast.target.Token;

/**
 * 识别token的dfa.
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

  /**
   * 根据输入流构造tokens.
   *
   * @param byteInputStream 将要识别文本的输入流.
   * @return
   */
  @Override
  public List<Token> buildToken(InputStream byteInputStream) {
    List<Token> ret = super.buildToken(byteInputStream);
    return keyWordAutomata.buildToken(ret);
  }
}
