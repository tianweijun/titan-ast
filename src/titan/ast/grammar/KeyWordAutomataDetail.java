package titan.ast.grammar;

import java.util.LinkedHashSet;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.token.KeyWordAutomata;
import titan.ast.util.StringUtils;

/**
 * .
 *
 * @author tian wei jun
 */
public class KeyWordAutomataDetail {
  public String rootKeyWordGrammarName = null;
  public LinkedHashSet<Grammar> keyWords = new LinkedHashSet<>();
  public KeyWordAutomata keyWordAutomata = null;

  public boolean isEmpty() {
    return StringUtils.isBlank(rootKeyWordGrammarName) || keyWords.isEmpty();
  }

  public void updateRootKeyWordGrammarName(String rootKeyWordGrammarName) {
    this.rootKeyWordGrammarName = rootKeyWordGrammarName;
  }

  public void addKeyWord(Grammar keyWord) {
    if (keyWords.contains(keyWord)) {
      throw new AstRuntimeException(
          String.format("name of grammar '%s' is not unique.", keyWord.name));
    }
    keyWords.add(keyWord);
  }
}
