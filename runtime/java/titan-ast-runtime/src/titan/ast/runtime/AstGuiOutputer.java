package titan.ast.runtime;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.swing.JFrame;

/**
 * 图形化显示树数据结构.
 *
 * @author tian wei jun
 */
public class AstGuiOutputer {
  Ast tree;
  String charsetName;

  public AstGuiOutputer(Ast tree) {
    this.tree = tree;
  }

  public AstGuiOutputer(Ast tree, String charsetName) {
    this.tree = tree;
    this.charsetName = charsetName;
  }

  public void output() {
    if (null == tree) {
      return;
    }
    StringTree strTree = buildStringTree(tree);
    if (StringUtils.isNotBlank(charsetName)) {
      try {
        serCharsetForStringTree(strTree);
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }
    output(strTree);
  }

  private void serCharsetForStringTree(StringTree strTree) throws UnsupportedEncodingException {
    strTree.text = new String(strTree.text.getBytes(StandardCharsets.ISO_8859_1), charsetName);
    for (StringTree child : strTree.children) {
      serCharsetForStringTree(child);
    }
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
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
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
