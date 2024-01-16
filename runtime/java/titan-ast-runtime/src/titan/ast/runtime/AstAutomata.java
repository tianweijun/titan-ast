package titan.ast.runtime;

import java.util.List;

/**
 * ast的自动机.
 *
 * @author tian wei jun
 */
public interface AstAutomata {
  AstAutomataType getType();

  Ast buildAst(List<Token> sourceTokens);

  List<Ast> buildAsts(List<Token> sourceTokens);
}
