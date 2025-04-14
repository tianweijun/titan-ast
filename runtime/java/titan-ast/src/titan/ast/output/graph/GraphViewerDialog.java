package titan.ast.output.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import titan.ast.output.JComponent2PicConverter;
import titan.ast.output.StringGraph;

/**
 * 显示图数据结构的窗口.
 *
 * @author tian wei jun
 */
public class GraphViewerDialog {

  public final JFrame dialog = new JFrame();
  private final GraphViewerJComponent graphViewer;

  /**
   * 带参初始化.
   *
   * @param strGraph 用字符表示图的数据结构
   */
  public GraphViewerDialog(StringGraph strGraph) {
    graphViewer = new GraphViewerJComponent(strGraph);
    setDiyLayout();
  }

  public void setTitle(String title) {
    dialog.setTitle(title);
  }

  private void setDiyLayout() {
    dialog.setPreferredSize(new Dimension(900, 800));
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    final Container mainPane = dialog.getContentPane();
    mainPane.setLayout(new BorderLayout(5, 5));
    final Container contentPane = new JPanel(new BorderLayout(0, 0));
    contentPane.setBackground(Color.white);

    JScrollPane graphWrapper = new JScrollPane(graphViewer);
    contentPane.add(graphWrapper, BorderLayout.CENTER);

    JPanel contentBottomPanel = new JPanel(new BorderLayout(0, 0));

    final JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL, -1000, 1000, 0);
    contentBottomPanel.add(scaleSlider, BorderLayout.CENTER);
    scaleSlider.addChangeListener(
        new ChangeListener() {
          @Override
          public void stateChanged(ChangeEvent e) {
            int v = scaleSlider.getValue();
            graphViewer.changeScale(v / 1000.0 + 1.0);
          }
        });

    JPanel btnWrapper = new JPanel(new FlowLayout());
    contentBottomPanel.add(btnWrapper, BorderLayout.SOUTH);
    contentPane.add(contentBottomPanel, BorderLayout.SOUTH);

    JButton exportAsPngBtn = new JButton("Export as PNG");
    exportAsPngBtn.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            new JComponent2PicConverter().convert(graphViewer, "graph");
          }
        });
    btnWrapper.add(exportAsPngBtn);

    mainPane.add(contentPane, BorderLayout.CENTER);
  }

  /**
   * 显示窗口.
   *
   * @return 窗口
   */
  public JFrame show() {
    dialog.pack();
    dialog.setVisible(true);
    graphViewer.showGraph();
    return dialog;
  }

  /**
   * 新建线程异步打开窗口.
   *
   * @return 异步运算结果是窗口
   */
  public Future<JFrame> open() {
    Callable<JFrame> callable =
        new Callable<JFrame>() {
          JFrame result;

          @Override
          public JFrame call() throws Exception {
            SwingUtilities.invokeAndWait(
                new Runnable() {
                  @Override
                  public void run() {
                    result = GraphViewerDialog.this.show();
                  }
                });

            return result;
          }
        };

    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
      return executor.submit(callable);
    } finally {
      executor.shutdown();
    }
  }
}
