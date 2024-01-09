package titan.ast.grammar.token;

import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;

/**
 * .
 *
 * @author tian wei jun
 */
public class KeyWordContextBuilder {

  void build(LanguageGrammar languageGrammar) {
    // 将keyword 添加到 terminals
    for (Grammar keyWord : languageGrammar.keyWords) {
      languageGrammar.addGrammar(keyWord);
    }
  }
}
