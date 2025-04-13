package titan.ast.grammar;

import java.util.Set;
import titan.ast.grammar.GrammarAttribute.NfaTerminalGrammarAttribute;
import titan.ast.grammar.GrammarAttribute.TerminalGrammarAttributeEnum;
import titan.ast.grammar.PrimaryGrammarContent.NfaPrimaryGrammarContent;
import titan.ast.grammar.PrimaryGrammarContent.RegExpPrimaryGrammarContent;

/**
 * .
 *
 * @author tian wei jun
 */
public class GrammarCreater {

  private GrammarCreater() {
  }

  public static NonterminalGrammar createNonterminalGrammar(RegExpPrimaryGrammarContent regExpPrimaryGrammarContent) {
    NonterminalGrammar nonterminalGrammar =
        new NonterminalGrammar(regExpPrimaryGrammarContent.grammarName);
    nonterminalGrammar.primaryGrammarContent = regExpPrimaryGrammarContent;
    return nonterminalGrammar;
  }


  public static TerminalFragmentGrammar createTerminalFragmentGrammar(
      RegExpPrimaryGrammarContent regExpPrimaryGrammarContent) {
    TerminalFragmentGrammar terminalFragmentGrammar =
        new TerminalFragmentGrammar(regExpPrimaryGrammarContent.grammarName);
    terminalFragmentGrammar.primaryGrammarContent = regExpPrimaryGrammarContent;
    return terminalFragmentGrammar;
  }

  public static TerminalGrammar createDerivedTerminalGrammar(RegExpPrimaryGrammarContent regExpPrimaryGrammarContent) {
    TerminalGrammar terminalGrammar =
        new TerminalGrammar(regExpPrimaryGrammarContent.grammarName);
    terminalGrammar.primaryGrammarContent = regExpPrimaryGrammarContent;
    return terminalGrammar;
  }

  public static TerminalGrammar createTerminalGrammar(PrimaryGrammarContent primaryGrammarContent) {
    TerminalGrammar terminalGrammar = new TerminalGrammar(primaryGrammarContent.grammarName);
    setGrammarAttributes(terminalGrammar, primaryGrammarContent.grammarAttributes);
    setGrammarAction(terminalGrammar, primaryGrammarContent.grammarAction);
    terminalGrammar.primaryGrammarContent = primaryGrammarContent;
    switch (primaryGrammarContent.type) {
      case REG_EXP -> {
      }
      case NFA -> {
        NfaPrimaryGrammarContent nfaPrimaryGrammarContent = (NfaPrimaryGrammarContent) primaryGrammarContent;
        setNfaStartEnd(nfaPrimaryGrammarContent, nfaPrimaryGrammarContent.grammarAttributes);
      }
    }
    return terminalGrammar;
  }

  private static void setNfaStartEnd(NfaPrimaryGrammarContent nfaPrimaryGrammarContent,
      Set<GrammarAttribute> grammarAttributes) {
    if (null == grammarAttributes || grammarAttributes.isEmpty()) {
      return;
    }
    for (GrammarAttribute grammarAttribute : grammarAttributes) {
      if (grammarAttribute.type == TerminalGrammarAttributeEnum.NFA_TERMINAL_GRAMMAR_ATTRIBUTE) {
        NfaTerminalGrammarAttribute nfaTerminalGrammarAttribute = (NfaTerminalGrammarAttribute) grammarAttribute;
        nfaPrimaryGrammarContent.start = nfaTerminalGrammarAttribute.start;
        nfaPrimaryGrammarContent.end = nfaTerminalGrammarAttribute.end;
        break;
      }
    }
  }

  private static void setGrammarAction(TerminalGrammar terminalGrammar, GrammarAction grammarAction) {
    if (grammarAction != null) {
      terminalGrammar.action = grammarAction;
    }
  }

  private static void setGrammarAttributes(TerminalGrammar terminalGrammar, Set<GrammarAttribute> grammarAttributes) {
    if (null == grammarAttributes || grammarAttributes.isEmpty()) {
      return;
    }
    for (GrammarAttribute grammarAttribute : grammarAttributes) {
      if (grammarAttribute.type == TerminalGrammarAttributeEnum.LAZINESS_TERMINAL_GRAMMAR_ATTRIBUTE) {
        terminalGrammar.lookaheadMatchingMode = LookaheadMatchingMode.LAZINESS;
        break;
      }
    }
  }
}
