package titan.ast.runtime;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.swing.JComponent;

/**
 * 显示树数据结构的组件，本组件在窗口中.
 *
 * @author tian wei jun
 */
public class TreeViewerJComponent extends JComponent {

  private static final int FONT_SIZE = 16;
  private float scale = 1.0f;
  private StringTree stringTree;
  private int fontHeight;
  private int colLineHeight;
  private int rowTextGap;

  private BoxTreeContext boxTreeContext;

  private FontMetrics fontMetrics;

  public TreeViewerJComponent(StringTree tree) {
    super();
    this.stringTree = tree;

    initFontMetrics();
    // 设置宽高
    boxTreeContext = getDrawTreeContext(stringTree);
    setSize(boxTreeContext.width * 2, boxTreeContext.height * 2);
  }

  private void initFontMetrics() {
    Font font = getFont();
    font = font == null ? new Font(null, Font.PLAIN, FONT_SIZE) : font;
    setFont(font);
    fontMetrics = this.getFontMetrics(font);
  }

  /**
   * 设置组件放大倍数并生效.
   *
   * @param scale 组件放大倍数
   */
  public void changeScale(float scale) {
    this.scale = scale;
    boxTreeContext = getDrawTreeContext(stringTree);
    setSize(boxTreeContext.width * 2, boxTreeContext.height * 2);
    repaint();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(boxTreeContext.width * 2, boxTreeContext.height * 2);
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(Color.BLACK);
    // scale
    Font font = g2d.getFont().deriveFont(FONT_SIZE * scale);
    g2d.setFont(font);
    // 消除文字锯齿
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    // 消除画图锯齿
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    drawBoxTree(g2d, boxTreeContext.boxTree);
    // g2d.scale(scale, scale);
  }

  private BoxTreeContext getDrawTreeContext(StringTree strTree) {
    fontMetrics = this.getFontMetrics(getFont().deriveFont(FONT_SIZE * scale));

    fontHeight = fontMetrics.getAscent();
    colLineHeight = fontHeight * 2;
    rowTextGap = fontHeight;

    BoxTreeContext boxTreeContext = new BoxTreeContext();
    boxTreeContext.build(strTree);
    return boxTreeContext;
  }

  /**
   * 画文字和线.
   *
   * @param g2d 2d画笔
   * @param box 显示树数据结构对应的盒子
   */
  private void drawBoxTree(Graphics2D g2d, Box box) {
    if (!(null == box.text || box.text.length() <= 0)) {
      g2d.drawString(box.text, box.horizontalAxis, box.verticalAxis);
    }
    LinkedList<Box> children = box.children;
    if (children.size() > 0) {
      int fromOfLineX = box.horizontalAxis + box.width / 2;
      int fromOfLineY = box.verticalAxis;
      for (Box child : children) {
        int toOfLineX = child.horizontalAxis + child.width / 2;
        int toOfLineY = child.verticalAxis - fontHeight;
        g2d.drawLine(fromOfLineX, fromOfLineY, toOfLineX, toOfLineY);
      }
    }
    if (children.size() > 0) {
      for (Box child : children) {
        drawBoxTree(g2d, child);
      }
    }
  }

  public static class HierarchicalRow {
    public int heightOfTree = 0;
    public int endOfRow = 0;
    public int height = 0;

    public HierarchicalRow(int heightOfTree) {
      this.heightOfTree = heightOfTree;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      HierarchicalRow that = (HierarchicalRow) o;
      return heightOfTree == that.heightOfTree;
    }

    @Override
    public int hashCode() {
      return Objects.hash(heightOfTree);
    }
  }

  private class BoxTreeContext {
    public int width = 0;
    public int height = 0;
    public Box boxTree;
    private Map<HierarchicalRow, LinkedList<Box>> hierarchicalRowMap = new HashMap<>();

    private void build(StringTree stringTree) {
      hierarchicalRowMap.clear();
      boxTree = initBoxTree(stringTree, null, new HierarchicalRow(1));
      initLocationOfBox();
      alignCenter(boxTree);
      setWidthAndHeight();
    }

    /**
     * 按照左右中顺序遍历居中.
     *
     * @param box
     */
    private void alignCenter(Box box) {
      if (box.children.isEmpty()) {
        return;
      }
      for (Box childBox : box.children) {
        alignCenter(childBox);
      }
      int startOfChildRow = box.children.getFirst().horizontalAxis;
      Box childrenLast = box.children.getLast();
      int endOfChildRow = childrenLast.horizontalAxis + childrenLast.width;
      int midOfChildRow = startOfChildRow + (endOfChildRow - startOfChildRow) / 2;
      int midOfThis = box.horizontalAxis + box.width / 2;
      if (midOfThis < midOfChildRow) { // 元素偏左，其之后的当前行box右移
        int thisMoveRight = midOfChildRow - midOfThis;
        moveRightBoxInRow(box, thisMoveRight);
      }
      if (midOfThis > midOfChildRow) { // 元素偏右,孩子右移
        int childMoveRight = midOfThis - midOfChildRow;
        HashSet<HierarchicalRow> hasMovedRows = new HashSet<>();
        for (Box child : box.children) {
          moveRightOnceForEachRow(hasMovedRows, child, childMoveRight);
        }
      }
    }

