package titan.ast.fa.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.TerminalGrammar;

/**
 * 只保留依赖的数据，真正的实现在runtime里面.
 *
 * @author tian wei jun
 */
public class DerivedTerminalGrammarAutomataData {

  public List<RootTerminalGrammarMap> rootTerminalGrammarMaps = new ArrayList<>();
  public LinkedHashMap<String, TerminalGrammar> derivedTerminalGrammars = new LinkedHashMap<>();

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

  public void verifyTexts() {
    int sizeOfTexts = sizeOfTextTerminalMap();
    HashSet<String> texts = new HashSet<>(sizeOfTexts);
    for (RootTerminalGrammarMap rootTerminalGrammarMap : rootTerminalGrammarMaps) {
      for (Entry<String, TerminalGrammar> entry : rootTerminalGrammarMap.textTerminalMap.entrySet()) {
        String text = entry.getKey();
        if (texts.contains(text)) {
          TerminalGrammar derivedTerminalGrammar = entry.getValue();
          throw new AstRuntimeException(
              String.format("terminal grammar %s : text(%s) is not unique.", derivedTerminalGrammar.name, text));
        } else {
          texts.add(text);
        }
      }
    }
  }

  public TerminalGrammar getDerivedTerminalGrammarByText(String text) {
    for (RootTerminalGrammarMap rootTerminalGrammarMap : rootTerminalGrammarMaps) {
      TerminalGrammar terminalGrammar = rootTerminalGrammarMap.textTerminalMap.get(text);
      if (null != terminalGrammar) {
        return terminalGrammar;
      }
    }
    return null;
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
