package titan.ast.fa.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import titan.ast.AstContext;
import titan.ast.AstRuntimeException;
import titan.ast.fa.token.DerivedTerminalGrammarAutomataData.RootTerminalGrammarMap;
import titan.ast.grammar.DerivedTerminalGrammarAutomataDetail.RootTerminalGrammarMapDetail;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.PrimaryGrammarContent.RegExpPrimaryGrammarContent;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.grammar.regexp.AndCompositeRegExp;
import titan.ast.grammar.regexp.OrCompositeRegExp;
import titan.ast.grammar.regexp.RegExpType;
import titan.ast.grammar.regexp.SequenceCharsRegExp;
import titan.ast.grammar.regexp.UnitRegExp;

/**
 * keyWords不参与任何tokenDfa的构建，仅仅是LanguageGrammarInitializer.init().
 *
 * @author tian wei jun
 */
public class DerivedTerminalGrammarAutomataDataBuilder {

  LanguageGrammar languageGrammar;
  DerivedTerminalGrammarAutomataData derivedTerminalGrammarAutomataData;

  public DerivedTerminalGrammarAutomataDataBuilder() {
    this.languageGrammar = AstContext.get().languageGrammar;
  }

  public void build() {
    if (languageGrammar.derivedTerminalGrammarAutomataDetail.isEmpty()) {
      return;
    }
    buildDerivedTerminalGrammarAutomataData();
    derivedTerminalGrammarAutomataData.verifyTexts();
    addDerivedTerminalGrammar2Terminals();
  }

  /**
   * 为了参与语法自动机的构建,derivedTerminalGrammars不参与任何tokenDfa的构建，参与syntaxDfa的构建.
   */
  private void addDerivedTerminalGrammar2Terminals() {
    for (RootTerminalGrammarMapDetail rootTerminalGrammarMapDetail :
        languageGrammar.derivedTerminalGrammarAutomataDetail.rootTerminalGrammarMaps.values()) {
      for (TerminalGrammar derivedTerminalGrammar :
          rootTerminalGrammarMapDetail.derivedTerminalGrammars.keySet()) {
        addDerivedTerminalGrammar2Terminals(derivedTerminalGrammar);
      }
    }
  }

  private void addDerivedTerminalGrammar2Terminals(TerminalGrammar derivedTerminalGrammar) {
    LinkedHashMap<String, TerminalGrammar> derivedTerminalGrammars =
        derivedTerminalGrammarAutomataData.derivedTerminalGrammars;
    if (languageGrammar.isUniqueTerminalGrammar(derivedTerminalGrammar)
        && !derivedTerminalGrammars.containsKey(derivedTerminalGrammar.name)) {
      derivedTerminalGrammars.put(derivedTerminalGrammar.name, derivedTerminalGrammar);
    } else {
      throw new AstRuntimeException(
          String.format("name of grammar '%s' is not unique.", derivedTerminalGrammar.name));
    }
  }

  private void buildDerivedTerminalGrammarAutomataData() {
    derivedTerminalGrammarAutomataData = new DerivedTerminalGrammarAutomataData();
    for (Entry<String, RootTerminalGrammarMapDetail> entry :
        languageGrammar.derivedTerminalGrammarAutomataDetail.rootTerminalGrammarMaps.entrySet()) {
      String rootTerminalGrammar = entry.getKey();
      RootTerminalGrammarMapDetail rootTerminalGrammarMapDetail = entry.getValue();
      RootTerminalGrammarMap rootTerminalGrammarMap =
          getRootTerminalGrammarMap(rootTerminalGrammar, rootTerminalGrammarMapDetail);
      if (!rootTerminalGrammarMap.textTerminalMap.isEmpty()) {
        derivedTerminalGrammarAutomataData.rootTerminalGrammarMaps.add(rootTerminalGrammarMap);
      }
    }

    languageGrammar.derivedTerminalGrammarAutomataDetail.derivedTerminalGrammarAutomataData =
        derivedTerminalGrammarAutomataData;
  }

  private RootTerminalGrammarMap getRootTerminalGrammarMap(
      String rootTerminalGrammarName, RootTerminalGrammarMapDetail dootTerminalGrammarMapDetail) {
    TerminalGrammar rootTerminalGrammar = getRootTerminalGrammar(rootTerminalGrammarName);
    HashMap<String, TerminalGrammar> textTerminalMap =
        getTextTerminalMap(dootTerminalGrammarMapDetail.derivedTerminalGrammars);
    return new RootTerminalGrammarMap(rootTerminalGrammar, textTerminalMap);
  }

