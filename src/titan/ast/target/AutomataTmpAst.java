package titan.ast.target;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.GrammarType;
import titan.ast.util.StringUtils;

/**
 * 抽象语法树.
 *
 * @author tian wei jun
 */
public class AutomataTmpAst implements Comparable<AutomataTmpAst> {

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
    ast.grammar = this.grammar;
    ast.alias = this.alias;
    if (null != this.token) {
      ast.token = new AstToken(this.token.start, this.token.text);
    }
    for (AutomataTmpAst thisChild : this.children) {
      ast.children.add(thisChild.toAst());
    }
    return ast;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AutomataTmpAst tmpAst = (AutomataTmpAst) o;
    return grammar.equals(tmpAst.grammar)
        && Objects.equals(alias, tmpAst.alias)
        && Objects.equals(token, tmpAst.token)
        && children.equals(tmpAst.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(grammar, alias, token, children);
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

  @Override
  public int compareTo(AutomataTmpAst that) {
    int compare = grammar.compareTo(that.grammar);
    if (0 != compare) {
      return compare;
    }
    compare = alias.compareTo(that.alias);
    if (0 != compare) {
      return compare;
    }
    if (token == null && that.token != null) {
      return -1;
    }
    if (token != null && that.token == null) {
      return 1;
    }
    if (token != null) {
      compare = token.compareTo(that.token);
      if (0 != compare) {
        return compare;
      }
    }
    // children
    if (children.size() != that.children.size()) {
      return children.size() - that.children.size();
    }
    Iterator<AutomataTmpAst> thisChildrenIt = children.iterator();
    Iterator<AutomataTmpAst> thatChildrenIt = that.children.iterator();
    while (thisChildrenIt.hasNext()) {
      AutomataTmpAst thisChild = thisChildrenIt.next();
      AutomataTmpAst thatChild = thatChildrenIt.next();
      compare = thisChild.compareTo(thatChild);
      if (0 != compare) {
        return compare;
      }
    }
    return 0;
  }
}
