package titan.ast.runtime;

import java.io.InputStream;
import java.util.List;

/**
 * .
 *
 * @author tian wei jun
 */
public interface TokenAutomata {
  List<Token> buildToken(String sourceFilePath);

  List<Token> buildToken(InputStream byteInputStream);
}
