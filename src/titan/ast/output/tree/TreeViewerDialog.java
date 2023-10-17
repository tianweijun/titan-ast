package titan.ast.output.tree;

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
import titan.ast.output.StringTree;

/**
 * 显示树数据结构的窗口.
 *
 * @author tian wei jun
 */
public class TreeViewerDialog {

  public final JFrame dialog = new JFrame();
  private final TreeViewerJComponent treeViewer;
  // private final TreeDirectoryJComponent treeDirectoryViewer = new TreeDirectoryJComponent();
  private JScrollPane treeViewerScrollPane;

  public TreeViewerDialog(StringTree tree) {
    treeViewer = new TreeViewerJComponent(tree);
    treeViewer.setLayout(null);
    setDiyLayout();
  }

  public void setTitle(String title) {
    dialog.setTitle(title);
  }

  private void setDiyLayout() {
    dialog.setPreferredSize(new Dimension(900, 800));
    dialog.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    final Container mainPane = dialog.getContentPane();
    mainPane.setLayout(new BorderLayout(5, 5));
    final Container contentPane = new JPanel(new BorderLayout(0, 0));
    contentPane.setBackground(Color.white);

    treeViewerScrollPane = new JScrollPane(treeViewer);
    treeViewerScrollPane.setPreferredSize(new Dimension(800, 600));
    contentPane.add(treeViewerScrollPane, BorderLayout.CENTER);

    JPanel contentBottomPanel = new JPanel(new BorderLayout(0, 0));
    final JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL, -1000, 1000, 0);
    contentBottomPanel.add(scaleSlider, BorderLayout.CENTER);
    scaleSlider.addChangeListener(
        new ChangeListener() {
          @Override
          public void stateChanged(ChangeEvent e) {
            int v = scaleSlider.getValue();
            treeViewer.changeScale(v / 1000.0f + 1.0f);
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
            new JComponent2PicConverter().convert(treeViewer, "tree");
          }
        });
    btnWrapper.add(exportAsPngBtn);
    /*
    JPanel treeDirectoryPanel = new JPanel(new BorderLayout(5, 5));
    treeDirectoryPanel.add(new JScrollPane(treeDirectoryViewer));


    final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            treeDirectoryPanel, contentPane);

    mainPane.add(splitPane, BorderLayout.CENTER);
    */
    mainPane.add(contentPane, BorderLayout.CENTER);
  }

  /**
   * 显示窗口.
   *
   * @return 窗口
   */
  public JFrame show() {
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    treeViewerScrollPane.repaint();
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
                    result = TreeViewerDialog.this.show();
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
