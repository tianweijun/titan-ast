package titan.ast.runtime;

import java.io.InputStream;
import titan.ast.runtime.AstGeneratorResult.TokensResult;

/**
 * .
 *
 * @author tian wei jun
 */
public interface TokenAutomata {
  TokensResult buildToken(String sourceFilePath);

  TokensResult buildToken(InputStream byteInputStream);
}
