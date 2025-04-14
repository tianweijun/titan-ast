package titan.ast.grammar.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import titan.ast.grammar.Grammar;

/**
 * 只保留依赖的数据，真正的实现在runtime里面.
 *
 * @author tian wei jun
 */
public class DerivedTerminalGrammarAutomataData {
  public List<RootTerminalGrammarMap> rootTerminalGrammarMaps = new ArrayList<>();

  public boolean isEmpty() {
    return rootTerminalGrammarMaps.isEmpty();
  }

  public int sizeOfTextTerminalMap() {
    int size = 0;
    for (RootTerminalGrammarMap rootTerminalGrammarMap : rootTerminalGrammarMaps) {
      size += rootTerminalGrammarMap.textTerminalMap.size();
    }
    return size;
  }

  public static class RootTerminalGrammarMap {
    public Grammar rootTerminalGrammar; // not null
    public HashMap<String, Grammar> textTerminalMap; // not empty

    public RootTerminalGrammarMap(
        Grammar rootTerminalGrammar, HashMap<String, Grammar> textTerminalMap) {
      this.rootTerminalGrammar = rootTerminalGrammar;
      this.textTerminalMap = textTerminalMap;
    }
  }
}
