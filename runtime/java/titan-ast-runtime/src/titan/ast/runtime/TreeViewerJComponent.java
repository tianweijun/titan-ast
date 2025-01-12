package titan.ast.runtime;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 显示树数据结构的组件，本组件在窗口中.
 *
 * @author tian wei jun
 */
public class TreeViewerJComponent extends JComponent {

  private static final int FONT_SIZE = 16;
  private final StringTree stringTree;
  private JSlider scaleSlider;
  private JButton exportAsPngBtn;

  public TreeViewerJComponent(StringTree tree) {
    super();
    setLayout(null);
    setScaleSlider();
    setExportAsPngBtn();
    this.stringTree = tree;
  }

  public Container getContainer() {
    JPanel controlTreeViewPanel = new JPanel();
    controlTreeViewPanel.setLayout(new BoxLayout(controlTreeViewPanel, BoxLayout.PAGE_AXIS));
    controlTreeViewPanel.add(this.scaleSlider);
    controlTreeViewPanel.add(this.exportAsPngBtn);

    Container treePanel = new JPanel(new BorderLayout());
    treePanel.setBackground(Color.white);
    treePanel.add(new JScrollPane(this), BorderLayout.CENTER);
    treePanel.add(controlTreeViewPanel, BorderLayout.PAGE_END);
    return treePanel;
  }

  private void setExportAsPngBtn() {
    exportAsPngBtn = new JButton("Export as PNG");
    exportAsPngBtn.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            new JComponent2PicConverter().convert(TreeViewerJComponent.this, "tree");
          }
        });
    exportAsPngBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
  }

  private void setScaleSlider() {
    scaleSlider = new JSlider(JSlider.HORIZONTAL, -1000, 1000, 0);
    scaleSlider.addChangeListener(
        new ChangeListener() {
          @Override
          public void stateChanged(ChangeEvent e) {
            TreeViewerJComponent.this.invalidate();
            TreeViewerJComponent.this.getParent().repaint();
          }
        });
    scaleSlider.setAlignmentX(JComponent.CENTER_ALIGNMENT);
  }

  public float getScale() {
    return scaleSlider.getValue() / 1000.0f + 1.0f;
  }

  @Override
  public Dimension getPreferredSize() {
    BoxTreeContext boxTreeContext = createDrawTreeContext(stringTree);
    return new Dimension(boxTreeContext.width, boxTreeContext.height);
  }

  @Override
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  @Override
  public Dimension getMaximumSize() {
    return getPreferredSize();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    drawBoxTree((Graphics2D) g);
  }

  private void drawBoxTree(Graphics2D g2d) {
    BoxTreeContext boxTreeContext = createDrawTreeContext(stringTree);
    // 消除文字锯齿
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    // 消除画图锯齿
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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

  private BoxTreeContext createDrawTreeContext(StringTree strTree) {
    Font font = getFont();
    if (font == null) {
      font = new Font(null, Font.PLAIN, FONT_SIZE);
    }
    font = font.deriveFont(FONT_SIZE * this.getScale());
    FontMetrics fontMetrics = this.getFontMetrics(font);
    return new BoxTreeContext(font, fontMetrics, strTree);
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

    public BoxTreeContext(Font font, FontMetrics fontMetrics, StringTree stringTree) {
      this.font = font;
      this.fontMetrics = fontMetrics;
      int fontHeight = fontMetrics.getAscent();
      colLineHeight = fontHeight * 2;
      rowTextGap = fontHeight;
      padding = fontHeight;
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
}
