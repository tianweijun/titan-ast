package titan.ast.grammar;


/**
 * 语法：包括终结符、终结符片段、非终结符.
 *
 * @author tian wei jun
 */
public abstract class Grammar implements Comparable<Grammar> {

  public final GrammarType type;
  public final String name;

  // transient
  public PrimaryGrammarContent primaryGrammarContent;

  public Grammar(GrammarType type, String name) {
    this.type = type;
    this.name = name;
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
    int result = type.hashCode();
    result = 31 * result + name.hashCode();
    return result;
  }

  @Override
  public int compareTo(Grammar that) {
    if (type != that.type) {
      return type.ordinal() - that.type.ordinal();
    }
    return name.compareTo(that.name);
  }
}
