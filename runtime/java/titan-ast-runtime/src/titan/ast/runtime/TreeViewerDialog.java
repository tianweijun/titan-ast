package titan.ast.runtime;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * 显示树数据结构的窗口.
 *
 * @author tian wei jun
 */
public class TreeViewerDialog {

  private final JFrame dialog = new JFrame();

  public TreeViewerDialog(StringTree tree) {
    dialog.setPreferredSize(new Dimension(900, 800));
    dialog.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    populateDialog(tree);
  }

  public void setTitle(String title) {
    dialog.setTitle(title);
  }

  private void populateDialog(StringTree tree) {
    final Container mainPane = dialog.getContentPane();
    mainPane.setLayout(new GridLayout(1, 1));
    mainPane.add(new TreeViewerJComponent(tree).getContainer());
  }

  /**
   * 显示窗口.
   *
   * @return 窗口
   */
  public JFrame show() {
    dialog.pack();
    dialog.setVisible(true);
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
