package titan.ast.impl.ast.contextast;

import java.util.ArrayList;
import titan.ast.runtime.AstGrammar;

public abstract class ContextAst implements ParseTree {

  public AstGrammar grammar = null;
  public ArrayList<ContextAst> children = new ArrayList<>();

  public ContextAst parent = null;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitChildren(this);
  }
}