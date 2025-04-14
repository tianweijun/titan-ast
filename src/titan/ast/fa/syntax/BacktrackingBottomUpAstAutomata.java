package titan.ast.fa.syntax;

import java.util.List;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.Grammar;
import titan.ast.runtime.Ast;
import titan.ast.runtime.Token;

/**
 * 按照可回溯方式构造ast的自动机,只保留依赖的数据，真正的实现在runtime里面.
 *
 * @author tian wei jun
 */
public class BacktrackingBottomUpAstAutomata implements AstAutomata {
  public SyntaxDfa astDfa = null;
  public Grammar startGrammar = null;

  public BacktrackingBottomUpAstAutomata(SyntaxDfa astDfa, Grammar startGrammar) {
    this.astDfa = astDfa;
    this.startGrammar = startGrammar;
  }

  @Override
  public AstAutomataType getType() {
    return AstAutomataType.BACKTRACKING_BOTTOM_UP_AST_AUTOMATA;
  }

  /**
   * 按照所有可能的产生式归约.
   *
   * @param sourceTokens token数据流
   * @return
   */
  @Override
  public Ast buildAst(List<Token> sourceTokens) {
    throw new AstRuntimeException("not support,implement in runtime");
  }
}
