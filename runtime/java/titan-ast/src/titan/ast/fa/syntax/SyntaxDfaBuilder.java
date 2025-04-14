package titan.ast.fa.syntax;

import titan.ast.AstContext;
import titan.ast.grammar.NonterminalGrammar;

/**
 * .
 *
 * @author tian wei jun
 */
public class SyntaxDfaBuilder {

  public void build() {
    SyntaxNfa nfa = getAugmentedNonterminalNfa();
    Nfa2SyntaxDfaConverter nfa2DfaConverter = new Nfa2SyntaxDfaConverter();
    SyntaxDfa dfa = nfa2DfaConverter.convert(nfa);
    dfa = new SyntaxDfaOptimizer().optimize(dfa);
    AstContext.get().astDfa = dfa;
  }

  private SyntaxNfa getAugmentedNonterminalNfa() {
    AstContext astContext = AstContext.get();
    NonterminalGrammar augmentedNonterminal = AstContext.get().languageGrammar.augmentedNonterminal;
    return astContext.nonterminalProductionRulesMap.get(augmentedNonterminal).get(0).nfa;
  }
}
