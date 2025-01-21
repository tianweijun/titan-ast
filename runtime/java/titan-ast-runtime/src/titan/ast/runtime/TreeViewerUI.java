package titan.ast.runtime;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * .
 *
 * @author tian wei jun
 */
public class TreeViewerUI extends ComponentUI {
  private static final int FONT_SIZE = 12;
  private transient BoxTreeContext boxTreeContextCache;
  private Map desktopHints;

  public TreeViewerUI() {
    setFontHints();
  }

  public static ComponentUI createUI(JComponent c) {
    return new TreeViewerUI();
  }

  private void setFontHints() {
    Toolkit tk = Toolkit.getDefaultToolkit();
    desktopHints = (Map) (tk.getDesktopProperty("awt.font.desktophints"));
  }

  @Override
  public Dimension getPreferredSize(JComponent c) {
    TreeViewer treeViewer = (TreeViewer) c;
    BoxTreeContext boxTreeContext =
        createDrawTreeContext(treeViewer.getTreeViewerModel(), treeViewer.getGraphics());
    return new Dimension(boxTreeContext.width, boxTreeContext.height);
  }

  @Override
  public Dimension getMinimumSize(JComponent c) {
    return getPreferredSize(c);
  }

  @Override
  public Dimension getMaximumSize(JComponent c) {
    return getPreferredSize(c);
  }

  @Override
  public void paint(Graphics g, JComponent c) {
    Graphics2D g2d = (Graphics2D) g;
    // set local env
    Insets insets = c.getInsets();
    g2d.translate(insets.left, insets.top);
    int width = c.getWidth() - insets.left - insets.right;
    int height = c.getHeight() - insets.top - insets.bottom;
    // clear back
    g2d.setColor(c.getBackground());
    g2d.fillRect(0, 0, width, height);
    // draw tree
    drawBoxTree(g2d, (TreeViewer) c);
    // recover original env
    g2d.translate(-insets.left, -insets.top);
  }

  private void drawBoxTree(Graphics2D g2d, TreeViewer treeViewer) {
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    if (desktopHints != null) {
      g2d.addRenderingHints(desktopHints);
    }
    BoxTreeContext boxTreeContext = createDrawTreeContext(treeViewer.getTreeViewerModel(), g2d);
    if (null == boxTreeContext) {
      return;
    }
    g2d.setColor(Color.BLACK);
    g2d.setFont(boxTreeContext.font);
    drawBoxTree(g2d, boxTreeContext.boxTree);
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
    if (!children.isEmpty()) {
      Point fromPoint = box.getBottomCenterPoint();
      for (Box child : children) {
        Point topPoint = child.getTopCenterPoint();
        g2d.drawLine(fromPoint.x, fromPoint.y, topPoint.x, topPoint.y);
      }
    }
    if (!children.isEmpty()) {
      for (Box child : children) {
        drawBoxTree(g2d, child);
      }
    }
  }

  private BoxTreeContext createDrawTreeContext(TreeViewerModel treeViewerModel, Graphics g) {
    if (treeViewerModel.getStringTree() == null) {
      return null;
    }
    if (null != boxTreeContextCache
        && boxTreeContextCache.treeViewerModel.equalsByProperties(treeViewerModel)) {
      return boxTreeContextCache;
    }

    Font font = g.getFont();
    if (font == null) {
      font = new Font(null, Font.PLAIN, FONT_SIZE);
    }
    font = font.deriveFont(FONT_SIZE * treeViewerModel.getScale());
    FontMetrics fontMetrics = g.getFontMetrics(font);
    BoxTreeContext boxTreeContext =
        new BoxTreeContext(
            font, fontMetrics, treeViewerModel.getStringTree(), treeViewerModel.copyProperties());
    boxTreeContextCache = boxTreeContext;
    return boxTreeContext;
  }

  private static class HierarchicalRow {
    public int heightOfTree = 0;
    public int endOfRow = 0;
    public int height = 0;
    public List<Box> boxs = new LinkedList<>();

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

  private static class Box {
    HierarchicalRow row;
    int horizontalAxis = 0;
    int verticalAxis = 0;
    int width = 0;
    int height = 0;
    String text = "";
    LinkedList<Box> children = new LinkedList<>();

    public Box(String text, HierarchicalRow row, int width, int height) {
      this.text = text;
      this.row = row;
      this.width = width;
      this.height = height;
    }

    public Point getTopCenterPoint() {
      int x = horizontalAxis + width / 2;
      int y = verticalAxis - height;
      return new Point(x, y);
    }

    public Point getBottomCenterPoint() {
      int x = horizontalAxis + width / 2;
      int y = verticalAxis;
      return new Point(x, y);
    }
  }

  private static class BoxTreeContext {
    public final Font font;
    public final FontMetrics fontMetrics;
    public final int colLineHeight;
    public final int rowTextGap;
    public final int padding;
    public int width = 0;
    public int height = 0;
    public Box boxTree;
    List<HierarchicalRow> rows;
    TreeViewerModel treeViewerModel;

