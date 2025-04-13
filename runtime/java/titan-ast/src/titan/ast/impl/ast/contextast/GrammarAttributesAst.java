package titan.ast.impl.ast.contextast;

import java.util.Set;
import titan.ast.grammar.GrammarAttribute;

public class GrammarAttributesAst extends NonterminalContextAst {

  public Set<GrammarAttribute> grammarAttributes;

  @Override
  public void accept(Visitor visitor) {
    visitor.visitGrammarAttributesAst(this);
  }
}