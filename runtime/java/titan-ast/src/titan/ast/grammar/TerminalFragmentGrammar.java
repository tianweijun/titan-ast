package titan.ast.grammar;

/**
 * TerminalFragmentGrammar.
 *
 * @author tian wei jun
 */
public class TerminalFragmentGrammar extends Grammar {

  public TerminalFragmentGrammar(String name) {
    super(name);
    this.type = GrammarType.TERMINAL_FRAGMENT;
  }

  @Override
  public int compareTo(Grammar that) {
    if (type != that.type) {
      return type.ordinal() - that.type.ordinal();
    }
    return name.compareTo(that.name);
  }
}
