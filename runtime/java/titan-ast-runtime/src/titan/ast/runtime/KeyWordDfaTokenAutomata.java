package titan.ast.runtime;

import java.io.InputStream;
import titan.ast.runtime.AstGeneratorResult.TokensResult;

/**
 * .
 *
 * @author tian wei jun
 */
class KeyWordDfaTokenAutomata extends DfaTokenAutomata {
  KeyWordAutomata keyWordAutomata;

  /**
   * 初始化oneTokenStringBuilder、eof、dfa.
   *
   * @param keyWordAutomata 关键字有限自动机
   * @param dfa 识别token的 确定有限状态自动机
   */
  KeyWordDfaTokenAutomata(KeyWordAutomata keyWordAutomata, TokenDfa dfa) {
    super(dfa);
    this.keyWordAutomata = keyWordAutomata;
  }

  @Override
  public TokensResult buildToken(InputStream byteInputStream) {
    TokensResult tokensResult = super.buildToken(byteInputStream);
    if (tokensResult.isOk()) {
      keyWordAutomata.buildToken(tokensResult.getOkData());
    }
    return tokensResult;
  }
}
