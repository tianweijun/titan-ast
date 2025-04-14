package titan.ast.fa.syntax;

import java.util.List;
import titan.ast.runtime.Ast;
import titan.ast.runtime.Token;

/**
 * ast的自动机.
 *
 * @author tian wei jun
 */
public interface AstAutomata {

  AstAutomataType getType();

  Ast buildAst(List<Token> sourceTokens);
}
