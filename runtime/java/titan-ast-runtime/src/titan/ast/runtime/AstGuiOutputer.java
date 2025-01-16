package titan.ast.runtime;

import java.util.ArrayList;
import javax.swing.SwingUtilities;

/**
 * 图形化显示树数据结构.
 *
 * @author tian wei jun
 */
public class AstGuiOutputer {
  Ast tree;

  public AstGuiOutputer(Ast tree) {
    this.tree = tree;
  }

  public void output() {
    SwingUtilities.invokeLater(new GuiRunnable(tree));
  }

  private static class GuiRunnable implements Runnable {
    StringTree strTree;

    public GuiRunnable(Ast tree) {
      if (null != tree) {
        this.strTree = buildStringTree(tree);
      }
    }

    @Override
    public void run() {
      if (null != strTree) {
        TreeViewerDialog astDialog = new TreeViewerDialog(strTree);
        astDialog.setTitle("ast");
        astDialog.show();
      }
    }

    private StringTree buildStringTree(Ast astTree) {
      StringTree strTree =
          new StringTree(astTree.toString(), new ArrayList<StringTree>(astTree.children.size()));
      if (!astTree.children.isEmpty()) {
        for (Ast astTreeChild : astTree.children) {
          StringTree strTreeChild = buildStringTree(astTreeChild);
          strTree.children.add(strTreeChild);
        }
      }
      return strTree;
    }
  }
}
