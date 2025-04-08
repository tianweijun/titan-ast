package titan.ast.impl.ast.contextast;

public class CompilationUnitAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitCompilationUnitAst(this);
  }
}