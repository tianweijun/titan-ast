package titan.ast.runtime;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * .
 *
 * @author tian wei jun
 */
public class VisualTree extends JPanel {
  TreeViewer treeViewer;
  ControlTreeViewerPanel controlTreeViewerPanel;

  public VisualTree(StringTree tree) {
    treeViewer = new TreeViewer(tree);
    controlTreeViewerPanel = new ControlTreeViewerPanel();
    layoutComponent();
  }

  private void layoutComponent() {
    setLayout(new BorderLayout());
    add(new JScrollPane(treeViewer), BorderLayout.CENTER);
    add(controlTreeViewerPanel, BorderLayout.PAGE_END);
  }

  class ControlTreeViewerPanel extends JPanel implements ActionListener, ChangeListener {
    public static final String EXPORT_TREE_AS_PNG_COMMAND = "EXPORT_TREE_AS_PNG";
    JSlider scaleSlider;
    JButton exportAsPngBtn;

    public ControlTreeViewerPanel() {
      setScaleSlider();
      setExportAsPngBtn();
      setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
      add(scaleSlider);
      add(exportAsPngBtn);
    }

    private void setExportAsPngBtn() {
      exportAsPngBtn = new JButton("Export as PNG");
      exportAsPngBtn.setActionCommand(EXPORT_TREE_AS_PNG_COMMAND);
      exportAsPngBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
      exportAsPngBtn.addActionListener(this);
    }

    private void setScaleSlider() {
      scaleSlider = new JSlider(JSlider.HORIZONTAL, -1000, 1000, 0);
      scaleSlider.setAlignmentX(JComponent.CENTER_ALIGNMENT);
      scaleSlider.addChangeListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (EXPORT_TREE_AS_PNG_COMMAND.equals(e.getActionCommand())) {
        new JComponent2PicConverter().convert(treeViewer, "tree");
      }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      JSlider scaleSlider = (JSlider) e.getSource();
      int length = scaleSlider.getMaximum() - scaleSlider.getMinimum();
      int valueLength = scaleSlider.getValue() - scaleSlider.getMinimum();
      float scale = 1.0f + valueLength / (float) length;
      treeViewer.setScale(scale);
    }
  }
}
