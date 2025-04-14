package titan.ast.output.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;
import titan.ast.output.StringGraph;

/**
 * 显示图数据结构的组件，本组件在窗口中.
 *
 * @author tian wei jun
 */
public class GraphViewerJComponent extends JComponent {

  private static final int FONT_SIZE = 24;
  private final StringGraph strGraph;
  private int width = 800;
  private int height = 700;
  private final int gapOfBox = 150;
  private FontMetrics fontMetrics;
  private double scale = 1d;

  private final LinkedList<Box> boxs = new LinkedList<>();
  private final LinkedList<BoxLine> boxLines = new LinkedList<>();
  private final LinkedList<BoxLine> boxLineTips = new LinkedList<>();

  /** 初始化字段:font、fontMetrics、mouseListener. */
  public GraphViewerJComponent(StringGraph strGraph) {
    super();

    this.addMouseListener(new GraphViewerMouseInputListener());
    this.strGraph = strGraph;

    Font font = getFont();
    font = font == null ? new Font(null, Font.PLAIN, FONT_SIZE) : font;
    setFont(font);
    fontMetrics = this.getFontMetrics(font);
    createBoxs();
    refreshWidthAndHeight();
  }

  void showGraph() {
    invalidate();
    if (getParent() != null) {
      getParent().validate();
    }
    repaint();
  }

  @Override
  public void repaint() {
    this.setSize(getPreferredSize());
    super.repaint();
  }

  /**
   * 设置组件放大倍数并生效.
   *
   * @param scale 组件放大倍数
   */
  public void changeScale(double scale) {
    this.scale = scale;

    invalidate();
    if (getParent() != null) {
      getParent().validate();
    }
    repaint();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(
        Double.valueOf(width * 2).intValue(), Double.valueOf(height * 2).intValue());
  }

  void refreshWidthAndHeight() {
    // boxs
    int maxWidth = width;
    int maxHeight = height;
    for (Box box : boxs) {
      int newWidth = box.horizontalAxis + box.width + gapOfBox;
      int newHeight = box.verticalAxis + box.height + gapOfBox;
      if (newWidth > maxWidth) {
        maxWidth = newWidth;
      }
      if (newHeight > maxHeight) {
        maxHeight = newHeight;
      }
    }
    this.width = maxWidth;
    this.height = maxHeight;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;
    // font
    float fontSize = Double.valueOf(FONT_SIZE * scale).floatValue();
    Font newFont = g2d.getFont().deriveFont(fontSize);
    g2d.setFont(newFont);
    fontMetrics = g2d.getFontMetrics(newFont);
    // boxs
    for (Box box : boxs) {
      box.paintComponent(g2d);
    }

    // lines
    for (BoxLine boxLine : boxLines) {
      boxLine.paintComponent(g2d);
    }
    // boxLineTips
    for (BoxLine boxLineTip : boxLineTips) {
      boxLineTip.paintTip(g2d);
    }
  }

  /** 从左至右，依次排列，由box xy和text决定位置大小和边， 出度在右上角，入度在中底部，相同的边weight累加，当box 相关信息更新时，发生重绘. */
  private void createBoxs() {
    boxs.clear();

    int boxX = 5;
    int boxY = 60;
    HashMap<StringGraph.Node, Box> nodeBoxMap = new HashMap<>(strGraph.nodes.size());
    // boxs
    for (StringGraph.Node node : strGraph.nodes) {
      Box box = new Box(boxX, boxY, node.name);
      box.selfRegulate(fontMetrics);
      nodeBoxMap.put(node, box);
      this.boxs.add(box);
      if (boxX + box.width + gapOfBox * 2 >= width) { // 换行
        boxX = 5;
        boxY += gapOfBox;
      } else { // 同行下一个
        boxX = boxX + box.width + gapOfBox;
      }
    }
    // lines
    HashMap<Box2Box, BoxLine> box2BoxLineMap = new HashMap<>();
    for (StringGraph.Node fromNode : strGraph.nodes) {
      Box fromBox = nodeBoxMap.get(fromNode);
      fromNode.edges.forEach(
          (weight, weight2Nodes) -> {
            for (StringGraph.Node toNode : weight2Nodes) {
              Box toBox = nodeBoxMap.get(toNode);
              Box2Box box2BoxLineMapKey = new Box2Box(fromBox, toBox);
              BoxLine boxLine = box2BoxLineMap.get(box2BoxLineMapKey);
              if (null == boxLine) {
                boxLine = new BoxLine(fromBox, toBox);
                box2BoxLineMap.put(box2BoxLineMapKey, boxLine);
              }
              boxLine.tip = boxLine.tip + weight;
            }
          });
    }
    for (BoxLine boxLine : box2BoxLineMap.values()) {
      boxLines.add(boxLine);
    }
  }

