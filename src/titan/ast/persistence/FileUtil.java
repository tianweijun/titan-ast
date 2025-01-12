package titan.ast.persistence;

import java.io.File;
import java.io.IOException;
import titan.ast.AstRuntimeException;

/**
 * 文件操作工具类.
 *
 * @author tian wei jun
 */
public class FileUtil {
  private FileUtil() {}

  public static File makeFile(String filePath) {
    File file = new File(filePath);
    if (file.exists()) {
      return file;
    }
    File fileParent = file.getParentFile();
    if (!fileParent.exists()) {
      boolean mkdirs = fileParent.mkdirs();
      if (!mkdirs) {
        throw new AstRuntimeException("mkdirs failed at makeFile method in FileUtil");
      }
    }

    try {
      boolean hasCreated = file.createNewFile();
      if (!hasCreated) {
        throw new AstRuntimeException("mkdirs failed at makeFile method in FileUtil");
      }
    } catch (IOException e) {
      throw new AstRuntimeException(e);
    }
    return file;
  }
}
