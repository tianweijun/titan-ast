package titan.ast.impl.ast.automata;

import java.util.ArrayList;
import java.util.List;
import titan.ast.grammar.*;
import titan.ast.grammar.PrimaryGrammarContent.NfaPrimaryGrammarContent;
import titan.ast.grammar.PrimaryGrammarContent.NfaPrimaryGrammarContentEdge;
import titan.ast.grammar.PrimaryGrammarContent.RegExpPrimaryGrammarContent;
import titan.ast.grammar.regexp.*;
import titan.ast.grammar.regexp.OneCharOptionCharsetRegExp.OptionChar;

class TerminalGrammarInitializer {
  LanguageGrammar languageGrammar;

  TerminalGrammarInitializer(LanguageGrammar languageGrammar) {
    this.languageGrammar = languageGrammar;
  }

  void init() {
    initColon();
    initSemi();
    initVerticalBar();
    initArrow();
    initStartGrammarPrefix();
    initNonterminalGrammarPrefix();
    initTerminalGrammarPrefix();
    initTerminalFragmentGrammarPrefix();
    initDerivedTerminalGrammarPrefix();
    initGrammarUnitRegExpRepeatTimes();
    initSequenceCharsUnitRegExp();
    initOneCharOptionCharsetUnitRegExp();
    initParenthesisUnitRegExpPrefix();
    initParenthesisUnitRegExpSuffix();
    initNfaTerminalGrammarAttribute();
    initLazinessTerminalGrammarAttribute();
    initDerivedTerminalGrammarAttribute();
    initNfaEdge();
    initAndCompositeRegExpAlias();
    initIdentifier();
    initBlockComment();
    initLineComment();
    initDelimiterChars();
  }