  public static class Box2Box {

    public Box from;
    public Box to;

    public Box2Box(Box from, Box to) {
      this.from = from;
      this.to = to;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      Box2Box box2Box = (Box2Box) o;

      if (!Objects.equals(from, box2Box.from)) {
        return false;
      }
      return Objects.equals(to, box2Box.to);
    }

    @Override
    public int hashCode() {
      int result = from != null ? from.hashCode() : 0;
      result = 31 * result + (to != null ? to.hashCode() : 0);
      return result;
    }
  }

  public static class Box {

    static int borderWidth = 2;
    static int padingWidth = 2;
    int horizontalAxis = 0;
    int verticalAxis = 0;
    int width = 0;
    int height = 0;
    String text = "";

    public Box() {}

    /**
     * 带参初始化.
     *
     * @param x box横坐标
     * @param y box纵坐标
     * @param text box中的文字内容
     */
    public Box(int x, int y, String text) {
      this.horizontalAxis = x;
      this.verticalAxis = y;
      this.text = text;
    }

    private void selfRegulate(FontMetrics fontMetrics) {
      width = fontMetrics.stringWidth(text) + borderWidth * 2 + padingWidth * 2;
      height = fontMetrics.getAscent() + borderWidth * 2 + padingWidth * 2;
    }

    public void setXY(int x, int y) {
      this.horizontalAxis = x;
      this.verticalAxis = y;
    }

    public void paintComponent(Graphics2D g) {
      drawBox(g, this);
    }

    private void drawBox(Graphics2D g2d, Box box) {
      FontMetrics fontMetrics = g2d.getFontMetrics();
      selfRegulate(fontMetrics);
      // border
      Color borderColor = Color.black;
      g2d.setColor(borderColor);
      g2d.setStroke(new BasicStroke(Box.borderWidth));
      g2d.drawRect(box.horizontalAxis, box.verticalAxis, +box.width, box.height);
      // text
      Color fontColor = Color.black;
      g2d.setColor(fontColor);
      g2d.drawString(
          box.text,
          box.horizontalAxis + Box.borderWidth + Box.padingWidth,
          box.verticalAxis + fontMetrics.getAscent() + Box.borderWidth + Box.padingWidth);
    }

    @Override
    public String toString() {
      return text;
    }
  }

  public static class BoxLine {

    static int selefLineHeight = 20;
    public Box from;
    public Box to;
    String tip = "";

    public BoxLine(Box from, Box to) {
      this.from = from;
      this.to = to;
    }

    /**
     * 绘制box之间的线条.
     *
     * @param g2d 2d画笔
     */
    public void paintComponent(Graphics2D g2d) {
      Color fromLineColor = Color.black;
      Color toLineColor = Color.RED;
      if (from.equals(to)) {
        g2d.setColor(toLineColor);
        g2d.drawRect(
            from.horizontalAxis, from.verticalAxis - selefLineHeight, from.width, selefLineHeight);
      } else {
        int fromX = from.horizontalAxis + from.width;
        int fromY = from.verticalAxis;
        int toX = to.horizontalAxis;
        int toY = to.verticalAxis + to.height;
        int midX = (fromX + toX) / 2;
        int midY = (fromY + toY) / 2;
        g2d.setColor(fromLineColor);
        g2d.drawLine(fromX, fromY, midX, midY);
        g2d.setColor(toLineColor);
        g2d.drawLine(midX, midY, toX, toY);
      }
    }

    void paintTip(Graphics2D g2d) {
      int width = g2d.getFontMetrics().stringWidth(tip);
      int height = g2d.getFontMetrics().getAscent();
      int x = 0;
      int y = 0;
      int midX = 0;
      int midY;
      if (from.equals(to)) {
        midX = from.horizontalAxis + from.width / 2;
        x = midX - width / 2;
        y = from.verticalAxis;
      } else {
        midX = (from.horizontalAxis + from.width + to.horizontalAxis) / 2;
        midY = (from.verticalAxis + to.verticalAxis + to.height) / 2;
        x = midX - width / 2;
        y = midY + height / 2;
      }
      g2d.setColor(Color.black);
      g2d.drawString(tip, x, y);

      g2d.setColor(Color.GREEN);
      g2d.drawLine(x, y, x + width, y);
    }

