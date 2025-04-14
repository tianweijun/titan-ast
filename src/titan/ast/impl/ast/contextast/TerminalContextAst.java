package titan.ast.impl.ast.contextast;

import titan.ast.runtime.AstToken;

public class TerminalContextAst extends ContextAst {

  public AstToken token;
  public String str;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitTerminalContextAst(this);
  }
}