  private TerminalGrammar getRootTerminalGrammar(String rootTerminalGrammarName) {
    TerminalGrammar rootTerminalGrammar = languageGrammar.terminals.get(rootTerminalGrammarName);
    if (null == rootTerminalGrammar) {
      throw new AstRuntimeException(
          String.format("RootTerminalGrammar '%s' match grammar error.", rootTerminalGrammarName));
    }
    return rootTerminalGrammar;
  }

  private HashMap<String, TerminalGrammar> getTextTerminalMap(
      HashMap<TerminalGrammar, LinkedList<TerminalGrammar>> derivedTerminalGrammars) {
    int countOfTexts = 0;
    for (LinkedList<TerminalGrammar> sameNameKeyWords : derivedTerminalGrammars.values()) {
      countOfTexts += sameNameKeyWords.size();
    }
    HashMap<String, TerminalGrammar> textGrammarMap = new HashMap<>(countOfTexts);
    for (Entry<TerminalGrammar, LinkedList<TerminalGrammar>> entry : derivedTerminalGrammars.entrySet()) {
      TerminalGrammar derivedTerminalGrammar = entry.getKey();
      for (TerminalGrammar sameNameDerivedTerminalGrammar : entry.getValue()) {
        List<String> textsOfDerivedTerminalGrammar =
            getTextsOfDerivedTerminalGrammar(sameNameDerivedTerminalGrammar);
        for (String textOfDerivedTerminalGrammar : textsOfDerivedTerminalGrammar) {
          if (textGrammarMap.containsKey(textOfDerivedTerminalGrammar)) {
            throw new AstRuntimeException(
                String.format(
                    "%s:text of DerivedTerminalGrammar must be unique ,error near '%s'.",
                    derivedTerminalGrammar.name, textOfDerivedTerminalGrammar));
          }
          textGrammarMap.put(textOfDerivedTerminalGrammar, derivedTerminalGrammar);
        }
      }
    }
    return textGrammarMap;
  }

  // sequenceCharsUnitRegExp ('|' sequenceCharsUnitRegExp)*
  private List<String> getTextsOfDerivedTerminalGrammar(TerminalGrammar derivedTerminalGrammar) {
    RegExpPrimaryGrammarContent regExpPrimaryGrammarContent =
        (RegExpPrimaryGrammarContent) derivedTerminalGrammar.primaryGrammarContent;
    OrCompositeRegExp orCompositeRegExp = regExpPrimaryGrammarContent.orCompositeRegExp;
    if (!isRightRegExpOfDerivedTerminalGrammar(orCompositeRegExp)) {
      throw new AstRuntimeException(
          String.format(
              "%s:grammar of DerivedTerminalGrammar is not legal,support like this : sequenceCharsUnitRegExp ('|' "
                  + "sequenceCharsUnitRegExp)* ",
              derivedTerminalGrammar.name));
    }
    List<String> texts = new ArrayList<>(orCompositeRegExp.children.size());
    for (AndCompositeRegExp andCompositeRegExp : orCompositeRegExp.children) {
      SequenceCharsRegExp sequenceCharsRegExp = (SequenceCharsRegExp) andCompositeRegExp.children.get(0);
      texts.add(sequenceCharsRegExp.chars);
    }
    return texts;
  }

  private boolean isRightRegExpOfDerivedTerminalGrammar(OrCompositeRegExp orCompositeRegExp) {
    boolean isRight = true;
    for (AndCompositeRegExp andCompositeRegExp : orCompositeRegExp.children) {
      if (andCompositeRegExp.children.size() != 1) {
        isRight = false;
        break;
      }
      UnitRegExp unitRegExp = andCompositeRegExp.children.get(0);
      if (!(unitRegExp.type == RegExpType.SEQUENCE_CHARS
          && unitRegExp.repMinTimes.isNumberTimesAndEqual(1)
          && unitRegExp.repMaxTimes.isNumberTimesAndEqual(1))
      ) {
        isRight = false;
        break;
      }
    }
    return isRight;
  }
}
