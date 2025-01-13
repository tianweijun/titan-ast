package titan.ast.grammar.token;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.io.GrammarCharset;
import titan.ast.grammar.io.GrammarToken;

/**
 * keyWords不参与任何tokenDfa的构建，仅仅是LanguageGrammarInitializer.init().
 *
 * @author tian wei jun
 */
public class KeyWordAutomataBuilder {
  LanguageGrammar languageGrammar;
  KeyWordAutomata keyWordAutomata;

  private Grammar grammar;

  public KeyWordAutomataBuilder(LanguageGrammar languageGrammar) {
    this.languageGrammar = languageGrammar;
  }

  public void build() {
    keyWordAutomata = new KeyWordAutomata();
    if (!languageGrammar.keyWordAutomataDetail.isEmpty()) {
      setRootKeyWord();
      setKeyWord();
      if (!keyWordAutomata.textTerminalMap.isEmpty()) {
        keyWordAutomata.emptyOrNot = KeyWordAutomata.NOT_EMPTY;
      }
    }
    languageGrammar.keyWordAutomataDetail.keyWordAutomata = keyWordAutomata;
  }

  private void setRootKeyWord() {
    String rootKeyWordName = languageGrammar.keyWordAutomataDetail.rootKeyWordGrammarName;
    Grammar rootKeyWord = languageGrammar.terminals.get(rootKeyWordName);
    if (null == rootKeyWord) {
      throw new AstRuntimeException(
          String.format("rootKeyWordName '%s' match grammar error.", rootKeyWordName));
    }
    keyWordAutomata.rootKeyWord = rootKeyWord;
  }

  private void setKeyWord() {
    LinkedHashSet<Grammar> keyWords = languageGrammar.keyWordAutomataDetail.keyWords;
    HashMap<String, Grammar> textGrammarMap = new HashMap<>(keyWords.size());
    for (Grammar keyWord : keyWords) {
      this.grammar = keyWord;
      List<String> textOfKeyWords = getTextsOfKeyWord(keyWord);
      for (String textOfKeyWord : textOfKeyWords) {
        if (textGrammarMap.containsKey(textOfKeyWord)) {
          throw new AstRuntimeException(
              String.format("KeyWord must be unique ,error near '%s'.", textOfKeyWord));
        }
        textGrammarMap.put(textOfKeyWord, keyWord);
      }
    }
    keyWordAutomata.textTerminalMap = textGrammarMap;
  }

  private List<String> getTextsOfKeyWord(Grammar keyWord) {
    StringBuilder charsBuilder = new StringBuilder();
    for (GrammarToken token : keyWord.text) {
      charsBuilder.append(token.text);
    }
    List<String> textOfKeyWords = new LinkedList<>();
    char[] text = charsBuilder.toString().toCharArray();
    int indexOfText = 0;
    indexOfText = setOneTextOfKeyWord(text, indexOfText, textOfKeyWords);

    while (indexOfText < text.length) {
      indexOfText = skipAndExpectOneChar(GrammarCharset.VERTICAL_BAR, text, indexOfText);
      indexOfText = setOneTextOfKeyWord(text, indexOfText, textOfKeyWords);
    }
    return textOfKeyWords;
  }

  private int setOneTextOfKeyWord(char[] text, int indexOfText, List<String> textOfKeyWords) {
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
              grammar.name, new String(text)));
    }
    textOfKeyWords.add(
        GrammarCharset.formatEscapeChar2Char(textOfKeyWordBuilder.toString(), grammar.name));
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
              grammar.name, ch, new String(text)));
    }
    if (text[indexOfText] != ch) {
      throw new AstRuntimeException(
          String.format(
              "%s: expect a char '%c',error near %s",
              grammar.name, ch, new String(text, indexOfText, text.length - indexOfText)));
    }
  }
}
