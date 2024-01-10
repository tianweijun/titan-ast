package titan.ast.runtime;

import java.util.HashMap;
import java.util.List;

/**
 * .
 *
 * @author tian wei jun
 */
public class KeyWordAutomata {
  public static final int EMPTY = 0;
  public static final int NOT_EMPTY = 1;

  public int emptyOrNot = EMPTY;

  public Grammar rootKeyWord = null;

  public HashMap<String, Grammar> textTerminalMap = new HashMap<>();

  public List<Token> buildToken(List<Token> tokens) {
    for (Token token : tokens) {
      if (token.terminal == rootKeyWord) {
        Grammar terminal = textTerminalMap.get(token.text);
        if (null != terminal) {
          token.terminal = terminal;
        }
      }
    }
    return tokens;
  }
}
