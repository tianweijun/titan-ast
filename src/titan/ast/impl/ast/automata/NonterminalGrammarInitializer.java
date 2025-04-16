package titan.ast.impl.ast.automata;

import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.NonterminalGrammar;
import titan.ast.grammar.PrimaryGrammarContent.RegExpPrimaryGrammarContent;
import titan.ast.grammar.regexp.*;

class NonterminalGrammarInitializer {
  LanguageGrammar languageGrammar;

  NonterminalGrammarInitializer(LanguageGrammar languageGrammar) {
    this.languageGrammar = languageGrammar;
  }

  void init() {
    languageGrammar.updateStartGrammarName("compilationUnit");
    initStartGrammar();
    initGrammarUnitRegExp();
    initSequenceCharsUnitRegExp();
    initOneCharOptionCharsetUnitRegExp();
    initParenthesisUnitRegExp();
    initUnitRegExp();
    initAndCompositeRegExp();
    initExclusiveOrCompositeRegExp();
    initInclusiveOrCompositeRegExp();
    initGrammarAttribute();
    initGrammarAttributes();
    initGrammarAction();
    initRegExpGrammar();
    initTerminalGrammarBeginning();
    initTerminalGrammarEnd();
    initNfaTerminalGrammar();
    initTerminalGrammar();
    initTerminalGrammarBlock();
    initTerminalFragmentGrammarBeginning();
    initTerminalFragmentGrammarEnd();
    initTerminalFragmentGrammarBlock();
    initDerivedTerminalGrammarBeginning();
    initDerivedTerminalGrammarEnd();
    initDerivedTerminalGrammarBlock();
    initNonterminalGrammarBeginning();
    initNonterminalGrammarEnd();
    initNonterminalGrammarBlock();
    initItem();
    initCompilationUnit();
    initIdentifier();
  }

