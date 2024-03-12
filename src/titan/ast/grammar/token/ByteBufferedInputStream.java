package titan.ast.grammar.token;

import java.io.IOException;
import java.io.InputStream;
import titan.ast.runtime.AstRuntimeException;
/**
 * 类似BufferedInpustream功能的流.
 *
 * @author tian wei jun
 */
public class ByteBufferedInputStream {
  private final int standardBufferCapacity = 1024;
  private final int eof = -1;

  public int nextReadIndex = 0;
  private int nextPos = 0;
  private int count = 0;
  private int mark = eof;

  private boolean hasReadFromFile2FillBuffer = false;
  private boolean isReadAllFromFile = false;

  private byte[] buffer;
  private InputStream byteInputStream;

  /**
   * 带参构造.
   *
   * @param byteInputStream 所识别的文本输入流
   */
  public ByteBufferedInputStream(InputStream byteInputStream) {
    this.byteInputStream = byteInputStream;
    init();
  }

  /** 初始化. */
  public void init() {
    buffer = new byte[standardBufferCapacity];
  }

  /**
   * 读取一个字符.
   *
   * @return 所读取的字符，若没有文本了，返回-1
   */
  public int read() {
    if (nextPos < count) { // 从缓冲中正常读取
      int read = ((int) buffer[nextPos++]) & 0x000000FF;
      ++nextReadIndex;
      return read;
    }
    if (!isReadAllFromFile) { // 文件还没有读完就先填充，在尝试读取
      fillBuffer();
      return read();
    }
    if (!hasReadFromFile2FillBuffer) { // 第一次填充缓冲，在尝试读取
      firstFillBuffer();
      return read();
    }
    return eof;
  }

  private void fillBuffer() {
    if (count < buffer.length) {
      fillRemainder();
    } else {
      fillByExpansion();
    }
  }

  private void fillRemainder() {
    int countOfReaded = doRead(buffer, count, buffer.length - count);
    count += countOfReaded;
  }

  private void firstFillBuffer() {
    this.count = doRead(buffer, 0, buffer.length);
    hasReadFromFile2FillBuffer = true;
  }

  private void fillByExpansion() {
    int nsz = buffer.length + standardBufferCapacity;
    byte[] newBuffer = new byte[nsz];
    System.arraycopy(buffer, 0, newBuffer, 0, count);

    // extend read
    int countOfNewRead = doRead(newBuffer, count, nsz - count);

    this.buffer = newBuffer;
    this.count = this.count + countOfNewRead;
  }

  private void moveBuffer() {
    if (mark < 0) {
      return;
    }
    int countOfAvailableData = count - mark - 1;
    if (countOfAvailableData > 0) { // 移动
      System.arraycopy(buffer, mark + 1, buffer, 0, countOfAvailableData);
    }
    this.count = countOfAvailableData;
  }

  /** mark为空 */
  public void reset() {
    moveBuffer();
    nextPos = 0;
    mark = eof;
  }

  public void mark() {
    this.mark = nextPos - 1;
  }

  private int doRead(byte[] buffer, int offset, int len) {
    int countOfRead = 0;
    try {
      countOfRead = byteInputStream.read(buffer, offset, len);
    } catch (IOException e) {
      close();
      throw new AstRuntimeException(e);
    }
    if (countOfRead == eof) {
      isReadAllFromFile = true;
    }
    return Math.max(countOfRead, 0);
  }

  /** 关闭输入流. */
  public void close() {
    try {
      byteInputStream.close();
    } catch (IOException e) {
      throw new AstRuntimeException(e);
    }
  }
}
