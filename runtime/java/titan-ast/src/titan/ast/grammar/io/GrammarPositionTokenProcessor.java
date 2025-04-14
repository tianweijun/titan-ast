package titan.ast.grammar.io;

import java.util.List;

/**
 * .
 *
 * @author tian wei jun
 */
public class GrammarPositionTokenProcessor implements GrammarTokenProcessor {

  @Override
  public void process(List<GrammarToken> grammarTokens) {
    int indexOfByte = 0;
    for (GrammarToken token : grammarTokens) {
      token.start = indexOfByte;
      indexOfByte += token.text.length();
    }
  }
}
