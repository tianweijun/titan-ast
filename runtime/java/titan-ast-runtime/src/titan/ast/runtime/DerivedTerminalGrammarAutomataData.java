package titan.ast.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * .
 *
 * @author tian wei jun
 */
class DerivedTerminalGrammarAutomataData {

  int count = 0;
  List<RootTerminalGrammarMap> rootTerminalGrammarMaps = new ArrayList<>();

  static class RootTerminalGrammarMap {
    Grammar rootTerminalGrammar;
    HashMap<String, Grammar> textTerminalMap;

    public RootTerminalGrammarMap(
        Grammar rootTerminalGrammar, HashMap<String, Grammar> textTerminalMap) {
      this.rootTerminalGrammar = rootTerminalGrammar;
      this.textTerminalMap = textTerminalMap;
    }
  }
}