    private void moveRightOnceForEachRow(
        HashSet<HierarchicalRow> hasMovedRows, Box box, int moveRight) {
      if (!hasMovedRows.contains(box.hierarchicalRow)) {
        moveRightBoxInRow(box, moveRight);
        hasMovedRows.add(box.hierarchicalRow);
      }
      for (Box child : box.children) {
        moveRightOnceForEachRow(hasMovedRows, child, moveRight);
      }
    }

    /** 每一层每个元素同等间隔依次计算位置. */
    private void initLocationOfBox() {
      int margin = fontHeight;
      Iterator<Entry<HierarchicalRow, LinkedList<Box>>> entryIt =
          hierarchicalRowMap.entrySet().iterator();
      while (entryIt.hasNext()) {
        Entry<HierarchicalRow, LinkedList<Box>> entry = entryIt.next();
        HierarchicalRow hierarchicalRow = entry.getKey();
        LinkedList<Box> boxesInRow = entry.getValue();
        int endInRow = margin;
        int verticalAxis =
            margin + (hierarchicalRow.heightOfTree - 1) * (fontHeight + colLineHeight) + fontHeight;
        for (Box box : boxesInRow) {
          box.horizontalAxis = endInRow;
          box.verticalAxis = verticalAxis;

          endInRow += box.width;
          endInRow += rowTextGap;
        }
        hierarchicalRow.endOfRow = endInRow;
        hierarchicalRow.height = verticalAxis;
      }
    }

    private void addBoxToHierarchicalRowMap(HierarchicalRow hierarchicalRow, Box box) {
      LinkedList<Box> boxesInRow = hierarchicalRowMap.get(hierarchicalRow);
      if (null == boxesInRow) {
        boxesInRow = new LinkedList<>();
        hierarchicalRowMap.put(hierarchicalRow, boxesInRow);
      }
      boxesInRow.add(box);
    }

    private Box initBoxTree(StringTree strTree, Box parent, HierarchicalRow hierarchicalRow) {
      String text = strTree.text;
      Box box = new Box(text, parent, hierarchicalRow);
      addBoxToHierarchicalRowMap(hierarchicalRow, box);

      HierarchicalRow hierarchicalRowOfChildTree =
          new HierarchicalRow(hierarchicalRow.heightOfTree + 1);
      for (StringTree stringTreeChild : strTree.children) {
        Box boxOfChild = initBoxTree(stringTreeChild, box, hierarchicalRowOfChildTree);
        box.children.add(boxOfChild);
      }
      return box;
    }

    private void moveRightBoxInRow(Box box, int moveRight) {
      boolean shouldMoveRight = false;
      LinkedList<Box> boxesInRow = hierarchicalRowMap.get(box.hierarchicalRow);
      Iterator<Box> boxesInRowIt = boxesInRow.iterator();
      while (boxesInRowIt.hasNext()) {
        Box boxInRow = boxesInRowIt.next();
        if (boxInRow == box) {
          shouldMoveRight = true;
          boxInRow.horizontalAxis += moveRight;
          break;
        }
      }
      if (shouldMoveRight) {
        while (boxesInRowIt.hasNext()) {
          Box boxInRow = boxesInRowIt.next();
          boxInRow.horizontalAxis += moveRight;
        }
        box.hierarchicalRow.endOfRow += moveRight;
      }
    }

    private void setWidthAndHeight() {
      int maxHeight = 0;
      int maxWidth = 0;
      for (HierarchicalRow hierarchicalRow : hierarchicalRowMap.keySet()) {
        if (hierarchicalRow.height > maxHeight) {
          maxHeight = hierarchicalRow.height;
        }
        if (hierarchicalRow.endOfRow > maxWidth) {
          maxWidth = hierarchicalRow.endOfRow;
        }
      }
      this.height = maxHeight + fontHeight;
      this.width = maxWidth;
    }
  }

  private class Box {
    HierarchicalRow hierarchicalRow = null;
    int horizontalAxis = 0;
    int verticalAxis = 0;
    int width = 0;
    int height = 0;
    String text = "";
    Box parent;
    LinkedList<Box> children = new LinkedList();

    public Box(String text, Box parent, HierarchicalRow hierarchicalRow) {
      this.parent = parent;
      this.text = text;
      this.height = fontHeight;
      this.hierarchicalRow = hierarchicalRow;
      if (null == text || text.length() <= 0) {
        this.width = fontHeight;
      } else {
        this.width = fontMetrics.stringWidth(text);
      }
    }
  }
}
