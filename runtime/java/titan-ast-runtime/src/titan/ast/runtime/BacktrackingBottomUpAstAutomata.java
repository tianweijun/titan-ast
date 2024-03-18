package titan.ast.runtime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

/**
 * 按照可回溯方式构造ast的自动机.
 *
 * @author tian wei jun
 */
public class BacktrackingBottomUpAstAutomata implements AstAutomata {

  SyntaxDfa astDfa = null;
  Grammar startGrammar = null;
  TokenReducingSymbolInputStream tokenReducingSymbolInputStream = null;
  TreeSet<BacktrackingBottomUpBranch> bottomUpBranchs =
      new TreeSet<>(new BacktrackingBottomUpBranchComparator());
  TreeSet<BacktrackingBottomUpBranch> triedBottomUpBranchs =
      new TreeSet<>(new BacktrackingBottomUpBranchComparator());
  AutomataTmpAst result = null;

  public BacktrackingBottomUpAstAutomata(SyntaxDfa astDfa, Grammar startGrammar) {
    this.astDfa = astDfa;
    this.startGrammar = startGrammar;
  }

  @Override
  public AstAutomataType getType() {
    return AstAutomataType.BACKTRACKING_BOTTOM_UP_AST_AUTOMATA;
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
    while (result == null && !bottomUpBranchs.isEmpty()) {
      consumeBottomUpBranch();
    }

    if (result == null) {
      throw new AstRuntimeException(getNoResultErrorInfo());
    }

    Ast ret = result.toAst();
    clear();
    return ret;
  }

  void consumeBottomUpBranch() {
    BacktrackingBottomUpBranch bottomUpBranch = bottomUpBranchs.pollFirst();

    if (triedBottomUpBranchs.contains(bottomUpBranch)) {
      return;
    }

    BacktrackingBottomUpBranch triedBottomUpBranch = bottomUpBranch.clone();
    triedBottomUpBranchs.add(triedBottomUpBranch);

    if (isAcceptedBottomUpBranch(bottomUpBranch)) {
      result = bottomUpBranch.reducingSymbols.getLast().astOfCurrentDfaState;
      return;
    }
    reduceBottomUpBranch(bottomUpBranch);
    shiftBottomUpBranch(bottomUpBranch);
  }

  void reduceBottomUpBranch(BacktrackingBottomUpBranch bottomUpBranch) {
    ReducingSymbol topReducingSymbol = bottomUpBranch.reducingSymbols.getLast();
    SyntaxDfaState currentDfaState = topReducingSymbol.currentDfaState;
    for (ProductionRule closingProductionRule : currentDfaState.closingProductionRules) {
      doReduce(bottomUpBranch, closingProductionRule);
    }
  }

