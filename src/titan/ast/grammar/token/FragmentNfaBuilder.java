package titan.ast.grammar.token;

import java.util.LinkedHashMap;
import titan.ast.grammar.Grammar;

/**
 * 构造正则片段的nfa.
 *
 * @author tian wei jun
 */
public class FragmentNfaBuilder {

  LinkedHashMap<String, Grammar> terminalFragments;

  public FragmentNfaBuilder(LinkedHashMap<String, Grammar> terminalFragments) {
    this.terminalFragments = terminalFragments;
  }

  public void build() {
    buildRegExp();
    buildNfa();
  }

  public void buildNfa() {
    Reg2TokenNfaConverter reg2TokenNfaConverter = new Reg2TokenNfaConverter();
    reg2TokenNfaConverter.addTasks(terminalFragments);
    reg2TokenNfaConverter.convert();
  }

  public void buildRegExp() {
    TokenRegExpBuilder regExpBuilder = new TokenRegExpBuilder();
    regExpBuilder.addTasks(terminalFragments);
    regExpBuilder.addSources(terminalFragments);

    regExpBuilder.build();
  }
}
