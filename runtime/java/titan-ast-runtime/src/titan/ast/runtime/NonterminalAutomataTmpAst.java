package titan.ast.runtime;

import java.util.ArrayList;

/**
 * .
 *
 * @author tian wei jun
 */
class NonterminalAutomataTmpAst extends AutomataTmpAst {
  final String alias;

  public NonterminalAutomataTmpAst(Grammar grammar, String alias) {
    super(grammar);
    this.alias = alias;
  }

  public NonterminalAutomataTmpAst cloneForAstAutomata() {
    NonterminalAutomataTmpAst ast = new NonterminalAutomataTmpAst(grammar, alias);
    for (AutomataTmpAst thisChild : this.children) {
      ast.children.add(thisChild.cloneForAstAutomata());
    }
    return ast;
  }

  @Override
  Ast toAst() {
    NonterminalAst ast = new NonterminalAst(this.grammar.toAstGrammar(), this.alias);
    ast.children = new ArrayList<>(this.children.size());
    for (AutomataTmpAst automataTmpAstChild : this.children) {
      ast.children.add(automataTmpAstChild.toAst());
    }
    return ast;
  }

  @Override
  public String toString() {
    String displayString = "";
    if (StringUtils.isNotBlank(alias)) {
      displayString = grammar.name + "[" + alias + "]";
    } else {
      displayString = grammar.name;
    }
    return displayString;
  }
}
