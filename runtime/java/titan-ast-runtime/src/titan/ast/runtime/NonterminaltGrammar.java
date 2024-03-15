package titan.ast.runtime;

/**
 * NonterminaltGrammar.
 *
 * @author tian wei jun
 */
public class NonterminaltGrammar extends Grammar {

  public NonterminaltGrammar(int index) {
    super(index);
    this.type = GrammarType.NONTERMINAL;
  }

  @Override
  public int compareTo(Grammar that) {
    return this.index - that.index;
  }
}
