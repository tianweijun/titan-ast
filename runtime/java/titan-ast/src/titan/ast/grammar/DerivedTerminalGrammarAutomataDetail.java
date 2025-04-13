package titan.ast.grammar;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import titan.ast.fa.token.DerivedTerminalGrammarAutomataData;

/**
 * .
 *
 * @author tian wei jun
 */
public class DerivedTerminalGrammarAutomataDetail {

  public DerivedTerminalGrammarAutomataData derivedTerminalGrammarAutomataData =
      new DerivedTerminalGrammarAutomataData();
  public Map<String, RootTerminalGrammarMapDetail> rootTerminalGrammarMaps = new HashMap<>();

  public boolean isEmpty() {
    boolean isEmpty = true;
    for (RootTerminalGrammarMapDetail rootTerminalGrammarMapDetail :
        rootTerminalGrammarMaps.values()) {
      if (!rootTerminalGrammarMapDetail.isEmpty()) {
        isEmpty = false;
        break;
      }
    }
    return isEmpty;
  }

  public RootTerminalGrammarMapDetail getRootTerminalGrammarMap(String rootTerminalGrammar) {
    RootTerminalGrammarMapDetail rootTerminalGrammarMapDetail =
        rootTerminalGrammarMaps.get(rootTerminalGrammar);
    if (rootTerminalGrammarMapDetail == null) {
      rootTerminalGrammarMapDetail = new RootTerminalGrammarMapDetail(rootTerminalGrammar);
      rootTerminalGrammarMaps.put(rootTerminalGrammar, rootTerminalGrammarMapDetail);
    }
    return rootTerminalGrammarMapDetail;
  }

  public static class RootTerminalGrammarMapDetail {

    public HashMap<TerminalGrammar, LinkedList<TerminalGrammar>> derivedTerminalGrammars =
        new HashMap<TerminalGrammar, LinkedList<TerminalGrammar>>();
    String rootTerminalGrammar;

    public RootTerminalGrammarMapDetail(String rootTerminalGrammar) {
      this.rootTerminalGrammar = rootTerminalGrammar;
    }

    boolean isEmpty() {
      return rootTerminalGrammar == null || derivedTerminalGrammars.isEmpty();
    }

    public void addTerminalGrammar(TerminalGrammar derivedTerminalGrammar) {
      LinkedList<TerminalGrammar> sameDerivedTerminalGrammars =
          derivedTerminalGrammars.get(derivedTerminalGrammar);
      if (sameDerivedTerminalGrammars == null) {
        sameDerivedTerminalGrammars = new LinkedList<>();
        derivedTerminalGrammars.put(derivedTerminalGrammar, sameDerivedTerminalGrammars);
      }
      sameDerivedTerminalGrammars.add(derivedTerminalGrammar);
    }
  }
}
