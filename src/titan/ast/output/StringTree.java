package titan.ast.output;

import java.util.List;

/**
 * 窗口显示树所需的数据结构.
 *
 * @author tian wei jun
 */
public class StringTree {

  public StringTree parent;
  public List<StringTree> children;
  public String text;

  /**
   * 带参初始化.
   *
   * @param parent 上一级节点
   * @param children 子节点
   * @param text 当前节点内容
   */
  public StringTree(StringTree parent, List<StringTree> children, String text) {
    this.parent = parent;
    this.children = children;
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }
}
