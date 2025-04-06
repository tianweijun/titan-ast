package titan.ast.grammar.token;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.DerivedTerminalGrammarAutomataDetail.RootTerminalGrammarMapDetail;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.io.GrammarCharset;
import titan.ast.grammar.io.GrammarToken;
import titan.ast.grammar.token.DerivedTerminalGrammarAutomataData.RootTerminalGrammarMap;

/**
 * keyWords不参与任何tokenDfa的构建，仅仅是LanguageGrammarInitializer.init().
 *
 * @author tian wei jun
 */
public class DerivedTerminalGrammarAutomataDataBuilder {
  LanguageGrammar languageGrammar;
  DerivedTerminalGrammarAutomataData derivedTerminalGrammarAutomataData;

  private Grammar currentDerivedTerminalGrammar;

  public DerivedTerminalGrammarAutomataDataBuilder(LanguageGrammar languageGrammar) {
    this.languageGrammar = languageGrammar;
  }

  public void build() {
    if (languageGrammar.derivedTerminalGrammarAutomataDetail.isEmpty()) {
      return;
    }
    buildDerivedTerminalGrammarAutomataData();
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
    Grammar rootTerminalGrammar = getRootTerminalGrammar(rootTerminalGrammarName);
    HashMap<String, Grammar> textTerminalMap =
        getTextTerminalMap(dootTerminalGrammarMapDetail.derivedTerminalGrammars);
    return new RootTerminalGrammarMap(rootTerminalGrammar, textTerminalMap);
  }

  private Grammar getRootTerminalGrammar(String rootTerminalGrammarName) {
    Grammar rootTerminalGrammar = languageGrammar.terminals.get(rootTerminalGrammarName);
    if (null == rootTerminalGrammar) {
      throw new AstRuntimeException(
          String.format("RootTerminalGrammar '%s' match grammar error.", rootTerminalGrammarName));
    }
    return rootTerminalGrammar;
  }

  private HashMap<String, Grammar> getTextTerminalMap(
      HashMap<Grammar, LinkedList<Grammar>> derivedTerminalGrammars) {
    int countOfTexts = 0;
    for (LinkedList<Grammar> sameNameKeyWords : derivedTerminalGrammars.values()) {
      countOfTexts += sameNameKeyWords.size();
    }
    HashMap<String, Grammar> textGrammarMap = new HashMap<>(countOfTexts);
    for (Entry<Grammar, LinkedList<Grammar>> entry : derivedTerminalGrammars.entrySet()) {
      Grammar derivedTerminalGrammar = entry.getKey();
      this.currentDerivedTerminalGrammar = derivedTerminalGrammar;
      for (Grammar sameNameDerivedTerminalGrammar : entry.getValue()) {
        List<String> textsOfDerivedTerminalGrammar =
            getTextsOfDerivedTerminalGrammar(sameNameDerivedTerminalGrammar);
        for (String textOfDerivedTerminalGrammar : textsOfDerivedTerminalGrammar) {
          if (textGrammarMap.containsKey(textOfDerivedTerminalGrammar)) {
            throw new AstRuntimeException(
                String.format(
                    "%s:DerivedTerminalGrammar must be unique ,error near '%s'.",
                    derivedTerminalGrammar.name, textOfDerivedTerminalGrammar));
          }
          textGrammarMap.put(textOfDerivedTerminalGrammar, derivedTerminalGrammar);
        }
      }
    }
    return textGrammarMap;
  }

  private List<String> getTextsOfDerivedTerminalGrammar(Grammar derivedTerminalGrammar) {
    StringBuilder charsBuilder = new StringBuilder();
    for (GrammarToken token : derivedTerminalGrammar.text) {
      charsBuilder.append(token.text);
    }
    List<String> textOfKeyWords = new LinkedList<>();
    char[] text = charsBuilder.toString().toCharArray();
    int indexOfText = 0;
    indexOfText = setOneTextOfDerivedTerminalGrammar(text, indexOfText, textOfKeyWords);

    while (indexOfText < text.length) {
      indexOfText = skipAndExpectOneChar(GrammarCharset.VERTICAL_BAR, text, indexOfText);
      indexOfText = setOneTextOfDerivedTerminalGrammar(text, indexOfText, textOfKeyWords);
    }
    return textOfKeyWords;
  }

  private int setOneTextOfDerivedTerminalGrammar(
      char[] text, int indexOfText, List<String> textsOfDerivedTerminalGrammar) {
    StringBuilder textOfKeyWordBuilder = new StringBuilder();
    indexOfText = skipAndExpectOneChar(GrammarCharset.SINGLE_QUOTE, text, indexOfText);
    boolean isTextRightEnd = false;
    while (indexOfText < text.length) {
      char ch = text[indexOfText];
      if (ch == GrammarCharset.SINGLE_QUOTE) {
        isTextRightEnd = true;
        break;
      }
      textOfKeyWordBuilder.append(ch);
      ++indexOfText;
    }
    if (!isTextRightEnd) {
      expectOneChar(GrammarCharset.SINGLE_QUOTE, text, text.length);
    }
    if (textOfKeyWordBuilder.isEmpty()) {
      throw new AstRuntimeException(
          String.format(
              "%s: expect keyword text like 'xxx', but is empty,error near %s",
              currentDerivedTerminalGrammar.name, new String(text)));
    }
    textsOfDerivedTerminalGrammar.add(
        GrammarCharset.formatEscapeChar2Char(
            textOfKeyWordBuilder.toString(), currentDerivedTerminalGrammar.name));
    // skip'
    ++indexOfText;
    return indexOfText;
  }

  private int skipAndExpectOneChar(char ch, char[] text, int indexOfText) {
    expectOneChar(ch, text, indexOfText);
    return ++indexOfText;
  }

  private void expectOneChar(char ch, char[] text, int indexOfText) {
    if (indexOfText >= text.length) {
      throw new AstRuntimeException(
          String.format(
              "%s: expect a char '%c',but there are empty,error at end of %s",
              currentDerivedTerminalGrammar.name, ch, new String(text)));
    }
    if (text[indexOfText] != ch) {
      throw new AstRuntimeException(
          String.format(
              "%s: expect a char '%c',error near %s",
              currentDerivedTerminalGrammar.name,
              ch,
              new String(text, indexOfText, text.length - indexOfText)));
    }
  }
}
