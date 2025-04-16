package titan.ast.impl.ast.automata;

import titan.ast.grammar.LanguageGrammar;

class AstGrammarInitializer {
  LanguageGrammar languageGrammar;

  AstGrammarInitializer(LanguageGrammar languageGrammar) {
    this.languageGrammar = languageGrammar;
  }

  void init() {
    new TerminalFragmentGrammarInitializer(languageGrammar).init();
    new TerminalGrammarInitializer(languageGrammar).init();
    new DerivedTerminalGrammarInitializer(languageGrammar).init();
    new NonterminalGrammarInitializer(languageGrammar).init();
  }
}
