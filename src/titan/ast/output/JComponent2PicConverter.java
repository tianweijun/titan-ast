package titan.ast.output;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import titan.ast.AstRuntimeException;

/**
 * 图形化组件转为图片.
 *
 * @author tian wei jun
 */
public class JComponent2PicConverter {

  private JComponent viewer;
  private String fileName;

  /**
   * 图形化组件转为图片.
   *
   * @param viewer 图形化组件
   * @param fileName 保存的文件名
   */
  public void convert(JComponent viewer, String fileName) {
    this.viewer = viewer;
    this.fileName = fileName;
    generatePngFile();
  }

  private void generatePngFile() {
    BufferedImage bufferedImage =
        new BufferedImage(
            viewer.getSize().width, viewer.getSize().height, BufferedImage.TYPE_INT_ARGB);
    Graphics bufferedImageGraphics = bufferedImage.createGraphics();
    bufferedImageGraphics.setColor(Color.WHITE);
    bufferedImageGraphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    viewer.paint(bufferedImageGraphics);
    bufferedImageGraphics.dispose();

    try {
      JFileChooser fileChooser = getFileChooser(".png", "PNG files");

      int returnValue = fileChooser.showSaveDialog(null);
      if (returnValue == JFileChooser.APPROVE_OPTION) {
        File pngFile = fileChooser.getSelectedFile();
        ImageIO.write(bufferedImage, "png", pngFile);

        try {
          // Try to open the parent folder using the OS' native file manager.
          Desktop.getDesktop().open(pngFile.getParentFile());
        } catch (Exception ex) {
          // We could not launch the file manager: just show a popup that we
          // succeeded in saving the PNG file.
          JOptionPane.showMessageDialog(null, "Saved PNG to: " + pngFile.getAbsolutePath());
          throw new AstRuntimeException(ex);
        }
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          null, "Could not export to PNG: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      throw new AstRuntimeException(ex);
    }
  }

  private JFileChooser getFileChooser(final String fileEnding, final String description) {
    File suggestedFile = generateNonExistingFile(fileEnding);
    JFileChooser fileChooser = new JFileChooserConfirmOverwrite();
    fileChooser.setCurrentDirectory(suggestedFile.getParentFile());
    fileChooser.setSelectedFile(suggestedFile);
    FileFilter filter =
        new FileFilter() {

          @Override
          public boolean accept(File pathname) {
            if (pathname.isFile()) {
              return pathname.getName().toLowerCase().endsWith(fileEnding);
            }

            return true;
          }

          @Override
          public String getDescription() {
            return description + " (*" + fileEnding + ")";
          }
        };
    fileChooser.addChoosableFileFilter(filter);
    fileChooser.setFileFilter(filter);
    return fileChooser;
  }

  private File generateNonExistingFile(String extension) {

    final String parent = ".";

    File file = new File(parent, fileName + extension);

    int counter = 1;
    while (file.exists()) {
      file = new File(parent, fileName + "_" + counter + extension);
      counter++;
    }

    return file;
  }
}
