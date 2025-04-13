package titan.ast.fa.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import titan.ast.grammar.TerminalGrammar;

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

    public TerminalGrammar rootTerminalGrammar; // not null
    public HashMap<String, TerminalGrammar> textTerminalMap; // not empty

    public RootTerminalGrammarMap(
        TerminalGrammar rootTerminalGrammar, HashMap<String, TerminalGrammar> textTerminalMap) {
      this.rootTerminalGrammar = rootTerminalGrammar;
      this.textTerminalMap = textTerminalMap;
    }
  }
}
