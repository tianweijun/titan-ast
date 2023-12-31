package titan.ast.grammar.syntax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import titan.ast.grammar.FaStateType;
import titan.ast.grammar.Grammar;
import titan.ast.target.Ast;
import titan.ast.target.AutomataTmpAst;
import titan.ast.target.Token;

/**
 * 按照可回溯方式构造ast的自动机.
 *
 * @author tian wei jun
 */
public class BacktrackingBottomUpAstAutomata implements AstAutomata {
  SyntaxDfa astDfa = null;
  Grammar startGrammar = null;
  TokenReducingSymbolInputStream tokenReducingSymbolInputStream = null;
  LinkedList<BacktrackingBottomUpBranch> bottomUpBranchs = new LinkedList<>();
  HashSet<BacktrackingBottomUpBranch> triedBottomUpBranchs = new HashSet<>();
  LinkedList<AutomataTmpAst> result = new LinkedList<>();
  private HashSet<BacktrackingBottomUpBranch> bottomUpBranchsShadow = new HashSet<>();

  public BacktrackingBottomUpAstAutomata(SyntaxDfa astDfa, Grammar startGrammar) {
    this.astDfa = astDfa;
    this.startGrammar = startGrammar;
  }

  /**
   * 按照所有可能的产生式归约.
   *
   * @param sourceTokens token数据流
   * @return
   */
  @Override
  public Ast buildAst(List<Token> sourceTokens) {
    init(sourceTokens);
    while (result.isEmpty() && !bottomUpBranchs.isEmpty()) {
      consumeBottomUpBranch();
    }
    Ast ret = result.isEmpty() ? null : result.getFirst().toAst();
    clear();
    return ret;
  }

  @Override
  public List<Ast> buildAsts(List<Token> sourceTokens) {
    init(sourceTokens);
    while (!bottomUpBranchs.isEmpty()) {
      consumeBottomUpBranch();
    }
    List<Ast> ret = new ArrayList<>(result.size());
    for (AutomataTmpAst tmpAst : result) {
      ret.add(tmpAst.toAst());
    }
    clear();
    return ret;
  }

  private void consumeBottomUpBranch() {
    BacktrackingBottomUpBranch bottomUpBranch = bottomUpBranchs.getFirst();
    bottomUpBranchs.removeFirst();
    bottomUpBranchsShadow.remove(bottomUpBranch);
    if (triedBottomUpBranchs.contains(bottomUpBranch)) {
      return;
    }
    BacktrackingBottomUpBranch triedBottomUpBranch = bottomUpBranch.clone();
    switch (bottomUpBranch.status) {
      case CREATED:
        reduceBottomUpBranch(bottomUpBranch);
        bottomUpBranchs.addFirst(bottomUpBranch);
        bottomUpBranchsShadow.add(bottomUpBranch);
        break;
      case REDUCED:
        shiftBottomUpBranch(bottomUpBranch);
        bottomUpBranchs.addFirst(bottomUpBranch);
        bottomUpBranchsShadow.add(bottomUpBranch);
        break;
      case SHIFTED:
        closeBottomUpBranch(bottomUpBranch);
        bottomUpBranchs.addFirst(bottomUpBranch);
        bottomUpBranchsShadow.add(bottomUpBranch);
        break;
      case NON_ACCEPTED:
        break;
      case ACCEPTED:
        result.add(bottomUpBranch.reducingSymbols.getLast().astOfCurrentDfaState);
        break;
      default:
    }
    triedBottomUpBranchs.add(triedBottomUpBranch);
  }

  private void shiftBottomUpBranch(BacktrackingBottomUpBranch bottomUpBranch) {
    ReducingSymbol topReducingSymbol = bottomUpBranch.reducingSymbols.getLast();
    // 将输入流定位到分支读取的位置
    tokenReducingSymbolInputStream.nextReadIndex = topReducingSymbol.endIndexOfToken + 1;
    // 移进一个token
    if (tokenReducingSymbolInputStream.hasNext()) {
      Token token = tokenReducingSymbolInputStream.read();
      SyntaxDfaState nextDfaState = topReducingSymbol.currentDfaState.edges.get(token.terminal);
      // 连通的
      if (null != nextDfaState) {
        BacktrackingBottomUpBranch terminalBottomUpBranch = bottomUpBranch.clone();
        terminalBottomUpBranch.status = BacktrackingBottomUpBranch.Status.CREATED;
        // 归约的符号
        ReducingSymbol terminalReducingSymbol = new ReducingSymbol();
        terminalReducingSymbol.reducedGrammar = token.terminal;
        terminalReducingSymbol.astOfCurrentDfaState = new AutomataTmpAst(token);
        terminalReducingSymbol.currentDfaState = nextDfaState;
        terminalReducingSymbol.endIndexOfToken = tokenReducingSymbolInputStream.nextReadIndex - 1;
        // 归约的符号进栈
        terminalBottomUpBranch.reducingSymbols.addLast(terminalReducingSymbol);
        addNewBacktrackingBottomUpBranch(terminalBottomUpBranch);
      }
    }
    bottomUpBranch.status = BacktrackingBottomUpBranch.Status.SHIFTED;
  }

