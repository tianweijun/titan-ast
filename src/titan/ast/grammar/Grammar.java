package titan.ast.grammar;

import java.util.LinkedList;
import java.util.Objects;
import titan.ast.grammar.io.GrammarToken;
import titan.ast.grammar.regexp.RegExp;

/**
 * 语法：包括终结符、终结符片段、非终结符.
 *
 * @author tian wei jun
 */
public abstract class Grammar implements Comparable<Grammar> {

  public GrammarType type = GrammarType.TERMINAL;
  public String name = "";
  public GrammarAction action = GrammarAction.TEXT;
  // 最顶层是作为wrapper的COMPOSITE正则
  public RegExp regExp = new RegExp(null);
  public LinkedList<GrammarToken> attributes = new LinkedList<>();
  // token of regExp content
  public LinkedList<GrammarToken> text = new LinkedList<>();

  public Grammar(String name) {
    this.name = name;
  }

  public boolean isNormalRegexpContent() {
    return GrammarAttribute.isNormalRegexpAttribute(attributes);
  }

  public boolean isNfaRegexpContent() {
    return GrammarAttribute.isNfaRegexpAttribute(attributes);
  }

  public GrammarToken getNfaRegexpAttributeToken() {
    return GrammarAttribute.getNfaRegexpAttributeToken(attributes);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Grammar grammar = (Grammar) o;
    return type == grammar.type && name.equals(grammar.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name);
  }

  @Override
  public int compareTo(Grammar that) {
    if (type != that.type) {
      return type.ordinal() - that.type.ordinal();
    }
    return name.compareTo(that.name);
  }

  @Override
  public String toString() {
    String info = name;
    if (null == info) {
      info = new String(regExp.text);
    }
    return info;
  }

  public boolean isNonterminal() {
    return type == GrammarType.NONTERMINAL;
  }

  public boolean isTerminal() {
    return type == GrammarType.TERMINAL;
  }
}
