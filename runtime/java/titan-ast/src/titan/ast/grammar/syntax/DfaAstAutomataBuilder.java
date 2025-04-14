package titan.ast.grammar.syntax;

import titan.ast.AstContext;
import titan.ast.grammar.LanguageGrammar;

/**
 * 按照dfa方式构造一个语法树的自动机.
 *
 * @author tian wei jun
 */
public class DfaAstAutomataBuilder {

  public DfaAstAutomataBuilder() {}

  /**
   * 1.生成树形正则 2.正则转产生式 3.产生式转项目集的nfa状态，转单个语法节点nfa 4.语法节点合成一个巨大的nfa 5.nfa转dfa 6.构造自动机
   *
   * @return AstAutomata
   */
  public AstAutomata build() {
    NonterminalDfaBuilder nonterminalDfaBuilder = new NonterminalDfaBuilder();
    SyntaxDfa astDfa = nonterminalDfaBuilder.buildAstDfa();
    LanguageGrammar languageGrammar = AstContext.get().languageGrammar;
    languageGrammar.astDfa = astDfa;

    AstAutomata astAutomata =
        new DfaAstAutomataFactory(AstAutomataType.FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA)
            .build();
    languageGrammar.astAutomata = astAutomata;
    return astAutomata;
  }
}