  private void reduceBottomUpBranch(BacktrackingBottomUpBranch bottomUpBranch) {
    ReducingSymbol topReducingSymbol = bottomUpBranch.reducingSymbols.getLast();
    SyntaxDfaState currentDfaState = topReducingSymbol.currentDfaState;
    if (!currentDfaState.closingProductionRules.isEmpty()) {
      for (ProductionRule closingProductionRule : currentDfaState.closingProductionRules) {
        doReduce(bottomUpBranch, closingProductionRule);
      }
    }
    bottomUpBranch.status = BacktrackingBottomUpBranch.Status.REDUCED;
  }

  private void doReduce(
      BacktrackingBottomUpBranch bottomUpBranch, ProductionRule closingProductionRule) {
    int endIndexOfToken = bottomUpBranch.reducingSymbols.getLast().endIndexOfToken;
    // 空归约
    if (FaStateType.isClosingTag(closingProductionRule.reducingDfa.start.type)) {
      SyntaxDfaState topReducingSymbolDfaState =
          bottomUpBranch.reducingSymbols.getLast().currentDfaState;
      SyntaxDfaState nextDfaState =
          topReducingSymbolDfaState.edges.get(closingProductionRule.grammar);
      // 连通的
      if (null != nextDfaState) {
        BacktrackingBottomUpBranch newBottomUpBranch = bottomUpBranch.clone();
        newBottomUpBranch.status = BacktrackingBottomUpBranch.Status.CREATED;
        // 归约的符号
        ReducingSymbol nonterminalReducingSymbol = new ReducingSymbol();
        nonterminalReducingSymbol.reducedGrammar = closingProductionRule.grammar;
        nonterminalReducingSymbol.astOfCurrentDfaState =
            new AutomataTmpAst(closingProductionRule.grammar, closingProductionRule.alias);
        nonterminalReducingSymbol.currentDfaState = nextDfaState;
        nonterminalReducingSymbol.endIndexOfToken = endIndexOfToken;
        // 归约的符号进栈
        newBottomUpBranch.reducingSymbols.addLast(nonterminalReducingSymbol);
        addNewBacktrackingBottomUpBranch(newBottomUpBranch);
      }
    }
    // 非空归约
    SyntaxDfaState reducingProductionRuleDfaState = closingProductionRule.reducingDfa.start;
    int countOfComsumedReducingSymbol = 0;
    ListIterator<ReducingSymbol> reducingSymbolListIt =
        bottomUpBranch.reducingSymbols.listIterator(bottomUpBranch.reducingSymbols.size());
    while (reducingSymbolListIt.hasPrevious()) {
      // 读取一个归约符号
      ReducingSymbol inputReducingSymbol = reducingSymbolListIt.previous();
      ++countOfComsumedReducingSymbol;
      if (!reducingSymbolListIt.hasPrevious()) { // 栈顶都没有，直接结束
        break;
      }
      SyntaxDfaState nextReducingProductionRuleDfaState =
          reducingProductionRuleDfaState.edges.get(inputReducingSymbol.reducedGrammar);
      if (null == nextReducingProductionRuleDfaState) { // 无法按照产生式向前归约，结束
        break;
      }
      if (FaStateType.isClosingTag(nextReducingProductionRuleDfaState.type)) {
        SyntaxDfaState topReducingSymbolDfaState = reducingSymbolListIt.previous().currentDfaState;
        reducingSymbolListIt.next(); // 回到原来的位置，等价于peek了前一个元素
        SyntaxDfaState nextDfaState =
            topReducingSymbolDfaState.edges.get(closingProductionRule.grammar);
        // 连通的
        if (null != nextDfaState) {
          BacktrackingBottomUpBranch newBottomUpBranch = bottomUpBranch.clone();
          newBottomUpBranch.status = BacktrackingBottomUpBranch.Status.CREATED;
          // 被归约的符号出栈，同时建立语法树孩子节点
          AutomataTmpAst reducingAst =
              new AutomataTmpAst(closingProductionRule.grammar, closingProductionRule.alias);
          for (int countOfReducingSymbol = 1;
              countOfReducingSymbol <= countOfComsumedReducingSymbol;
              countOfReducingSymbol++) {
            ReducingSymbol childReducingSymbol = newBottomUpBranch.reducingSymbols.removeLast();
            // 不用克隆孩子语法树，因为newBottomUpBranch已经克隆了原来分支的符号栈信息，
            // 现在又把这些产生式孩子元素丢弃，故这些孩子语法树直接拿来用，使其不被回收
            reducingAst.children.addFirst(childReducingSymbol.astOfCurrentDfaState);
          }
          // 归约的符号
          ReducingSymbol nonterminalReducingSymbol = new ReducingSymbol();
          nonterminalReducingSymbol.reducedGrammar = closingProductionRule.grammar;
          nonterminalReducingSymbol.endIndexOfToken = endIndexOfToken;
          nonterminalReducingSymbol.currentDfaState = nextDfaState;
          nonterminalReducingSymbol.astOfCurrentDfaState = reducingAst;
          // 归约的符号进栈
          newBottomUpBranch.reducingSymbols.addLast(nonterminalReducingSymbol);
          addNewBacktrackingBottomUpBranch(newBottomUpBranch);
        }
      }
      reducingProductionRuleDfaState = nextReducingProductionRuleDfaState;
    }
  }

