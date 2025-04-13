package titan.ast.fa.token;

import java.io.InputStream;
import java.util.List;
import titan.ast.AstRuntimeException;
import titan.ast.runtime.Token;

/**
 * 识别token的dfa,只保留依赖的数据，真正的实现在runtime里面.
 *
 * @author tian wei jun
 */
public class DfaTokenAutomata implements TokenAutomata {

  TokenDfa dfa;

  /**
   * 初始化oneTokenStringBuilder、eof、dfa.
   *
   * @param dfa 识别token的 确定有限状态自动机
   */
  public DfaTokenAutomata(TokenDfa dfa) {
    this.dfa = dfa;
  }

  @Override
  public List<Token> buildToken(String sourceFilePath) {
    throw new AstRuntimeException("not support,implement in runtime");
  }

  /**
   * 根据输入流构造tokens.
   *
   * @param byteInputStream 将要识别文本的输入流.
   * @return
   */
  @Override
  public List<Token> buildToken(InputStream byteInputStream) {
    throw new AstRuntimeException("not support,implement in runtime");
  }
}
