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

  public FollowFilterBacktrackingBottomUpAstAutomata(
      SyntaxDfa astDfa, Grammar startGrammar, Map<Grammar, Set<Grammar>> nonterminalFollowMap) {
    super(astDfa, startGrammar);
    this.nonterminalFollowMap = nonterminalFollowMap;
  }

  /*
  @Override
  void reduceBottomUpBranch(BacktrackingBottomUpBranch bottomUpBranch) {
    ReducingSymbol topReducingSymbol = bottomUpBranch.reducingSymbols.getLast();
    tokenReducingSymbolInputStream.nextReadIndex = topReducingSymbol.endIndexOfToken + 1;

    SyntaxDfaState currentDfaState = topReducingSymbol.currentDfaState;
    if (tokenReducingSymbolInputStream.hasReadAll()) { // token已经读取完了，尝试规约
      for (ProductionRule closingProductionRule : currentDfaState.closingProductionRules) {
        doReduce(bottomUpBranch, closingProductionRule);
      }
      return;
    }
    // 还有token没有规约，判断token是不是在follow集里决定是否规约
    Token nextToken = tokenReducingSymbolInputStream.read();
    Grammar followOfText = nextToken.terminal;
    for (ProductionRule closingProductionRule : currentDfaState.closingProductionRules) {
      Grammar nonterminal = closingProductionRule.grammar;
      Set<Grammar> follow = nonterminalFollowMap.get(nonterminal);
      if (follow.contains(followOfText)) {
        doReduce(bottomUpBranch, closingProductionRule);
      } else {
        ++count;
        doReduce(bottomUpBranch, closingProductionRule);
      }
    }
  }*/
}
