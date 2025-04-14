package titan.ast.output;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * 窗口显示图所需的数据结构.
 *
 * @author tian wei jun
 */
public class StringGraph {

  public LinkedList<Node> nodes = new LinkedList<>();

  public static class Node {

    public String name = "";
    public Map<String, Set<Node>> edges = new HashMap<>();

    /**
     * 给当前节点（from）添加边.
     *
     * @param weight 节点之间边上的文字
     * @param toNode 箭头指向的节点
     */
    public void addEdge(String weight, Node toNode) {
      Set<Node> chToNodes = edges.get(weight);
      if (null == chToNodes) {
        chToNodes = new HashSet<>();
        edges.put(weight, chToNodes);
      }
      chToNodes.add(toNode);
    }

    /**
     * 计算并返回当前节点（from）通过边上的文字所能到达的节点.
     *
     * @param weight 节点之间边上的文字
     * @return 当前节点（from）通过边上的文字所能到达的节点
     */
    public Set<Node> getWeight2Nodes(String weight) {
      Set<Node> chToNodes = edges.get(weight);
      if (null == chToNodes) {
        chToNodes = new HashSet<>();
        edges.put(weight, chToNodes);
      }
      return chToNodes;
    }

    @Override
    public String toString() {
      return name;
    }
  }
}