  // startGrammar : '@StartGrammar' identifier ';'  ;
  private void initStartGrammar() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "startGrammar";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("@StartGrammar"),
                new GrammarRegExp("identifier"),
                new SequenceCharsRegExp(";")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // grammarUnitRegExp : identifier | GrammarUnitRegExpRepeatTimes ;
  private void initGrammarUnitRegExp() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "grammarUnitRegExp";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("identifier")),
            new AndCompositeRegExp(new GrammarRegExp("GrammarUnitRegExpRepeatTimes")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // sequenceCharsUnitRegExp : SequenceCharsUnitRegExp ;
  private void initSequenceCharsUnitRegExp() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "sequenceCharsUnitRegExp";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new GrammarRegExp("SequenceCharsUnitRegExp")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // oneCharOptionCharsetUnitRegExp : OneCharOptionCharsetUnitRegExp ;
  private void initOneCharOptionCharsetUnitRegExp() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "oneCharOptionCharsetUnitRegExp";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("OneCharOptionCharsetUnitRegExp")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  /*
    parenthesisUnitRegExp :
    ParenthesisUnitRegExpPrefix inclusiveOrCompositeRegExp ParenthesisUnitRegExpSuffix
  ;
     */
  private void initParenthesisUnitRegExp() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "parenthesisUnitRegExp";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp("ParenthesisUnitRegExpPrefix"),
                new GrammarRegExp("inclusiveOrCompositeRegExp"),
                new GrammarRegExp("ParenthesisUnitRegExpSuffix")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  /*
    unitRegExp :
        grammarUnitRegExp
      | sequenceCharsUnitRegExp
      | oneCharOptionCharsetUnitRegExp
      | parenthesisUnitRegExp
  ;
     */
  private void initUnitRegExp() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "unitRegExp";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("grammarUnitRegExp")),
            new AndCompositeRegExp(new GrammarRegExp("sequenceCharsUnitRegExp")),
            new AndCompositeRegExp(new GrammarRegExp("oneCharOptionCharsetUnitRegExp")),
            new AndCompositeRegExp(new GrammarRegExp("parenthesisUnitRegExp")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // andCompositeRegExp : unitRegExp+ ;
  private void initAndCompositeRegExp() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "andCompositeRegExp";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp(
                    RepeatTimes.numberTimes(1), RepeatTimes.infinityTimes(), "unitRegExp")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // exclusiveOrCompositeRegExp : andCompositeRegExp AndCompositeRegExpAlias? ;
  private void initExclusiveOrCompositeRegExp() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "exclusiveOrCompositeRegExp";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp("andCompositeRegExp"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0),
                    RepeatTimes.numberTimes(1),
                    "AndCompositeRegExpAlias")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  /*
    inclusiveOrCompositeRegExp :
    exclusiveOrCompositeRegExp
    | inclusiveOrCompositeRegExp '|' exclusiveOrCompositeRegExp
  ;
     */
  private void initInclusiveOrCompositeRegExp() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "inclusiveOrCompositeRegExp";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("exclusiveOrCompositeRegExp")),
            new AndCompositeRegExp(
                new GrammarRegExp("inclusiveOrCompositeRegExp"),
                new SequenceCharsRegExp("|"),
                new GrammarRegExp("exclusiveOrCompositeRegExp")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  /*
    grammarAttribute :
    NfaTerminalGrammarAttribute | LazinessTerminalGrammarAttribute
  ;
     */
  private void initGrammarAttribute() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "grammarAttribute";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("NfaTerminalGrammarAttribute")),
            new AndCompositeRegExp(new GrammarRegExp("LazinessTerminalGrammarAttribute")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // grammarAttributes : grammarAttribute+ ;
  private void initGrammarAttributes() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "grammarAttributes";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp(
                    RepeatTimes.numberTimes(1), RepeatTimes.infinityTimes(), "grammarAttribute")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // grammarAction :  Arrow Skip ;
  private void initGrammarAction() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "grammarAction";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("Arrow"), new GrammarRegExp("Skip")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  /*
    regExpGrammar :
     identifier grammarAttributes? ':' inclusiveOrCompositeRegExp grammarAction? ';'
  ;
     */
  private void initRegExpGrammar() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "regExpGrammar";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp("identifier"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.numberTimes(1), "grammarAttributes"),
                new SequenceCharsRegExp(":"),
                new GrammarRegExp("inclusiveOrCompositeRegExp"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.numberTimes(1), "grammarAction"),
                new SequenceCharsRegExp(";")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // terminalGrammarBeginning : '@TerminalGrammar' 'begin' ';' ;
  private void initTerminalGrammarBeginning() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "terminalGrammarBeginning";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("@TerminalGrammar"),
                new SequenceCharsRegExp("begin"),
                new SequenceCharsRegExp(";")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // terminalGrammarEnd : '@TerminalGrammar' 'end' ';' ;
  private void initTerminalGrammarEnd() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "terminalGrammarEnd";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("@TerminalGrammar"),
                new SequenceCharsRegExp("end"),
                new SequenceCharsRegExp(";")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  /*
    nfaTerminalGrammar :
   identifier grammarAttributes? ':' NfaEdge+ grammarAction? ';'
  ;
     */
  private void initNfaTerminalGrammar() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "nfaTerminalGrammar";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp("identifier"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.numberTimes(1), "grammarAttributes"),
                new SequenceCharsRegExp(":"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(1), RepeatTimes.infinityTimes(), "NfaEdge"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.numberTimes(1), "grammarAction"),
                new SequenceCharsRegExp(";")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  /*
    terminalGrammar :
     regExpGrammar | nfaTerminalGrammar
  ;
     */
  private void initTerminalGrammar() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "terminalGrammar";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("regExpGrammar")),
            new AndCompositeRegExp(new GrammarRegExp("nfaTerminalGrammar")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  /*
    terminalGrammarBlock :
     terminalGrammarBeginning
     terminalGrammar*
     terminalGrammarEnd
  ;
     */
  private void initTerminalGrammarBlock() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "terminalGrammarBlock";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp("terminalGrammarBeginning"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.infinityTimes(), "terminalGrammar"),
                new GrammarRegExp("terminalGrammarEnd")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // terminalFragmentGrammarBeginning : '@TerminalFragmentGrammar' 'begin' ';' ;
  private void initTerminalFragmentGrammarBeginning() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "terminalFragmentGrammarBeginning";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("@TerminalFragmentGrammar"),
                new SequenceCharsRegExp("begin"),
                new SequenceCharsRegExp(";")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // terminalFragmentGrammarEnd : '@TerminalFragmentGrammar' 'end' ';' ;
  private void initTerminalFragmentGrammarEnd() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "terminalFragmentGrammarEnd";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("@TerminalFragmentGrammar"),
                new SequenceCharsRegExp("end"),
                new SequenceCharsRegExp(";")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  /*
  terminalFragmentGrammarBlock :
     terminalFragmentGrammarBeginning
     terminalGrammar*
     terminalFragmentGrammarEnd
  ;
     */
  private void initTerminalFragmentGrammarBlock() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "terminalFragmentGrammarBlock";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp("terminalFragmentGrammarBeginning"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.infinityTimes(), "terminalGrammar"),
                new GrammarRegExp("terminalFragmentGrammarEnd")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // derivedTerminalGrammarBeginning : '@DerivedTerminalGrammar' DerivedTerminalGrammarAttribute
  // 'begin' ';' ;
  private void initDerivedTerminalGrammarBeginning() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "derivedTerminalGrammarBeginning";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("@DerivedTerminalGrammar"),
                new GrammarRegExp("DerivedTerminalGrammarAttribute"),
                new SequenceCharsRegExp("begin"),
                new SequenceCharsRegExp(";")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // derivedTerminalGrammarEnd : '@DerivedTerminalGrammar' 'end' ';' ;
  private void initDerivedTerminalGrammarEnd() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "derivedTerminalGrammarEnd";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("@DerivedTerminalGrammar"),
                new SequenceCharsRegExp("end"),
                new SequenceCharsRegExp(";")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  /*
    derivedTerminalGrammarBlock :
     derivedTerminalGrammarBeginning
     regExpGrammar*  // regExpGrammar:sequenceCharsUnitRegExp ('|' sequenceCharsUnitRegExp)*
     derivedTerminalGrammarEnd
  ;
     */
  private void initDerivedTerminalGrammarBlock() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "derivedTerminalGrammarBlock";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp("derivedTerminalGrammarBeginning"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.infinityTimes(), "regExpGrammar"),
                new GrammarRegExp("derivedTerminalGrammarEnd")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // nonterminalGrammarBeginning : '@NonterminalGrammar' 'begin' ';' ;
  private void initNonterminalGrammarBeginning() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "nonterminalGrammarBeginning";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("@NonterminalGrammar"),
                new SequenceCharsRegExp("begin"),
                new SequenceCharsRegExp(";")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // nonterminalGrammarEnd : '@NonterminalGrammar' 'end' ';' ;
  private void initNonterminalGrammarEnd() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "nonterminalGrammarEnd";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("@NonterminalGrammar"),
                new SequenceCharsRegExp("end"),
                new SequenceCharsRegExp(";")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  /*
    nonterminalGrammarBlock :
     nonterminalGrammarBeginning
     regExpGrammar*
     nonterminalGrammarEnd
  ;
     */
  private void initNonterminalGrammarBlock() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "nonterminalGrammarBlock";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp("nonterminalGrammarBeginning"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.infinityTimes(), "regExpGrammar"),
                new GrammarRegExp("nonterminalGrammarEnd")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  /*
    item :
    startGrammar
    | terminalFragmentGrammarBlock
    | terminalGrammarBlock
    | derivedTerminalGrammarBlock
    | nonterminalGrammarBlock
  ;
     */
  private void initItem() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "item";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("startGrammar")),
            new AndCompositeRegExp(new GrammarRegExp("terminalFragmentGrammarBlock")),
            new AndCompositeRegExp(new GrammarRegExp("terminalGrammarBlock")),
            new AndCompositeRegExp(new GrammarRegExp("derivedTerminalGrammarBlock")),
            new AndCompositeRegExp(new GrammarRegExp("nonterminalGrammarBlock")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // compilationUnit : item+ ;
  private void initCompilationUnit() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "compilationUnit";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp(
                    RepeatTimes.numberTimes(1), RepeatTimes.infinityTimes(), "item")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  // identifier : Identifier | Begin | End | Skip ;
  private void initIdentifier() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "identifier";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("Identifier")),
            new AndCompositeRegExp(new GrammarRegExp("Begin")),
            new AndCompositeRegExp(new GrammarRegExp("End")),
            new AndCompositeRegExp(new GrammarRegExp("Skip")));
    languageGrammar.addNonterminalGrammar(createNonterminalGrammar(grammarContent));
  }

  NonterminalGrammar createNonterminalGrammar(
      RegExpPrimaryGrammarContent regExpPrimaryGrammarContent) {
    NonterminalGrammar nonterminalGrammar =
        new NonterminalGrammar(regExpPrimaryGrammarContent.grammarName);
    nonterminalGrammar.primaryGrammarContent = regExpPrimaryGrammarContent;
    return nonterminalGrammar;
  }
}
