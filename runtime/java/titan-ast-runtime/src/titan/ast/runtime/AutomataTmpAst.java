package titan.ast.runtime;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 抽象语法树.
 *
 * @author tian wei jun
 */
public class AutomataTmpAst {
  public Grammar grammar = null;
  public String alias = "";
  // grammar.type == GrammarType.TERMINAL
  public Token token = null;

  public LinkedList<AutomataTmpAst> children = new LinkedList<>();

  public AutomataTmpAst() {}

  public AutomataTmpAst(Grammar grammar, String alias) {
    this.grammar = grammar;
    this.alias = alias;
  }

  public AutomataTmpAst(Token token) {
    this.grammar = token.terminal;
    this.token = token;
  }

  public AutomataTmpAst diyClone() {
    AutomataTmpAst ast = new AutomataTmpAst();
    ast.grammar = this.grammar;
    ast.alias = this.alias;
    ast.token = this.token;
    for (AutomataTmpAst thisChild : this.children) {
      ast.children.add(thisChild.diyClone());
    }
    return ast;
  }

  public Ast toAst() {
    Ast ast = new Ast();
    ast.grammar = this.grammar.toAstGrammar();
    ast.alias = this.alias;
    if (null != this.token) {
      ast.token = new AstToken(this.token.start, this.token.text);
    }
    ArrayList<Ast> astChildren = new ArrayList<>(this.children.size());
    for (AutomataTmpAst thisChild : this.children) {
      astChildren.add(thisChild.toAst());
    }
    ast.children = astChildren;
    return ast;
  }

  @Override
  public String toString() {
    String displayString = "";
    GrammarType type = grammar.type;
    if (type == GrammarType.TERMINAL) {
      displayString = token.text;
    }
    if (type == GrammarType.NONTERMINAL) {
      if (StringUtils.isNotBlank(alias)) {
        displayString = grammar.name + "[" + alias + "]";
      } else {
        displayString = grammar.name;
      }
    }
    return displayString;
  }
}
