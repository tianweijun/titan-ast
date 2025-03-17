package titan.ast.runtime;

import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 显示树数据结构的组件，本组件在窗口中.
 *
 * @author tian wei jun
 */
public class TreeViewer extends JComponent implements ChangeListener {
  static final String UI_CLASS_ID = "TreeViewerUI";
  private final TreeViewerModel treeViewerModel = new TreeViewerModel();

  static {
    UIManager.put(TreeViewer.UI_CLASS_ID, TreeViewerUI.class.getName());
  }

  TreeViewer(StringTree tree) {
    updateUI();
    treeViewerModel.addChangeListener(this);
    setBackground(Color.white);
    setStringTree(tree);
  }

  TreeViewerModel getTreeViewerModel() {
    return treeViewerModel;
  }

  void setStringTree(StringTree stringTree) {
    treeViewerModel.setStringTree(stringTree);
  }

  void setScale(float scale) {
    treeViewerModel.setScale(scale);
  }

  public void updateUI() {
    setUI(UIManager.getUI(this));
  }

  @Override
  public String getUIClassID() {
    return UI_CLASS_ID;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    repaint();
  }
}
