package titan.ast.grammar.regexp;

import titan.ast.grammar.LanguageGrammar;

/**
 * LanguageGrammarRegExpBuilder,after textOfGrammarBuilder.build().
 *
 * @author tian wei jun
 */
public class LanguageGrammarRegExpBuilder {
  LanguageGrammar languageGrammar;

  public LanguageGrammarRegExpBuilder(LanguageGrammar languageGrammar) {
    this.languageGrammar = languageGrammar;
  }

  public void buildRegExpOfNonterminal() {
    SyntaxRegExpBuilder syntaxRegExpBuilder = new SyntaxRegExpBuilder();
    syntaxRegExpBuilder.addTasks(languageGrammar.nonterminals);
    syntaxRegExpBuilder.addSources(languageGrammar.nonterminals);
    syntaxRegExpBuilder.addSources(languageGrammar.terminals);
    syntaxRegExpBuilder.build();
  }

  public void buildRegExpOfTerminal() {
    TokenRegExpBuilder regExpBuilder = new TokenRegExpBuilder();
    regExpBuilder.addTasks(languageGrammar.terminals);
    regExpBuilder.addSources(languageGrammar.terminals);
    regExpBuilder.addSources(languageGrammar.terminalFragments);
    regExpBuilder.build();
  }

  public void buildRegExpOfFragment() {
    TokenRegExpBuilder regExpBuilder = new TokenRegExpBuilder();
    regExpBuilder.addTasks(languageGrammar.terminalFragments);
    regExpBuilder.addSources(languageGrammar.terminalFragments);

    regExpBuilder.build();
  }
}