  void doReduce(BacktrackingBottomUpBranch bottomUpBranch, ProductionRule closingProductionRule) {
    int endIndexOfToken = bottomUpBranch.reducingSymbols.getLast().endIndexOfToken;
    // 空归约
    if (FaStateType.isClosingTag(closingProductionRule.reducingDfa.start.type)) {
      SyntaxDfaState topReducingSymbolDfaState =
          bottomUpBranch.reducingSymbols.getLast().currentDfaState;
      SyntaxDfaState nextDfaState =
          topReducingSymbolDfaState.edges.get(closingProductionRule.grammar);
      // 连通的
      if (null != nextDfaState) {
        // 归约的符号
        ReducingSymbol nonterminalReducingSymbol = new ReducingSymbol();
        nonterminalReducingSymbol.astOfCurrentDfaState =
            new AutomataTmpAst(closingProductionRule.grammar, closingProductionRule.alias);
        nonterminalReducingSymbol.currentDfaState = nextDfaState;
        nonterminalReducingSymbol.endIndexOfToken = endIndexOfToken;
        // 归约的符号进栈
        BacktrackingBottomUpBranch newBottomUpBranch = bottomUpBranch.clone();
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
          reducingProductionRuleDfaState.edges.get(
              inputReducingSymbol.astOfCurrentDfaState.grammar);
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
          // 被归约的符号出栈，同时建立语法树孩子节点
          AutomataTmpAst reducingAst =
              new AutomataTmpAst(closingProductionRule.grammar, closingProductionRule.alias);
          for (int countOfReducingSymbols = 1;
              countOfReducingSymbols <= countOfComsumedReducingSymbol;
              countOfReducingSymbols++) {
            ReducingSymbol childReducingSymbol = newBottomUpBranch.reducingSymbols.removeLast();
            // 不用克隆孩子语法树，因为newBottomUpBranch已经克隆了原来分支的符号栈信息，
            // 现在又把这些产生式孩子元素丢弃，故这些孩子语法树直接拿来用，使其不被回收
            reducingAst.children.addFirst(childReducingSymbol.astOfCurrentDfaState);
          }
          // 归约的符号
          ReducingSymbol nonterminalReducingSymbol = new ReducingSymbol();
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

  void shiftBottomUpBranch(BacktrackingBottomUpBranch bottomUpBranch) {
    ReducingSymbol topReducingSymbol = bottomUpBranch.reducingSymbols.getLast();
    // 将输入流定位到分支读取的位置
    tokenReducingSymbolInputStream.nextReadIndex = topReducingSymbol.endIndexOfToken + 1;
    // 移进一个token
    if (tokenReducingSymbolInputStream.hasNext()) {
      Token token = tokenReducingSymbolInputStream.read();
      SyntaxDfaState nextDfaState = topReducingSymbol.currentDfaState.edges.get(token.terminal);
      // 连通的
      if (null != nextDfaState) {
        // 归约的符号
        ReducingSymbol terminalReducingSymbol = new ReducingSymbol();
        terminalReducingSymbol.astOfCurrentDfaState = new AutomataTmpAst(token);
        terminalReducingSymbol.currentDfaState = nextDfaState;
        terminalReducingSymbol.endIndexOfToken = tokenReducingSymbolInputStream.nextReadIndex - 1;
        // 归约的符号进栈
        BacktrackingBottomUpBranch terminalBottomUpBranch = bottomUpBranch.clone();
        terminalBottomUpBranch.reducingSymbols.addLast(terminalReducingSymbol);
        addNewBacktrackingBottomUpBranch(terminalBottomUpBranch);
      }
    }
  }

  boolean isAcceptedBottomUpBranch(BacktrackingBottomUpBranch bottomUpBranch) {
    ReducingSymbol topReducingSymbol = bottomUpBranch.reducingSymbols.getLast();
    tokenReducingSymbolInputStream.nextReadIndex = topReducingSymbol.endIndexOfToken + 1;
    // 可接受状态:栈中有两个归约，栈底是基准标志，栈顶是归约结果，并且源文件输入流全部识别了
    return tokenReducingSymbolInputStream.hasReadAll()
        && bottomUpBranch.reducingSymbols.size() == 2
        && startGrammar.equals(topReducingSymbol.astOfCurrentDfaState.grammar);
  }

  /**
   * 增广文法，相当于一个连通标志，主要作用是开始文法归约连通判断.
   *
   * @return ReducingSymbol
   */
  private ReducingSymbol getConnectedSignOfStartGrammarReducingSymbol() {
    ReducingSymbol connectedSignOfStartGrammarReducingSymbol = new ReducingSymbol();
    connectedSignOfStartGrammarReducingSymbol.astOfCurrentDfaState =
        new AutomataTmpAst(startGrammar, ""); // 应该是augmentedNonterminal,简化为startGrammar
    connectedSignOfStartGrammarReducingSymbol.endIndexOfToken = -1;
    connectedSignOfStartGrammarReducingSymbol.currentDfaState = astDfa.start;
    return connectedSignOfStartGrammarReducingSymbol;
  }

  private void addNewBacktrackingBottomUpBranch(
      BacktrackingBottomUpBranch newBacktrackingBottomUpBranch) {
    if (triedBottomUpBranchs.contains(newBacktrackingBottomUpBranch)) {
      return;
    }
    if (bottomUpBranchs.add(newBacktrackingBottomUpBranch)) {
      if (triedBottomUpBranchs.isEmpty()) {
        return;
      }
      int minEndIndexOfTask = bottomUpBranchs.first().reducingSymbols.getLast().endIndexOfToken;
      int minEndIndexOfTriedBranch =
          triedBottomUpBranchs.first().reducingSymbols.getLast().endIndexOfToken;
      while (minEndIndexOfTriedBranch < minEndIndexOfTask) {
        triedBottomUpBranchs.pollFirst();
        if (triedBottomUpBranchs.isEmpty()) {
          break;
        }
        minEndIndexOfTriedBranch =
            triedBottomUpBranchs.first().reducingSymbols.getLast().endIndexOfToken;
      }
    }
  }

  private void clear() {
    tokenReducingSymbolInputStream = null;
    bottomUpBranchs.clear();
    triedBottomUpBranchs.clear();
    result = null;
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
   * 错误信息最开始处，错误信息所有可能的范围.
   *
   * @return error info
   */
  private String getNoResultErrorInfo() {
    ArrayList<Token> tokenReducingSymbols = tokenReducingSymbolInputStream.tokenReducingSymbols;
    if (tokenReducingSymbols.isEmpty()) {
      return "";
    }

    int indexOfLastToken = tokenReducingSymbols.size() - 1;

    int startIndexOfToken = indexOfLastToken;
    int endIndexOfToken = 0;
    for (BacktrackingBottomUpBranch branch : triedBottomUpBranchs) {
      int lastIndexOfBranch = branch.reducingSymbols.getLast().endIndexOfToken;
      if (lastIndexOfBranch < 0) {
        lastIndexOfBranch = 0;
      }
      if (startIndexOfToken > lastIndexOfBranch) { // 错误开始处尽量小
        startIndexOfToken = lastIndexOfBranch;
      }
      if (endIndexOfToken < lastIndexOfBranch) { // 错误结束处尽量大
        endIndexOfToken = lastIndexOfBranch;
      }
    }
    if (endIndexOfToken + 1 <= indexOfLastToken) { // 如果还有下一个token，将他加入过来，错误信息必须涵盖可能的位置。
      endIndexOfToken += 1;
    }
    int startIndexByte = 0;
    int endIndexByte = 0;

    Token startToken = tokenReducingSymbols.get(startIndexOfToken);
    Token endToken = tokenReducingSymbols.get(endIndexOfToken);
    startIndexByte = startToken.start;
    endIndexByte = endToken.start + endToken.text.length();

    StringBuilder tokenInfo = new StringBuilder();
    for (int indexOfToken = startIndexOfToken; indexOfToken <= endIndexOfToken; indexOfToken++) {
      Token token = tokenReducingSymbols.get(indexOfToken);
      tokenInfo.append(token.text).append(" ");
    }
    tokenInfo.delete(tokenInfo.length() - 1, tokenInfo.length());

    return String.format(
        "generate ast failed,error near [%d,%d):%s",
        startIndexByte, endIndexByte, tokenInfo.toString());
  }

  public static class BacktrackingBottomUpBranchComparator
      implements Comparator<BacktrackingBottomUpBranch> {

    @Override
    public int compare(BacktrackingBottomUpBranch o1, BacktrackingBottomUpBranch o2) {
      return o1.compareTo(o2);
    }
  }
}
