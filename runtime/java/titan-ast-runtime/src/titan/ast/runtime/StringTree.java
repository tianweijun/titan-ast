package titan.ast.runtime;

import java.util.List;

/**
 * 窗口显示树所需的数据结构.
 *
 * @author tian wei jun
 */
public class StringTree {
  public String text;
  public List<StringTree> children;

  /**
   * 带参初始化.
   *
   * @param text 当前节点内容
   * @param children 子节点
   */
  public StringTree(String text, List<StringTree> children) {
    this.text = text;
    this.children = children;
  }

  public int getHeight() {
    return getMaxHeight(this, 1, 1);
  }

  private int getMaxHeight(StringTree stringTree, int height, int currentHeight) {
    if (currentHeight > height) {
      height = currentHeight;
    }
    int childHeight = currentHeight + 1;
    for (StringTree child : stringTree.children) {
      int maxHeightOfChild = getMaxHeight(child, height, childHeight);
      if (maxHeightOfChild > height) {
        height = maxHeightOfChild;
      }
    }
    return height;
  }

  @Override
  public String toString() {
    return text;
  }
}
