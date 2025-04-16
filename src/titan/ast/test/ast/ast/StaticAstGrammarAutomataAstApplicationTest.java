package titan.ast.test.ast.ast;

import titan.ast.impl.ast.automata.StaticAstGrammarAutomataAstApplication;

public class StaticAstGrammarAutomataAstApplicationTest {
  public static void main(String[] args) {
    StaticAstGrammarAutomataAstApplication staticAstGrammarAutomataAstApplication =
        new StaticAstGrammarAutomataAstApplication("");
    staticAstGrammarAutomataAstApplication.buildPersistentAutomata(
        "D:\\github-pro\\titan\\titan-ast\\src\\resources\\titanAstGrammar.automata");
  }
}
