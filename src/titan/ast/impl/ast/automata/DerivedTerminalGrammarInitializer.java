package titan.ast.impl.ast.automata;

import titan.ast.grammar.DerivedTerminalGrammarAutomataDetail;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.PrimaryGrammarContent.RegExpPrimaryGrammarContent;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.grammar.regexp.AndCompositeRegExp;
import titan.ast.grammar.regexp.OrCompositeRegExp;
import titan.ast.grammar.regexp.SequenceCharsRegExp;

class DerivedTerminalGrammarInitializer {
  LanguageGrammar languageGrammar;
  DerivedTerminalGrammarAutomataDetail.RootTerminalGrammarMapDetail rootTerminalGrammarMapDetail;

  DerivedTerminalGrammarInitializer(LanguageGrammar languageGrammar) {
    this.languageGrammar = languageGrammar;
  }

  /*
  @DerivedTerminalGrammar derive(Identifier) begin ;

  Begin : 'begin' ;
  End : 'end' ;
  Skip : 'skip' ;

  @DerivedTerminalGrammar end ;
     */
  void init() {
    rootTerminalGrammarMapDetail = languageGrammar.getRootTerminalGrammarMap("Identifier");
    initBegin();
    initEnd();
    initSkip();
  }

  // Begin : 'begin' ;
  private void initBegin() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "Begin";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new SequenceCharsRegExp("begin")));
    rootTerminalGrammarMapDetail.addTerminalGrammar(createDerivedTerminalGrammar(grammarContent));
  }

  // End : 'end' ;
  private void initEnd() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "End";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new SequenceCharsRegExp("end")));
    rootTerminalGrammarMapDetail.addTerminalGrammar(createDerivedTerminalGrammar(grammarContent));
  }

  // Skip : 'skip' ;
  private void initSkip() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "Skip";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new SequenceCharsRegExp("skip")));
    rootTerminalGrammarMapDetail.addTerminalGrammar(createDerivedTerminalGrammar(grammarContent));
  }

  TerminalGrammar createDerivedTerminalGrammar(
      RegExpPrimaryGrammarContent regExpPrimaryGrammarContent) {
    TerminalGrammar terminalGrammar = new TerminalGrammar(regExpPrimaryGrammarContent.grammarName);
    terminalGrammar.primaryGrammarContent = regExpPrimaryGrammarContent;
    return terminalGrammar;
  }
}
