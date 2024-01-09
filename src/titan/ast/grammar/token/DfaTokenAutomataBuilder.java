package titan.ast.grammar.token;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.RegExp;
import titan.ast.grammar.RegExp.MatchingPattern;
import titan.ast.grammar.RegExp.RegExpCharSet;
import titan.ast.grammar.RegExp.RegExpCharSetType;
import titan.ast.grammar.RegExp.RegExpType;
import titan.ast.grammar.RelationshipQualifier;

/**
 * 构造识别token的dfa.
 *
 * @auhor tian wei jun
 */
public class DfaTokenAutomataBuilder {

  final LanguageGrammar languageGrammar;

  public DfaTokenAutomataBuilder(LanguageGrammar languageGrammar) {
    this.languageGrammar = languageGrammar;
  }

  /**
   * fragment -> terminal -> nonterminal 因为正则转NFA简单直接没有过多冗余，那就节点NFA融合为一个NFA在转DFA及最小化.
   *
   * @return DFATokenAutomata
   */
  public DfaTokenAutomata build() {
    // fragment
    FragmentNfaBuilder fragmentNfaBuilder =
        new FragmentNfaBuilder(languageGrammar.terminalFragments);
    fragmentNfaBuilder.build();
    // terminal
    TerminalDfaBuilder terminalDfaBuilder =
        new TerminalDfaBuilder(languageGrammar.terminals, languageGrammar.terminalFragments);
    TokenDfa tokenDfa = terminalDfaBuilder.build();
    languageGrammar.tokenDfa = tokenDfa;

    buildKeyWordAutomata();
    buildUnitRegExpTerminalsMap();
    clearTransientObjects();

    DfaTokenAutomata dfaTokenAutomata =
        new DfaTokenAutomata(languageGrammar.keyWordAutomata, tokenDfa);
    languageGrammar.tokenAutomata = dfaTokenAutomata;

    return dfaTokenAutomata;
  }

  private void buildKeyWordAutomata() {
    KeyWordAutomataBuilder keyWordAutomataBuilder = new KeyWordAutomataBuilder(languageGrammar);
    keyWordAutomataBuilder.build();

    KeyWordContextBuilder keyWordContextBuilder = new KeyWordContextBuilder();
    keyWordContextBuilder.build(languageGrammar);
  }

  private void buildUnitRegExpTerminalsMap() {
    LinkedHashMap<String, Grammar> terminals = languageGrammar.terminals;
    LinkedHashSet<Grammar> keyWords = languageGrammar.keyWords;
    Map<RegExp, Grammar> unitRegExpTerminalsMap =
        new LinkedHashMap<>(terminals.size() + keyWords.size());
    // 普通的串字符
    for (Grammar terminal : terminals.values()) {
      RegExp terminalCompositeRegExp = terminal.regExp;
      if (terminalCompositeRegExp.children.size() == 1) {
        RegExp unitRegExp = terminalCompositeRegExp.children.getFirst();
        if (unitRegExp.type == RegExp.RegExpType.UNIT
            && unitRegExp.unitType == RegExp.RegExpUnitType.SEQUENCE_CHARS) {
          if (unitRegExp.repMinTimes.isNumberTimesAndEqual(1)
              && unitRegExp.repMaxTimes.isNumberTimesAndEqual(1)) {
            unitRegExpTerminalsMap.put(unitRegExp, terminal);
          }
        }
      }
    }
    // keyWords
    for (Entry<String, Grammar> entry :
        languageGrammar.keyWordAutomata.textTerminalMap.entrySet()) {
      RegExp unitRegExp = new RegExp(null);
      unitRegExp.type = RegExpType.UNIT;
      unitRegExp.isNot = false;
      unitRegExp.repMinTimes.setTimes(1);
      unitRegExp.repMaxTimes.setTimes(1);
      unitRegExp.matchingPattern = MatchingPattern.UNBACKTRACKING_GREEDINESS;
      unitRegExp.children.clear();
      unitRegExp.relationshipOfChildren = RelationshipQualifier.AND;
      unitRegExp.unitType = RegExp.RegExpUnitType.SEQUENCE_CHARS;
      RegExpCharSet regExpCharSet = new RegExpCharSet();
      regExpCharSet.type = RegExpCharSetType.SEQUENCE_CHARS;
      String textOfKeyWord = entry.getKey();
      regExpCharSet.chars = textOfKeyWord.toCharArray();
      unitRegExp.sets.add(regExpCharSet);
      Grammar terminal = entry.getValue();
      unitRegExpTerminalsMap.put(unitRegExp, terminal);
    }

    AstContext.get().unitRegExpTerminalsMap = unitRegExpTerminalsMap;
  }

  private void clearTransientObjects() {
    // fragment
    for (Grammar terminalFragment : languageGrammar.terminalFragments.values()) {
      terminalFragment.regExp = null;
      terminalFragment.attributes = null;
      terminalFragment.text = null;
    }
    // terminal
    for (Grammar terminal : languageGrammar.terminals.values()) {
      terminal.regExp = null;
      terminal.attributes = null;
      terminal.text = null;
    }
  }
}
