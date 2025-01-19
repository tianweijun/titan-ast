package titan.ast.grammar;

import java.util.HashMap;
import java.util.LinkedList;
import titan.ast.grammar.token.KeyWordAutomata;
import titan.ast.util.StringUtils;

/**
 * .
 *
 * @author tian wei jun
 */
public class KeyWordAutomataDetail {
  public String rootKeyWordGrammarName = null;
  public HashMap<Grammar, LinkedList<Grammar>> keyWords =
      new HashMap<Grammar, LinkedList<Grammar>>();
  public KeyWordAutomata keyWordAutomata = null;

  public boolean isEmpty() {
    return StringUtils.isBlank(rootKeyWordGrammarName) || keyWords.isEmpty();
  }

  public void updateRootKeyWordGrammarName(String rootKeyWordGrammarName) {
    this.rootKeyWordGrammarName = rootKeyWordGrammarName;
  }

  public void addKeyWord(Grammar keyWord) {
    LinkedList<Grammar> sameNameKeyWords = keyWords.get(keyWord);
    if (sameNameKeyWords == null) {
      sameNameKeyWords = new LinkedList<>();
      keyWords.put(keyWord, sameNameKeyWords);
    }
    sameNameKeyWords.add(keyWord);
  }
}