    @Override
    public String toString() {
      return from.toString() + ":" + tip + ":" + to.toString();
    }
  }

  public static class GraphViewerMouseInputListener implements MouseInputListener {

    Box boxTarget = null;

    @Override
    public void mouseClicked(MouseEvent e) {
      GraphViewerJComponent graphViewerJComponent = (GraphViewerJComponent) e.getSource();
      LinkedList<BoxLine> boxLineTips = graphViewerJComponent.boxLineTips;
      BoxLine boxLine = getBoxLineByClicked(e);
      if (null != boxLine) {
        if (boxLineTips.contains(boxLine)) {
          boxLineTips.remove(boxLine);
        } else {
          boxLineTips.add(boxLine);
        }
        graphViewerJComponent.repaint();
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
      GraphViewerJComponent graphViewerJComponent = (GraphViewerJComponent) e.getSource();
      Box box = getBoxByPressed(e);
      if (null != box) {
        graphViewerJComponent.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        boxTarget = box;
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      GraphViewerJComponent graphViewerJComponent = (GraphViewerJComponent) e.getSource();
      if (boxTarget != null) {
        int x = e.getX();
        int y = e.getY();
        boxTarget.setXY(x, y);

        boxTarget = null;
        graphViewerJComponent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        graphViewerJComponent.repaint();
      }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    Box getBoxByPressed(MouseEvent e) {
      GraphViewerJComponent graphViewerJComponent = (GraphViewerJComponent) e.getSource();
      int x = e.getX();
      int y = e.getY();

      Box res = null;
      for (Box box : graphViewerJComponent.boxs) {
        if (x >= box.horizontalAxis
            && x <= box.horizontalAxis + box.width
            && y >= box.verticalAxis
            && y <= box.verticalAxis + box.height) {
          res = box;
          break;
        }
      }
      return res;
    }

    private BoxLine getBoxLineByClicked(MouseEvent e) {
      GraphViewerJComponent graphViewerJComponent = (GraphViewerJComponent) e.getSource();
      int x = e.getX();
      int y = e.getY();
      BoxLine res = null;

      Iterator<BoxLine> boxLinesIt = graphViewerJComponent.boxLines.iterator();
      while (boxLinesIt.hasNext()) {
        BoxLine boxLine = boxLinesIt.next();
        if (boxLine.from.equals(boxLine.to)) {
          Box box = boxLine.from;
          int minX = box.horizontalAxis;
          int maxX = box.horizontalAxis + box.width;
          int minY = box.verticalAxis - BoxLine.selefLineHeight;
          int maxY = box.verticalAxis;
          if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
            res = boxLine;
            break;
          }
        } else {
          int fromX = boxLine.from.horizontalAxis + boxLine.from.width;
          int fromY = boxLine.from.verticalAxis;
          int toX = boxLine.to.horizontalAxis;
          int toY = boxLine.to.verticalAxis + boxLine.to.height;
          if (isLineByThreePoint(fromX, fromY, toX, toY, x, y)) {
            res = boxLine;
            break;
          }
        }
      }
      return res;
    }

    private boolean isLineByThreePoint(int x1, int y1, int x2, int y2, int x3, int y3) {
      int maxX = x2;
      int minX = x1;
      int maxY = y2;
      int minY = y1;
      if (x2 < x1) {
        maxX = x1;
        minX = x2;
      }
      if (y2 < y1) {
        maxY = y1;
        minY = y2;
      }
      if (x3 < minX || x3 > maxX || y3 < minY || y3 > maxY) {
        return false;
      }
      int maxD = 6;
      if (x1 == x2) {
        int d = x3 - x1;
        d = d < 0 ? -d : d;
        return d <= maxD;
      }
      if (y1 == y2) {
        int d = y3 - y1;
        d = d < 0 ? -d : d;
        return d <= maxD;
      }
      double k = ((double) y2 - y1) / ((double) x2 - x1);
      double b = y1 - k * x1;
      double distanceTop = k * x3 - y3 + b;
      double ddBottom = k * k + 1;
      double dd = distanceTop * distanceTop / ddBottom;
      return dd <= maxD * maxD;
    }
  }
}
