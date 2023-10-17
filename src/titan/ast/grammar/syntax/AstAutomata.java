package titan.ast.grammar.syntax;

import java.util.List;
import titan.ast.target.Ast;
import titan.ast.target.Token;

/**
 * ast的自动机.
 *
 * @author tian wei jun
 */
public interface AstAutomata {

  Ast buildAst(List<Token> sourceTokens);

  List<Ast> buildAsts(List<Token> sourceTokens);
}
