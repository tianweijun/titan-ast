package titan.ast.fa.token;

import java.io.InputStream;
import java.util.List;
import titan.ast.runtime.Token;

/**
 * .
 *
 * @author tian wei jun
 */
public interface TokenAutomata {
  List<Token> buildToken(String sourceFilePath);

  List<Token> buildToken(InputStream byteInputStream);
}
