package titan.ast.fa.token;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import titan.ast.AstContext;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.PrimaryGrammarContent.RegExpPrimaryGrammarContent;
import titan.ast.grammar.TerminalFragmentGrammar;
import titan.ast.grammar.regexp.AndCompositeRegExp;
import titan.ast.grammar.regexp.GrammarRegExp;
import titan.ast.grammar.regexp.OrCompositeRegExp;
import titan.ast.grammar.regexp.ParenthesisRegExp;
import titan.ast.grammar.regexp.UnitRegExp;

/**
 * 构造正则片段的nfa.
 *
 * @author tian wei jun
 */
public class TerminalFragmentGrammarNfaBuilder {

  LinkedHashMap<String, TerminalFragmentGrammar> terminalFragments;

  public TerminalFragmentGrammarNfaBuilder() {
    terminalFragments = AstContext.get().languageGrammar.terminalFragments;
  }

  public void buildNfa() {
    setGrammarOfGrammarRegExp();
    //build nfa
    LinkedList<Grammar> nfaGrammars = new LinkedList<>();
    LinkedList<Grammar> regExpGrammars = new LinkedList<>();
    for (TerminalFragmentGrammar grammar : terminalFragments.values()) {
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
  }


  private void setGrammarOfGrammarRegExp() {
    for (TerminalFragmentGrammar terminalFragmentGrammar : terminalFragments.values()) {
      if (terminalFragmentGrammar.primaryGrammarContent instanceof RegExpPrimaryGrammarContent regExpPrimaryGrammarContent) {
        OrCompositeRegExp orCompositeRegExp = regExpPrimaryGrammarContent.orCompositeRegExp;
        setGrammarOfGrammarRegExp(terminalFragmentGrammar, orCompositeRegExp);
      }
    }
  }


  private void setGrammarOfGrammarRegExp(TerminalFragmentGrammar terminalFragmentGrammar,
      OrCompositeRegExp orCompositeRegExp) {
    for (AndCompositeRegExp andCompositeRegExp : orCompositeRegExp.children) {
      for (UnitRegExp unitRegExp : andCompositeRegExp.children) {
        if (unitRegExp instanceof GrammarRegExp grammarRegExp) {
          grammarRegExp.grammar = terminalFragments.get(grammarRegExp.grammarName);
          if (null == grammarRegExp.grammar) {
            throw new AstRuntimeException(
                String.format("terminal fragment grammar(%s) : text(%s) not match any grammar(other "
                    + "fragment)", terminalFragmentGrammar.name, grammarRegExp.grammarName));
          }
        }
        if (unitRegExp instanceof ParenthesisRegExp parenthesisRegExp) {
          setGrammarOfGrammarRegExp(terminalFragmentGrammar, parenthesisRegExp.orCompositeRegExp);
        }
      }
    }
  }
}
