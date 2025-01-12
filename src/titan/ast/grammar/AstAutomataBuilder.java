package titan.ast.grammar;

import titan.ast.grammar.syntax.AstAutomata;
import titan.ast.grammar.syntax.DfaAstAutomataBuilder;
import titan.ast.grammar.token.TokenAutomataBuilder;

/**
 * 建造器：生成抽象语法树的自动机.
 *
 * @author tian wei jun
 */
public class AstAutomataBuilder {

  public AstAutomata build() {
    // token自动机
    TokenAutomataBuilder tokenAutomataBuilder = new TokenAutomataBuilder();
    tokenAutomataBuilder.build();
    // 语法自动机
    DfaAstAutomataBuilder dfaAstAutomataBuilder = new DfaAstAutomataBuilder();
    return dfaAstAutomataBuilder.build();
  }
}
