package titan.ast.grammar;

/**
 * NonterminaltGrammar.
 *
 * @author tian wei jun
 */
public class NonterminalGrammar extends Grammar {

  public NonterminalGrammar(String name) {
    super(name);
    this.type = GrammarType.NONTERMINAL;
  }

  @Override
  public int compareTo(Grammar that) {
    if (type != that.type) {
      return type.ordinal() - that.type.ordinal();
    }
    return name.compareTo(that.name);
  }
}
