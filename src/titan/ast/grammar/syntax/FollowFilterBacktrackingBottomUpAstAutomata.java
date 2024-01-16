package titan.ast.grammar.syntax;

import java.util.Map;
import java.util.Set;
import titan.ast.grammar.Grammar;

/**
 * 按照可回溯方式构造ast的自动机.
 *
 * @author tian wei jun
 */
public class FollowFilterBacktrackingBottomUpAstAutomata extends BacktrackingBottomUpAstAutomata {
  Map<Grammar, Set<Grammar>> nonterminalFollowMap;
  Grammar eof;

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
  void reduceBottomUpBranch(BacktrackingBottomUpBranch bottomUpBranch) {
    ReducingSymbol topReducingSymbol = bottomUpBranch.reducingSymbols.getLast();
    tokenReducingSymbolInputStream.nextReadIndex = topReducingSymbol.endIndexOfToken + 1;

    Grammar terminalOfNextToken = null; // 下一个token的语法名字
    if (tokenReducingSymbolInputStream.hasReadAll()) { // token读完了,相当于eof
      terminalOfNextToken = eof;
    } else {
      terminalOfNextToken = tokenReducingSymbolInputStream.read().terminal;
    }
    // 还有token没有规约，判断token是不是在follow集里决定是否规约
    for (ProductionRule closingProductionRule :
        topReducingSymbol.currentDfaState.closingProductionRules) {
      Grammar nonterminal = closingProductionRule.grammar;
      Set<Grammar> follow = nonterminalFollowMap.get(nonterminal);
      if (follow.contains(terminalOfNextToken)) {
        doReduce(bottomUpBranch, closingProductionRule);
      }
    }
  }
}
