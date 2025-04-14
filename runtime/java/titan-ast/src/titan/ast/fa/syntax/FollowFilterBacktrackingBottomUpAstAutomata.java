package titan.ast.fa.syntax;

import java.util.Map;
import java.util.Set;
import titan.ast.grammar.Grammar;

/**
 * 按照可回溯方式构造ast的自动机,增加了follow规约判定，只保留依赖的数据，真正的实现在runtime里面.
 *
 * @author tian wei jun
 */
public class FollowFilterBacktrackingBottomUpAstAutomata extends BacktrackingBottomUpAstAutomata {
  public Map<Grammar, Set<Grammar>> nonterminalFollowMap;
  public Grammar eof;

  public FollowFilterBacktrackingBottomUpAstAutomata(
      SyntaxDfa astDfa,
      Grammar startGrammar,
      Map<Grammar, Set<Grammar>> nonterminalFollowMap,
      Grammar eof) {
    super(astDfa, startGrammar);
    this.nonterminalFollowMap = nonterminalFollowMap;
    this.eof = eof;
  }

  @Override
  public AstAutomataType getType() {
    return AstAutomataType.FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA;
  }
}
