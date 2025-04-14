package titan.ast.impl.ast.contextast;

import titan.ast.grammar.regexp.SequenceCharsRegExp;

public class SequenceCharsUnitRegExpAst extends NonterminalContextAst {

  public SequenceCharsRegExp sequenceCharsRegExp;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitSequenceCharsUnitRegExpAst(this);
  }
}