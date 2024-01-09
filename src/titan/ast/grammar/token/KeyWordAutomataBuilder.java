package titan.ast.grammar.token;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.io.GrammarCharset;
import titan.ast.grammar.io.GrammarToken;
import titan.ast.runtime.AstRuntimeException;
import titan.ast.util.StringUtils;

/**
 * .
 *
 * @author tian wei jun
 */
public class KeyWordAutomataBuilder {
  LanguageGrammar languageGrammar;
  KeyWordAutomata keyWordAutomata;
  GrammarCharset grammarCharset;

  public KeyWordAutomataBuilder(LanguageGrammar languageGrammar) {
    this.languageGrammar = languageGrammar;
    this.grammarCharset = AstContext.get().grammarCharset;
  }

  public void build() {
    keyWordAutomata = new KeyWordAutomata();
    if (!languageGrammar.isKeyWordEmpty()) {
      setRootKeyWord();
      setKeyWord();
      keyWordAutomata.emptyOrNot = KeyWordAutomata.NOT_EMPTY;
    }
    languageGrammar.keyWordAutomata = keyWordAutomata;
  }

  private void setKeyWord() {
    LinkedHashSet<Grammar> keyWords = languageGrammar.keyWords;
    HashMap<String, Grammar> textGrammarMap = new HashMap<>(keyWords.size());
    for (Grammar keyWord : keyWords) {
      String textOfKeyWord = getTextOfKeyWord(keyWord);
      if (textGrammarMap.containsKey(textOfKeyWord)) {
        throw new AstRuntimeException(
            String.format("KeyWord must be unique ,error near '%s'.", textOfKeyWord));
      }
      textGrammarMap.put(textOfKeyWord, keyWord);
    }
    keyWordAutomata.textTerminalMap = textGrammarMap;
  }

  private String getTextOfKeyWord(Grammar keyWord) {
    LinkedList<GrammarToken> textTokens = keyWord.text;
    boolean isTextTokensRight = true;
    if (textTokens.size() != 1) {
      isTextTokensRight = false;
    }
    String text = textTokens.getFirst().text;
    if (StringUtils.isBlank(text)) {
      isTextTokensRight = false;
    } else {
      if (!text.startsWith("'")) {
        isTextTokensRight = false;
      }
      if (!text.endsWith("'")) {
        isTextTokensRight = false;
      }
      if (text.length() < 2) {
        isTextTokensRight = false;
      }
    }
    if (!isTextTokensRight) {
      throw new AstRuntimeException(
          String.format("text of KeyWord is not illegal,error near '%s'", keyWord.name));
    }
    text = text.substring(1, text.length() - 1);
    text = grammarCharset.formatEscapeChar2Char(text);
    return text;
  }

  private void setRootKeyWord() {
    String rootKeyWordName = languageGrammar.rootKeyWord;
    Grammar rootKeyWord = languageGrammar.terminals.get(rootKeyWordName);
    if (null == rootKeyWord) {
      throw new AstRuntimeException(
          String.format("rootKeyWordName '%s' match grammar error.", rootKeyWordName));
    }
    keyWordAutomata.rootKeyWord = rootKeyWord;
  }
}
