package titan.ast.fa.token;

import java.util.LinkedList;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;

/**
 * 构造正则片段的nfa.
 *
 * @author tian wei jun
 */
public class FragmentNfaBuilder {

  public static void buildNfa() {
    LinkedList<Grammar> tasks = new LinkedList<>(AstContext.get().languageGrammar.terminalFragments.values());
    Reg2TokenNfaConverter reg2TokenNfaConverter = new Reg2TokenNfaConverter(tasks);
    reg2TokenNfaConverter.convert();
  }
}
