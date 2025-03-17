package titan.ast.runtime;

import java.util.HashMap;
import java.util.List;

/**
 * .
 *
 * @author tian wei jun
 */
class KeyWordAutomata {
  static final int EMPTY = 0;
  static final int NOT_EMPTY = 1;

  int emptyOrNot = EMPTY;

  Grammar rootKeyWord = null;

  HashMap<String, Grammar> textTerminalMap = new HashMap<>();

  List<Token> buildToken(List<Token> tokens) {
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