  // Colon : ':'  ;
  private void initColon() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "Colon";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new SequenceCharsRegExp(":")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // Semi : ';' ;
  private void initSemi() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "Semi";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new SequenceCharsRegExp(";")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // VerticalBar : '|' ;
  private void initVerticalBar() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "VerticalBar";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new SequenceCharsRegExp("|")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // Arrow : '->' ;
  private void initArrow() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "Arrow";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new SequenceCharsRegExp("->")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // StartGrammarPrefix : '@StartGrammar' ;
  private void initStartGrammarPrefix() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "StartGrammarPrefix";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new SequenceCharsRegExp("@StartGrammar")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // NonterminalGrammarPrefix : '@NonterminalGrammar' ;
  private void initNonterminalGrammarPrefix() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "NonterminalGrammarPrefix";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new SequenceCharsRegExp("@NonterminalGrammar")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // TerminalGrammarPrefix : '@TerminalGrammar' ;
  private void initTerminalGrammarPrefix() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "TerminalGrammarPrefix";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new SequenceCharsRegExp("@TerminalGrammar")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // TerminalFragmentGrammarPrefix : '@TerminalFragmentGrammar' ;
  private void initTerminalFragmentGrammarPrefix() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "TerminalFragmentGrammarPrefix";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new SequenceCharsRegExp("@TerminalFragmentGrammar")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // DerivedTerminalGrammarPrefix : '@DerivedTerminalGrammar' ;
  private void initDerivedTerminalGrammarPrefix() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "DerivedTerminalGrammarPrefix";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new SequenceCharsRegExp("@DerivedTerminalGrammar")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // GrammarUnitRegExpRepeatTimes : GrammarUnitRegExpRepeatTimesFragment ;
  private void initGrammarUnitRegExpRepeatTimes() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "GrammarUnitRegExpRepeatTimes";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("GrammarUnitRegExpRepeatTimesFragment")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // SequenceCharsUnitRegExp : SequenceCharsUnitRegExpFragment ;
  private void initSequenceCharsUnitRegExp() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "SequenceCharsUnitRegExp";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("SequenceCharsUnitRegExpFragment")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // OneCharOptionCharsetUnitRegExp : OneCharOptionCharsetUnitRegExpFragment ;
  private void initOneCharOptionCharsetUnitRegExp() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "OneCharOptionCharsetUnitRegExp";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("OneCharOptionCharsetUnitRegExpFragment")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // ParenthesisUnitRegExpPrefix : '(' ;
  private void initParenthesisUnitRegExpPrefix() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "ParenthesisUnitRegExpPrefix";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new SequenceCharsRegExp("(")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // ParenthesisUnitRegExpSuffix : ParenthesisUnitRegExpSuffixFragment ;
  private void initParenthesisUnitRegExpSuffix() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "ParenthesisUnitRegExpSuffix";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("ParenthesisUnitRegExpSuffixFragment")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // NfaTerminalGrammarAttribute : NfaTerminalGrammarAttributeFragment ;
  private void initNfaTerminalGrammarAttribute() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "NfaTerminalGrammarAttribute";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("NfaTerminalGrammarAttributeFragment")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // LazinessTerminalGrammarAttribute :  LazinessTerminalGrammarAttributeFragment ;
  private void initLazinessTerminalGrammarAttribute() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "LazinessTerminalGrammarAttribute";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("LazinessTerminalGrammarAttributeFragment")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // DerivedTerminalGrammarAttribute : DerivedTerminalGrammarAttributeFragment ;
  private void initDerivedTerminalGrammarAttribute() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "DerivedTerminalGrammarAttribute";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("DerivedTerminalGrammarAttributeFragment")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // NfaEdge : NfaEdgeFragment ;
  private void initNfaEdge() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "NfaEdge";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new GrammarRegExp("NfaEdgeFragment")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // AndCompositeRegExpAlias : AndCompositeRegExpAliasFragment ;
  private void initAndCompositeRegExpAlias() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "AndCompositeRegExpAlias";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(new GrammarRegExp("AndCompositeRegExpAliasFragment")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // Identifier : IdentifierFragment ;
  private void initIdentifier() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "Identifier";
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(new AndCompositeRegExp(new GrammarRegExp("IdentifierFragment")));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  /*
  BlockComment
    nfa(start,end)
     :
               start'/*'prefix2
               prefix2[*]suffix1 prefix2~[*]prefix2
               suffix1[/]end suffix1[*]suffix1 suffix1~[/*]prefix2
     -> skip
  ;
   */
  private void initBlockComment() {
    NfaPrimaryGrammarContent grammarContent = new NfaPrimaryGrammarContent();
    grammarContent.grammarName = "BlockComment";
    grammarContent.grammarAction = GrammarAction.SKIP;
    grammarContent.start = "start";
    grammarContent.end = "end";
    List<NfaPrimaryGrammarContentEdge> edges = new ArrayList<>(6);
    // start'/*'prefix2
    edges.add(NfaPrimaryGrammarContentEdge.sequenceCharsEdge("start", "prefix2", "/*"));
    // prefix2[*]suffix1
    edges.add(
        NfaPrimaryGrammarContentEdge.optionCharsEdge("prefix2", "suffix1", new OptionChar('*')));
    // prefix2~[*]prefix2  *42
    edges.add(
        NfaPrimaryGrammarContentEdge.optionCharsEdge(
            "prefix2", "prefix2", new OptionChar(0, 41), new OptionChar(43, 255)));
    // suffix1[/]end
    edges.add(NfaPrimaryGrammarContentEdge.optionCharsEdge("suffix1", "end", new OptionChar('/')));
    // suffix1[*]suffix1
    edges.add(
        NfaPrimaryGrammarContentEdge.optionCharsEdge("suffix1", "suffix1", new OptionChar('*')));
    // suffix1~[/*]prefix2  /47 *42
    edges.add(
        NfaPrimaryGrammarContentEdge.optionCharsEdge(
            "suffix1",
            "prefix2",
            new OptionChar(0, 41),
            new OptionChar(43, 46),
            new OptionChar(48, 255)));
    grammarContent.edges = edges;
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  /*
    LineComment
      nfa(start,end)
       :
        start'//'prefix2
        prefix2[]end prefix2[\n]end prefix2~[\n]prefix2
      -> skip
  ;
     */
  private void initLineComment() {
    NfaPrimaryGrammarContent grammarContent = new NfaPrimaryGrammarContent();
    grammarContent.grammarName = "LineComment";
    grammarContent.grammarAction = GrammarAction.SKIP;
    grammarContent.start = "start";
    grammarContent.end = "end";
    List<NfaPrimaryGrammarContentEdge> edges = new ArrayList<>(6);
    // start'//'prefix2
    edges.add(NfaPrimaryGrammarContentEdge.sequenceCharsEdge("start", "prefix2", "//"));
    // prefix2[]end
    edges.add(NfaPrimaryGrammarContentEdge.optionCharsEdge("prefix2", "end"));
    // prefix2[\n]end
    edges.add(NfaPrimaryGrammarContentEdge.optionCharsEdge("prefix2", "end", new OptionChar('\n')));
    // prefix2~[\n]prefix2 \n-10
    edges.add(
        NfaPrimaryGrammarContentEdge.optionCharsEdge(
            "prefix2", "prefix2", new OptionChar(0, 9), new OptionChar(11, 255)));
    grammarContent.edges = edges;
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  // DelimiterChars : [\s\t\r\n]+ -> skip ;
  private void initDelimiterChars() {
    RegExpPrimaryGrammarContent grammarContent = new RegExpPrimaryGrammarContent();
    grammarContent.grammarName = "DelimiterChars";
    grammarContent.grammarAction = GrammarAction.SKIP;
    grammarContent.orCompositeRegExp =
        new OrCompositeRegExp(
            new AndCompositeRegExp(
                new OneCharOptionCharsetRegExp(
                    RepeatTimes.numberTimes(1),
                    RepeatTimes.infinityTimes(),
                    new OptionChar(' '),
                    new OptionChar('\t'),
                    new OptionChar('\r'),
                    new OptionChar('\n'))));
    languageGrammar.addTerminalGrammar(createTerminalGrammar(grammarContent));
  }

  private TerminalGrammar createTerminalGrammar(PrimaryGrammarContent primaryGrammarContent) {
    TerminalGrammar terminalGrammar = new TerminalGrammar(primaryGrammarContent.grammarName);
    if (primaryGrammarContent.grammarAction != null) {
      terminalGrammar.action = primaryGrammarContent.grammarAction;
    }

    terminalGrammar.primaryGrammarContent = primaryGrammarContent;
    return terminalGrammar;
  }
}
