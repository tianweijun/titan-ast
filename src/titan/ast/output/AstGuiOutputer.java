package titan.ast.output;

import java.util.ArrayList;
import java.util.concurrent.Future;
import javax.swing.JFrame;
import titan.ast.output.tree.TreeViewerDialog;
import titan.ast.target.Ast;

/**
 * 图形化显示树数据结构.
 *
 * @author tian wei jun
 */
public class AstGuiOutputer {

  public void output(Ast tree) {
    if (null == tree) {
      return;
    }
    StringTree strTree = buildStringTree(tree);
    output(strTree);
  }

  public void output(StringTree strTree) {
    if (null == strTree) {
      return;
    }
    TreeViewerDialog astDialog = new TreeViewerDialog(strTree);
    astDialog.setTitle("ast");
    Future<JFrame> dialogrameFuture = astDialog.open();
    try {
      dialogrameFuture.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private StringTree buildStringTree(Ast astTree) {
    StringTree strTree = new StringTree(null, new ArrayList<StringTree>(), astTree.toString());
    if (!astTree.children.isEmpty()) {
      for (Ast astTreeChild : astTree.children) {
        StringTree strTreeChild = buildStringTree(astTreeChild);
        strTreeChild.parent = strTree;
        strTree.children.add(strTreeChild);
      }
    }
    return strTree;
  }
}
