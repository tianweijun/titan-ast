package titan.ast.persistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import titan.ast.runtime.AstRuntimeException;

/**
 * 文件操作工具类.
 *
 * @author tian wei jun
 */
public class FileUtil {
  private FileUtil() {}

  public static void copyInputStreamToFile(InputStream input, File destFile) {
    try (OutputStream output = new FileOutputStream(destFile)) {
      destFile.createNewFile();
      copyInputStreamToOutputStream(input, output);
    } catch (IOException e) {
      throw new AstRuntimeException(e);
    }
  }

  public static void copyInputStreamToOutputStream(InputStream input, OutputStream output) {
    try {
      byte[] buf = new byte[1024];
      int bytesRead;
      while ((bytesRead = input.read(buf)) > 0) {
        output.write(buf, 0, bytesRead);
      }
    } catch (Exception e) {
      throw new AstRuntimeException(e);
    }
  }

  public static File makeFileDirectory(String fileDirectory) {
    File file = new File(fileDirectory);
    if (!file.exists()) {
      boolean hasCreadted = file.mkdirs();
      if (!hasCreadted) {
        throw new AstRuntimeException("file.mkdirs failed.");
      }
    }
    return file;
  }

  public static File makeFile(String filePath) {
    File file = new File(filePath);
    File fileParent = file.getParentFile();
    if (!fileParent.exists()) {
      fileParent.mkdirs();
    }
    try {
      boolean hasCreated = file.createNewFile();
    } catch (IOException e) {
      throw new AstRuntimeException(e);
    }
    return file;
  }
}
