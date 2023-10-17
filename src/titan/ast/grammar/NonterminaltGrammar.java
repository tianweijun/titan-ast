package titan.ast.grammar;

/**
 * NonterminaltGrammar.
 *
 * @author tian wei jun
 */
public class NonterminaltGrammar extends Grammar {

  public NonterminaltGrammar(String name) {
    super(name);
    this.type = GrammarType.NONTERMINAL;
  }
}