    public BoxTreeContext(
        Font font,
        FontMetrics fontMetrics,
        StringTree stringTree,
        TreeViewerModel treeViewerModel) {
      this.font = font;
      this.fontMetrics = fontMetrics;
      int fontHeight = fontMetrics.getAscent();
      colLineHeight = fontHeight * 2;
      rowTextGap = fontHeight;
      padding = fontHeight;
      this.treeViewerModel = treeViewerModel;
      build(stringTree);
    }

    private void build(StringTree stringTree) {
      initRows(stringTree.getHeight());
      boxTree = initBoxTree(stringTree, 0);
      initLocationOfBox();
      alignCenter(boxTree);
      setWidthAndHeight();
    }

    private void initRows(int height) {
      rows = new ArrayList<>(height);
      for (int heightOfTree = 1; heightOfTree <= height; heightOfTree++) {
        rows.add(new HierarchicalRow(heightOfTree));
      }
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
      // do align center
      int startOfChildRow = box.children.getFirst().horizontalAxis;
      Box lastChild = box.children.getLast();
      int endOfChildRow = lastChild.horizontalAxis + lastChild.width;
      int midOfChildRow = startOfChildRow + (endOfChildRow - startOfChildRow) / 2;
      int midOfThis = box.horizontalAxis + box.width / 2;
      if (midOfThis < midOfChildRow) { // 元素偏左，其之后的当前行box右移
        int thisMoveRight = midOfChildRow - midOfThis;
        moveRightBoxInRow(box, thisMoveRight);
      }
      if (midOfThis > midOfChildRow) { // 元素偏右,孩子右移
        int childMoveRight = midOfThis - midOfChildRow;
        HashSet<Integer> hasMovedRows = new HashSet<>();
        for (Box child : box.children) {
          moveRightOnceForEachRow(hasMovedRows, child, childMoveRight);
        }
      }
    }

    private void moveRightOnceForEachRow(HashSet<Integer> hasMovedRows, Box box, int moveRight) {
      if (!hasMovedRows.contains(box.row.heightOfTree)) {
        moveRightBoxInRow(box, moveRight);
        hasMovedRows.add(box.row.heightOfTree);
      }
      for (Box child : box.children) {
        moveRightOnceForEachRow(hasMovedRows, child, moveRight);
      }
    }

    private void moveRightBoxInRow(Box box, int moveRight) {
      List<Box> boxesInRow = box.row.boxs;
      Iterator<Box> boxesInRowIt = boxesInRow.iterator();
      while (boxesInRowIt.hasNext()) {
        Box boxInRow = boxesInRowIt.next();
        if (boxInRow == box) {
          boxInRow.horizontalAxis += moveRight;
          break;
        }
      }
      while (boxesInRowIt.hasNext()) {
        Box boxInRow = boxesInRowIt.next();
        boxInRow.horizontalAxis += moveRight;
      }
      HierarchicalRow hierarchicalRow = box.row;
      hierarchicalRow.endOfRow += moveRight;
    }

    /** 每一层每个元素同等间隔依次计算位置. */
    private void initLocationOfBox() {
      int fontHeight = fontMetrics.getAscent();
      for (int indexOfRow = 0; indexOfRow < rows.size(); indexOfRow++) {
        HierarchicalRow hierarchicalRow = rows.get(indexOfRow);
        int endOfRow = padding;
        int verticalAxis = padding + indexOfRow * (fontHeight + colLineHeight) + fontHeight;
        for (Box box : hierarchicalRow.boxs) {
          box.horizontalAxis = endOfRow;
          box.verticalAxis = verticalAxis;

          endOfRow += box.width + rowTextGap;
        }

        hierarchicalRow.endOfRow = endOfRow;
        hierarchicalRow.height = verticalAxis;
      }
    }

    private Box initBoxTree(StringTree strTree, int indexOfRow) {
      String text = strTree.text;
      HierarchicalRow hierarchicalRow = rows.get(indexOfRow);
      Box box = createBox(text, hierarchicalRow);
      hierarchicalRow.boxs.add(box);

      int indexOfChildrenRow = indexOfRow + 1;
      for (StringTree stringTreeChild : strTree.children) {
        Box boxOfChild = initBoxTree(stringTreeChild, indexOfChildrenRow);
        box.children.add(boxOfChild);
      }
      return box;
    }

    private Box createBox(String text, HierarchicalRow hierarchicalRow) {
      int width = fontMetrics.getAscent();
      if (StringUtils.isNotEmpty(text)) {
        width = fontMetrics.stringWidth(text);
      }
      return new Box(text, hierarchicalRow, width, fontMetrics.getAscent());
    }

    private void setWidthAndHeight() {
      for (HierarchicalRow hierarchicalRow : rows) {
        int currentWidth = hierarchicalRow.endOfRow + padding;
        if (currentWidth > width) {
          width = currentWidth;
        }
        int currentHeight = hierarchicalRow.height + padding;
        if (currentHeight > height) {
          height = currentHeight;
        }
      }
    }
  }
}
