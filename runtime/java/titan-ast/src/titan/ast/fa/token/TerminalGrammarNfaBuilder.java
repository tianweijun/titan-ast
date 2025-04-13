package titan.ast.fa.token;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import titan.ast.AstContext;
import titan.ast.fa.FaStateType;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.PrimaryGrammarContent.RegExpPrimaryGrammarContent;
import titan.ast.grammar.TerminalFragmentGrammar;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.grammar.regexp.AndCompositeRegExp;
import titan.ast.grammar.regexp.GrammarRegExp;
import titan.ast.grammar.regexp.OrCompositeRegExp;
import titan.ast.grammar.regexp.ParenthesisRegExp;
import titan.ast.grammar.regexp.UnitRegExp;

/**
 * 构造终结符的nfa并设置.
 *
 * @author tian wei jun
 */
public class TerminalGrammarNfaBuilder {

  private final Map<String, TerminalFragmentGrammar> terminalFragments;
  private LinkedHashMap<String, TerminalGrammar> terminals;

  public TerminalGrammarNfaBuilder() {
    LanguageGrammar languageGrammar = AstContext.get().languageGrammar;
    this.terminals = languageGrammar.terminals;
    this.terminalFragments = languageGrammar.terminalFragments;
  }

  public void buildNfa() {
    setGrammarOfGrammarRegExp();
    //build nfa
    LinkedList<Grammar> nfaGrammars = new LinkedList<>();
    LinkedList<Grammar> regExpGrammars = new LinkedList<>();
    for (TerminalGrammar grammar : terminals.values()) {
      switch (grammar.primaryGrammarContent.type) {
        case REG_EXP -> {
          regExpGrammars.add(grammar);
        }
        case NFA -> {
          nfaGrammars.add(grammar);
        }
      }
    }
    new Nfa2TokenNfaConverter(nfaGrammars).convert();
    new RegExp2TokenNfaConverter(regExpGrammars).convert();

    setClosingState();
  }

  private void setClosingState() {
    for (TerminalGrammar terminal : terminals.values()) {
      TokenNfaState terminalEndState = terminal.tokenNfa.end;
      terminalEndState.type = FaStateType.appendClosingTag(terminalEndState.type);
      terminalEndState.terminal = terminal;
    }
  }

  private void setGrammarOfGrammarRegExp() {
    for (TerminalGrammar terminalGrammar : terminals.values()) {
      if (terminalGrammar.primaryGrammarContent instanceof RegExpPrimaryGrammarContent regExpPrimaryGrammarContent) {
        OrCompositeRegExp orCompositeRegExp = regExpPrimaryGrammarContent.orCompositeRegExp;
        setGrammarOfGrammarRegExp(orCompositeRegExp);
      }
    }
  }


  private void setGrammarOfGrammarRegExp(OrCompositeRegExp orCompositeRegExp) {
    for (AndCompositeRegExp andCompositeRegExp : orCompositeRegExp.children) {
      for (UnitRegExp unitRegExp : andCompositeRegExp.children) {
        if (unitRegExp instanceof GrammarRegExp grammarRegExp) {
          grammarRegExp.grammar = getGrammarForGrammarRegExp(grammarRegExp.grammarName);
        }
        if (unitRegExp instanceof ParenthesisRegExp parenthesisRegExp) {
          setGrammarOfGrammarRegExp(parenthesisRegExp.orCompositeRegExp);
        }
      }
    }
  }

  private Grammar getGrammarForGrammarRegExp(String grammarName) {
    Grammar grammar = terminals.get(grammarName);
    if (null == grammar) {
      grammar = terminalFragments.get(grammarName);
    }
    return grammar;
  }
}
