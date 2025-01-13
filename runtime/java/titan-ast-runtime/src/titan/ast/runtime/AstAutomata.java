package titan.ast.runtime;

import java.util.ArrayList;
import titan.ast.runtime.AstGeneratorResult.AstResult;

/**
 * ast的自动机.
 *
 * @author tian wei jun
 */
public interface AstAutomata {
  AstAutomataType getType();

  AstResult buildAst(ArrayList<Token> sourceTokens);
}
