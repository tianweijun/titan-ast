package titan.ast.runtime;

import java.util.LinkedList;
import java.util.Objects;

/**
 * 抽象语法树.
 *
 * @author tian wei jun
 */
public class Ast {
  public Grammar grammar = null;
  public String alias = "";
  // grammar.type == GrammarType.TERMINAL
  public AstToken token = null;

  public LinkedList<Ast> children = new LinkedList<>();

  public Ast() {}

  public Ast(Grammar grammar, String alias) {
    this.grammar = grammar;
    this.alias = alias;
  }

  public Ast(Token token) {
    this.grammar = token.terminal;
    this.token = new AstToken(token.start, token.text);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Ast ast = (Ast) o;
    return grammar.equals(ast.grammar)
        && Objects.equals(alias, ast.alias)
        && Objects.equals(token, ast.token)
        && children.equals(ast.children);
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
}
