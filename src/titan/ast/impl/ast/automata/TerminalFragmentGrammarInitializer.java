package titan.ast.impl.ast.automata;

import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.PrimaryGrammarContent;
import titan.ast.grammar.PrimaryGrammarContent.RegExpPrimaryGrammarContent;
import titan.ast.grammar.TerminalFragmentGrammar;
import titan.ast.grammar.regexp.*;
import titan.ast.grammar.regexp.OneCharOptionCharsetRegExp.OptionChar;

class TerminalFragmentGrammarInitializer {
  LanguageGrammar languageGrammar;

  TerminalFragmentGrammarInitializer(LanguageGrammar languageGrammar) {
    this.languageGrammar = languageGrammar;
  }

  void init() {
    initIdentifierFragment();
    initIdentifierNondigit();
    initDigit();
    initNaturalNumber();
    initHexadecimalDigit();
    initHexadecimalEscapeChar();
    initCharForSequenceChars();
    initOptionCharNonhyphens();
    initCharForOneCharOptionCharset();
    initRepeatTimes();
    initGrammarUnitRegExpRepeatTimesFragment();
    initSequenceCharsUnitRegExpFragment();
    initOneCharOptionCharsetUnitRegExpFragment();
    initParenthesisUnitRegExpSuffixFragment();
    initNfaTerminalGrammarAttributeFragment();
    initLazinessTerminalGrammarAttributeFragment();
    initDerivedTerminalGrammarAttributeFragment();
    initNfaEdgeFragment();
    initAndCompositeRegExpAliasFragment();
  }

