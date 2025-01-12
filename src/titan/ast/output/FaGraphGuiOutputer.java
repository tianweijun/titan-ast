package titan.ast.output;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import javax.swing.JFrame;
import titan.ast.grammar.FaStateType;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.io.GrammarCharset;
import titan.ast.grammar.syntax.ProductionRule;
import titan.ast.grammar.syntax.SyntaxDfa;
import titan.ast.grammar.syntax.SyntaxDfaState;
import titan.ast.grammar.syntax.SyntaxNfa;
import titan.ast.grammar.syntax.SyntaxNfaState;
import titan.ast.grammar.token.TokenDfa;
import titan.ast.grammar.token.TokenDfaState;
import titan.ast.grammar.token.TokenNfa;
import titan.ast.grammar.token.TokenNfaState;
import titan.ast.output.graph.GraphViewerDialog;
import titan.ast.util.StringUtils;

/**
 * 图形化显示自动机.
 *
 * @author tian wei jun
 */
public class FaGraphGuiOutputer {

  /**
   * 显示非确定有限状态机.
   *
   * @param nfa 非确定有限状态机
   */
  public void outputTokenNfa(TokenNfa nfa) {
    if (null == nfa) {
      return;
    }
    StringGraph strGraph = new TokenNfa2StringGraphConverter(nfa).convert();
    Grammar terminal = nfa.end.terminal;
    String title = "TokenNfa";
    if (null != terminal) {
      if (StringUtils.isNotBlank(terminal.name)) {
        title = terminal.name + " TokenNfa";
      }
    }
    GraphViewerDialog graphViewerDialog = new GraphViewerDialog(strGraph);
    graphViewerDialog.setTitle(title);
    Future<JFrame> dialogFuture = graphViewerDialog.open();
    try {
      dialogFuture.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 显示确定有限状态机.
   *
   * @param dfa 确定有限状态机
   */
  public void outputTokenDfa(TokenDfa dfa) {
    if (null == dfa) {
      return;
    }
    StringGraph strGraph = new TokenDfa2StringGraphConverter(dfa).convert();
    GraphViewerDialog graphViewerDialog = new GraphViewerDialog(strGraph);
    graphViewerDialog.setTitle("TokenDfa");
    Future<JFrame> dialogFuture = graphViewerDialog.open();
    try {
      dialogFuture.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void outputSyntaxDfa(SyntaxDfa dfa) {
    if (null == dfa) {
      return;
    }
    StringGraph strGraph = new SyntaxDfa2StringGraphConverter(dfa).convert();
    GraphViewerDialog graphViewerDialog = new GraphViewerDialog(strGraph);
    graphViewerDialog.setTitle("SyntaxDfa");
    Future<JFrame> dialogFuture = graphViewerDialog.open();
    try {
      dialogFuture.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void outputSyntaxNfa(SyntaxNfa syntaxNfa) {
    if (null == syntaxNfa) {
      return;
    }
    StringGraph strGraph = new SyntaxNfa2StringGraphConverter(syntaxNfa).convert();
    GraphViewerDialog graphViewerDialog = new GraphViewerDialog(strGraph);
    graphViewerDialog.setTitle("SyntaxNfa");
    Future<JFrame> dialogFuture = graphViewerDialog.open();
    try {
      dialogFuture.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static class SyntaxNfa2StringGraphConverter {

    StringGraph stringGraph;
    SyntaxNfa source;
    Map<SyntaxNfaState, StringGraph.Node> sourceGraphStateMap;
    Set<SyntaxNfaState> sourceStates;

    public SyntaxNfa2StringGraphConverter(SyntaxNfa source) {
      this.source = source;
      stringGraph = new StringGraph();
    }

    /**
     * 找到所有状态，映射新旧状态，clone新旧状态.
     *
     * @return 窗口显示树所需的数据结构
     */
    public StringGraph convert() {
      sourceStates = source.getStates();
      createAndMapSourceGraphStates();
      cloneEdges();
      return stringGraph;
    }

    private void cloneEdges() {
      for (SyntaxNfaState fromState : sourceStates) {
        StringGraph.Node fromNode = sourceGraphStateMap.get(fromState);
        // edges
        fromState.edges.forEach(
            (grammarCh, toStates) -> {
              String ch = grammarCh.name;
              Set<StringGraph.Node> ch2Nodes = fromNode.getWeight2Nodes(ch);
              for (SyntaxNfaState toState : toStates) {
                StringGraph.Node toNode = sourceGraphStateMap.get(toState);
                ch2Nodes.add(toNode);
              }
            });
      }
    }

    private void createAndMapSourceGraphStates() {
      sourceGraphStateMap = new HashMap<>();
      LinkedList<StringGraph.Node> nodes = stringGraph.nodes;
      for (SyntaxNfaState sourceState : sourceStates) {
        StringGraph.Node stringGraphNode = new StringGraph.Node();
        if (FaStateType.isClosingTag(sourceState.type)) {
          stringGraphNode.name = sourceState.productionRule.grammar.name;
        } else {
          stringGraphNode.name = String.valueOf(sourceState.id);
        }
        nodes.add(stringGraphNode);
        sourceGraphStateMap.put(sourceState, stringGraphNode);
      }
    }
  }

  public static class SyntaxDfa2StringGraphConverter {

    SyntaxDfa dfa;
    Set<SyntaxDfaState> sourceStates;
    Map<SyntaxDfaState, StringGraph.Node> sourceGraphStateMap;
    StringGraph stringGraph;

    SyntaxDfa2StringGraphConverter(SyntaxDfa dfa) {
      this.dfa = dfa;
      stringGraph = new StringGraph();
    }

    /**
     * 将确定有限状态自动机 转为 窗口显示树所需的数据结构.
     *
     * @return 窗口显示树所需的数据结构
     */
    public StringGraph convert() {
      sourceStates = dfa.getStates();
      createAndMapSourceGraphStates();
      cloneEdges();
      return stringGraph;
    }

    private void createAndMapSourceGraphStates() {
      sourceGraphStateMap = new HashMap<>();
      LinkedList<StringGraph.Node> nodes = stringGraph.nodes;
      for (SyntaxDfaState sourceState : sourceStates) {
        StringGraph.Node stringGraphNode = new StringGraph.Node();
        stringGraphNode.name = getInfoByState(sourceState);
        nodes.add(stringGraphNode);
        sourceGraphStateMap.put(sourceState, stringGraphNode);
      }
    }

    private String getInfoByState(SyntaxDfaState state) {
      if (!FaStateType.isClosingTag(state.type)) {
        return Integer.toString(state.id);
      }
      Set<String> names = new HashSet<>();
      if (FaStateType.isClosingTag(state.type)) {
        for (ProductionRule productionRule : state.closingProductionRules) {
          names.add(productionRule.grammar.name);
        }
      }
      StringBuilder namesStrBuilder = new StringBuilder();
      for (String name : names) {
        namesStrBuilder.append("[");
        namesStrBuilder.append(name);
        namesStrBuilder.append("]");
      }
      return namesStrBuilder.toString();
    }

    private void cloneEdges() {
      Iterator<SyntaxDfaState> sourceStatesIt = sourceStates.iterator();
      while (sourceStatesIt.hasNext()) {
        SyntaxDfaState fromState = sourceStatesIt.next();
        StringGraph.Node fromNode = sourceGraphStateMap.get(fromState);
        // edges
        fromState.edges.forEach(
            (grammarCh, toState) -> {
              String ch = grammarCh.name;
              StringGraph.Node toNode = sourceGraphStateMap.get(toState);
              Set<StringGraph.Node> ch2Nodes = fromNode.getWeight2Nodes(ch);
              ch2Nodes.add(toNode);
            });
      }
    }
  }

  public static class TokenDfa2StringGraphConverter {

    TokenDfa dfa;
    Set<TokenDfaState> sourceStates;
    Map<TokenDfaState, StringGraph.Node> sourceGraphStateMap;
    StringGraph stringGraph;

    TokenDfa2StringGraphConverter(TokenDfa dfa) {
      this.dfa = dfa;
      stringGraph = new StringGraph();
    }

    /**
     * 将确定有限状态自动机 转为 窗口显示树所需的数据结构.
     *
     * @return 窗口显示树所需的数据结构
     */
    public StringGraph convert() {
      sourceStates = dfa.getStates();
      createAndMapSourceGraphStates();
      cloneEdges();
      return stringGraph;
    }

    private void createAndMapSourceGraphStates() {
      sourceGraphStateMap = new HashMap<>();
      LinkedList<StringGraph.Node> nodes = stringGraph.nodes;
      for (TokenDfaState sourceState : sourceStates) {
        StringGraph.Node stringGraphNode = new StringGraph.Node();
        if (FaStateType.isClosingTag(sourceState.type)) {
          stringGraphNode.name = sourceState.terminal.name;
        } else {
          stringGraphNode.name = String.valueOf(sourceState.id);
        }
        nodes.add(stringGraphNode);
        sourceGraphStateMap.put(sourceState, stringGraphNode);
      }
    }

    private void cloneEdges() {
      Iterator<TokenDfaState> sourceStatesIt = sourceStates.iterator();
      while (sourceStatesIt.hasNext()) {
        TokenDfaState fromState = sourceStatesIt.next();
        StringGraph.Node fromNode = sourceGraphStateMap.get(fromState);
        // edges
        fromState.edges.forEach(
            (integerCh, toState) -> {
              String ch = new String(GrammarCharset.getDisplayingChars(integerCh));
              StringGraph.Node toNode = sourceGraphStateMap.get(toState);
              Set<StringGraph.Node> ch2Nodes = fromNode.getWeight2Nodes(ch);
              ch2Nodes.add(toNode);
            });
      }
    }
  }

  public static class TokenNfa2StringGraphConverter {

    StringGraph stringGraph;
    TokenNfa source;
    Map<TokenNfaState, StringGraph.Node> sourceGraphStateMap;
    Set<TokenNfaState> sourceStates;

    public TokenNfa2StringGraphConverter(TokenNfa source) {
      this.source = source;
      stringGraph = new StringGraph();
    }

    /**
     * 找到所有状态，映射新旧状态，clone新旧状态.
     *
     * @return 窗口显示树所需的数据结构
     */
    public StringGraph convert() {
      sourceStates = source.getStates();
      createAndMapSourceGraphStates();
      cloneEdges();
      return stringGraph;
    }

    private void cloneEdges() {
      for (TokenNfaState fromState : sourceStates) {
        StringGraph.Node fromNode = sourceGraphStateMap.get(fromState);
        // edges
        fromState.edges.forEach(
            (integerCh, toStates) -> {
              String ch = new String(GrammarCharset.getDisplayingChars(integerCh));
              Set<StringGraph.Node> ch2Nodes = fromNode.getWeight2Nodes(ch);
              for (TokenNfaState toState : toStates) {
                StringGraph.Node toNode = sourceGraphStateMap.get(toState);
                ch2Nodes.add(toNode);
              }
            });
      }
    }

    private void createAndMapSourceGraphStates() {
      sourceGraphStateMap = new HashMap<>();
      LinkedList<StringGraph.Node> nodes = stringGraph.nodes;
      for (TokenNfaState sourceState : sourceStates) {
        StringGraph.Node stringGraphNode = new StringGraph.Node();
        if (FaStateType.isClosingTag(sourceState.type)) {
          stringGraphNode.name = sourceState.terminal.name;
        } else {
          stringGraphNode.name = String.valueOf(sourceState.id);
        }
        nodes.add(stringGraphNode);
        sourceGraphStateMap.put(sourceState, stringGraphNode);
      }
    }
  }
}