  private void closeBottomUpBranch(BacktrackingBottomUpBranch bottomUpBranch) {
    ReducingSymbol topReducingSymbol = bottomUpBranch.reducingSymbols.getLast();
    tokenReducingSymbolInputStream.nextReadIndex = topReducingSymbol.endIndexOfToken + 1;
    bottomUpBranch.status = BacktrackingBottomUpBranch.Status.NON_ACCEPTED;
    // 可接受状态:栈中有两个归约，栈底是基准标志，栈顶是归约结果，并且源文件输入流全部识别了
    if (tokenReducingSymbolInputStream.hasReadAll()
        && bottomUpBranch.reducingSymbols.size() == 2
        && startGrammar.equals(topReducingSymbol.reducedGrammar)) {
      bottomUpBranch.status = BacktrackingBottomUpBranch.Status.ACCEPTED;
    }
  }

  private void clear() {
    tokenReducingSymbolInputStream = null;
    bottomUpBranchs.clear();
    bottomUpBranchsShadow.clear();
    triedBottomUpBranchs.clear();
    result.clear();
  }

  private void init(List<Token> sourceTokens) {
    clear();
    tokenReducingSymbolInputStream = new TokenReducingSymbolInputStream(sourceTokens);

    ReducingSymbol connectedSignOfStartGrammarReducingSymbol =
        getConnectedSignOfStartGrammarReducingSymbol();

    BacktrackingBottomUpBranch beginningBottomUpBranch = new BacktrackingBottomUpBranch();
    beginningBottomUpBranch.reducingSymbols.addLast(connectedSignOfStartGrammarReducingSymbol);

    addNewBacktrackingBottomUpBranch(beginningBottomUpBranch);
  }

  /**
   * 增广文法，相当于一个连通标志，主要作用是开始文法归约连通判断.
   *
   * @return ReducingSymbol
   */
  private ReducingSymbol getConnectedSignOfStartGrammarReducingSymbol() {
    ReducingSymbol connectedSignOfStartGrammarReducingSymbol = new ReducingSymbol();
    connectedSignOfStartGrammarReducingSymbol.reducedGrammar = startGrammar;
    connectedSignOfStartGrammarReducingSymbol.astOfCurrentDfaState =
        new AutomataTmpAst(startGrammar, null);
    connectedSignOfStartGrammarReducingSymbol.endIndexOfToken = -1;
    connectedSignOfStartGrammarReducingSymbol.currentDfaState = astDfa.start;
    return connectedSignOfStartGrammarReducingSymbol;
  }

  private void addNewBacktrackingBottomUpBranch(
      BacktrackingBottomUpBranch newBacktrackingBottomUpBranch) {
    if (triedBottomUpBranchs.contains(newBacktrackingBottomUpBranch)) {
      return;
    }
    if (!bottomUpBranchsShadow.contains(newBacktrackingBottomUpBranch)) {
      bottomUpBranchs.addFirst(newBacktrackingBottomUpBranch);
      bottomUpBranchsShadow.add(newBacktrackingBottomUpBranch);
    }
  }
}
