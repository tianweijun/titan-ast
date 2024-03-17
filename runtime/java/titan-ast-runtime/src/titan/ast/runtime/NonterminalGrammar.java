package titan.ast.runtime;

/**
 * NonterminaltGrammar.
 *
 * @author tian wei jun
 */
public class NonterminalGrammar extends Grammar {

  public NonterminalGrammar(int index) {
    super(index);
    this.type = GrammarType.NONTERMINAL;
  }
}