  // IdentifierFragment : IdentifierNondigit  ( IdentifierNondigit | Digit )* ;
  private void initIdentifierFragment() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "IdentifierFragment";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp("IdentifierNondigit"),
                new ParenthesisRegExp(
                    RepeatTimes.numberTimes(0),
                    RepeatTimes.infinityTimes(),
                    new OrCompositeRegExp(
                        new AndCompositeRegExp(new GrammarRegExp("IdentifierNondigit")),
                        new AndCompositeRegExp(new GrammarRegExp("Digit"))))));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  // IdentifierNondigit : [a-zA-Z_] ;
  private void initIdentifierNondigit() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "IdentifierNondigit";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new OneCharOptionCharsetRegExp(
                    new OptionChar('a', 'z'), new OptionChar('A', 'Z'), new OptionChar('_'))));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  // Digit : [0-9] ;
  private void initDigit() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "Digit";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new OneCharOptionCharsetRegExp(new OptionChar('0', '9'))));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  // NaturalNumber : Digit+ ;
  private void initNaturalNumber() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "NaturalNumber";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp(
                    RepeatTimes.numberTimes(1), RepeatTimes.infinityTimes(), "Digit")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  // HexadecimalDigit : [0-9] | [a-f] | [A-F] ;
  private void initHexadecimalDigit() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "HexadecimalDigit";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new OneCharOptionCharsetRegExp(new OptionChar('0', '9'))),
            new AndCompositeRegExp(new OneCharOptionCharsetRegExp(new OptionChar('a', 'f'))),
            new AndCompositeRegExp(new OneCharOptionCharsetRegExp(new OptionChar('A', 'F'))));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  // HexadecimalEscapeChar : '\\' [xX] HexadecimalDigit{1,2} ;
  private void initHexadecimalEscapeChar() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "HexadecimalEscapeChar";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("\\"),
                new OneCharOptionCharsetRegExp(new OptionChar('x'), new OptionChar('X')),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(1), RepeatTimes.numberTimes(2), "HexadecimalDigit")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  /*
   CharForSequenceChars :
          ~['\\]  // '39\92
          | HexadecimalEscapeChar
          | '\\' ['0\\abfnrstv]
  ;
  */
  private void initCharForSequenceChars() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "CharForSequenceChars";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new OneCharOptionCharsetRegExp(
                    new OptionChar(0, 38), new OptionChar(40, 91), new OptionChar(93, 255))),
            new AndCompositeRegExp(new GrammarRegExp("HexadecimalEscapeChar")),
            new AndCompositeRegExp(
                new SequenceCharsRegExp("\\"),
                new OneCharOptionCharsetRegExp(
                    new OptionChar('\''),
                    new OptionChar('0'),
                    new OptionChar('\\'),
                    new OptionChar('a', 'b'),
                    new OptionChar('f'),
                    new OptionChar('n'),
                    new OptionChar('r', 't'),
                    new OptionChar('v'))));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  /*
    OptionCharNonhyphens :
         ~[\-\\\]] //  -45 \92 ]93
         | HexadecimalEscapeChar
         | '\\' [\-\]0\\abfnrstv]
  ;
     */
  private void initOptionCharNonhyphens() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "OptionCharNonhyphens";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new OneCharOptionCharsetRegExp(
                    new OptionChar(0, 44), new OptionChar(46, 91), new OptionChar(94, 255))),
            new AndCompositeRegExp(new GrammarRegExp("HexadecimalEscapeChar")),
            new AndCompositeRegExp(
                new SequenceCharsRegExp("\\"),
                new OneCharOptionCharsetRegExp(
                    new OptionChar('-'),
                    new OptionChar(']'),
                    new OptionChar('0'),
                    new OptionChar('\\'),
                    new OptionChar('a', 'b'),
                    new OptionChar('f'),
                    new OptionChar('n'),
                    new OptionChar('r', 't'),
                    new OptionChar('v'))));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  /*
    CharForOneCharOptionCharset :
      OptionCharNonhyphens
      | OptionCharNonhyphens '-' OptionCharNonhyphens
  ;
     */
  private void initCharForOneCharOptionCharset() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "CharForOneCharOptionCharset";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("OptionCharNonhyphens")),
            new AndCompositeRegExp(
                new GrammarRegExp("OptionCharNonhyphens"),
                new SequenceCharsRegExp("-"),
                new GrammarRegExp("OptionCharNonhyphens")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  /*
  RepeatTimes :
        '?'
        | '*'
        | '+'
        | '{' NaturalNumber '}'
        | '{' NaturalNumber? ',' NaturalNumber? '}'
  ;
       */
  private void initRepeatTimes() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "RepeatTimes";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new SequenceCharsRegExp("?")),
            new AndCompositeRegExp(new SequenceCharsRegExp("*")),
            new AndCompositeRegExp(new SequenceCharsRegExp("+")),
            new AndCompositeRegExp(
                new SequenceCharsRegExp("{"),
                new GrammarRegExp("NaturalNumber"),
                new SequenceCharsRegExp("}")),
            new AndCompositeRegExp(
                new SequenceCharsRegExp("{"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.numberTimes(1), "NaturalNumber"),
                new SequenceCharsRegExp(","),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.numberTimes(1), "NaturalNumber"),
                new SequenceCharsRegExp("}")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  // GrammarUnitRegExpRepeatTimesFragment : IdentifierFragment RepeatTimes ;
  private void initGrammarUnitRegExpRepeatTimesFragment() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "GrammarUnitRegExpRepeatTimesFragment";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp("IdentifierFragment"), new GrammarRegExp("RepeatTimes")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  /*
  SequenceCharsUnitRegExpFragment :
  '\'' CharForSequenceChars* '\'' RepeatTimes? ;
   */
  private void initSequenceCharsUnitRegExpFragment() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "SequenceCharsUnitRegExpFragment";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("'"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0),
                    RepeatTimes.infinityTimes(),
                    "CharForSequenceChars"),
                new SequenceCharsRegExp("'"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.numberTimes(1), "RepeatTimes")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  /*
  OneCharOptionCharsetUnitRegExpFragment :
  '~'? '[' CharForOneCharOptionCharset* ']' RepeatTimes? ;
   */
  private void initOneCharOptionCharsetUnitRegExpFragment() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "OneCharOptionCharsetUnitRegExpFragment";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.numberTimes(1), "~"),
                new SequenceCharsRegExp("["),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0),
                    RepeatTimes.infinityTimes(),
                    "CharForOneCharOptionCharset"),
                new SequenceCharsRegExp("]"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.numberTimes(1), "RepeatTimes")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  // ParenthesisUnitRegExpSuffixFragment : ')' RepeatTimes? ;
  private void initParenthesisUnitRegExpSuffixFragment() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "ParenthesisUnitRegExpSuffixFragment";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp(")"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.numberTimes(1), "RepeatTimes")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  // NfaTerminalGrammarAttributeFragment : 'nfa' '(' IdentifierFragment ',' IdentifierFragment  ')'
  // ;
  private void initNfaTerminalGrammarAttributeFragment() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "NfaTerminalGrammarAttributeFragment";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("nfa"),
                new SequenceCharsRegExp("("),
                new GrammarRegExp("IdentifierFragment"),
                new SequenceCharsRegExp(","),
                new GrammarRegExp("IdentifierFragment"),
                new SequenceCharsRegExp(")")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  // LazinessTerminalGrammarAttributeFragment :  'laziness()' ;
  private void initLazinessTerminalGrammarAttributeFragment() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "LazinessTerminalGrammarAttributeFragment";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new SequenceCharsRegExp("laziness()")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  // DerivedTerminalGrammarAttributeFragment :  'derive' '(' IdentifierFragment ')' ;
  private void initDerivedTerminalGrammarAttributeFragment() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "DerivedTerminalGrammarAttributeFragment";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("derive"),
                new SequenceCharsRegExp("("),
                new GrammarRegExp("IdentifierFragment"),
                new SequenceCharsRegExp(")")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  /*
    NfaEdgeFragment :
      IdentifierFragment '\'' CharForSequenceChars* '\'' IdentifierFragment
      | IdentifierFragment '~'? '[' CharForOneCharOptionCharset* ']' IdentifierFragment
  ;
     */
  private void initNfaEdgeFragment() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "NfaEdgeFragment";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new GrammarRegExp("IdentifierFragment"),
                new SequenceCharsRegExp("'"),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0),
                    RepeatTimes.infinityTimes(),
                    "CharForSequenceChars"),
                new SequenceCharsRegExp("'"),
                new GrammarRegExp("IdentifierFragment")),
            new AndCompositeRegExp(
                new GrammarRegExp("IdentifierFragment"),
                new SequenceCharsRegExp(
                    RepeatTimes.numberTimes(0), RepeatTimes.numberTimes(1), "~"),
                new SequenceCharsRegExp("["),
                new GrammarRegExp(
                    RepeatTimes.numberTimes(0),
                    RepeatTimes.infinityTimes(),
                    "CharForOneCharOptionCharset"),
                new SequenceCharsRegExp("]"),
                new GrammarRegExp("IdentifierFragment")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  // AndCompositeRegExpAliasFragment : '#'  IdentifierFragment ;
  private void initAndCompositeRegExpAliasFragment() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "AndCompositeRegExpAliasFragment";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new SequenceCharsRegExp("#"), new GrammarRegExp("IdentifierFragment")));
    languageGrammar.addTerminalFragmentGrammar(createTerminalFragmentGrammar(grammarContent));
  }

  private TerminalFragmentGrammar createTerminalFragmentGrammar(
      PrimaryGrammarContent primaryGrammarContent) {
    TerminalFragmentGrammar terminalFragmentGrammar =
        new TerminalFragmentGrammar(primaryGrammarContent.grammarName);
    terminalFragmentGrammar.primaryGrammarContent = primaryGrammarContent;
    return terminalFragmentGrammar;
  }
}
