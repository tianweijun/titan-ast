package titan.ast.output;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * 文件选择器.
 *
 * @author tian wei jun
 */
public class JFileChooserConfirmOverwrite extends JFileChooser {

  public JFileChooserConfirmOverwrite() {
    this.setMultiSelectionEnabled(false);
  }

  @Override
  public void approveSelection() {
    File selectedFile = this.getSelectedFile();
    if (selectedFile.exists()) {
      int answer = JOptionPane.showConfirmDialog(this, "Overwrite existing file?", "Overwrite?", 0);
      if (answer != 0) {
        return;
      }
    }

    super.approveSelection();
  }
}
