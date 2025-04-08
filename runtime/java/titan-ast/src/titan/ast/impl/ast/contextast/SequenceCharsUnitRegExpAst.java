package titan.ast.impl.ast.contextast;

public class SequenceCharsUnitRegExpAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitSequenceCharsUnitRegExpAst(this);
  }
}