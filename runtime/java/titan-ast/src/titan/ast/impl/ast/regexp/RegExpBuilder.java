package titan.ast.impl.ast.regexp;

import titan.ast.impl.ast.contextast.AbstractVisitor;
import titan.ast.impl.ast.contextast.CompilationUnitAst;
import titan.ast.impl.ast.contextast.ContextAst;

/**
 * .
 *
 * @author tian wei jun
 */
public class RegExpBuilder extends AbstractVisitor {

  final CompilationUnitAst compilationUnitAst;

  public RegExpBuilder(ContextAst contextAst) {
    compilationUnitAst = (CompilationUnitAst) contextAst;
  }

  public void build() {
    visitCompilationUnitAst(compilationUnitAst);
  }
}
