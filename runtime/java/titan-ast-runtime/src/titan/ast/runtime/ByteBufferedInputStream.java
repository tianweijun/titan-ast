package titan.ast.runtime;

import java.io.IOException;
import java.io.InputStream;

/**
 * 类似BufferedInpustream功能的流.
 *
 * @author tian wei jun
 */
public class ByteBufferedInputStream {
  private final int standardBufferCapacity = 512;
  private final int eof = -1;

  public int nextReadIndex = 0;

  private int nextPos = 0;
  private int limit = 0;
  private int start = -1;
  private int mark = -1;

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
    if (nextPos < limit) { // 从缓冲中正常读取
      int read = ((int) buffer[nextPos++]) & 0x000000FF;
      ++nextReadIndex;
      return read;
    }
    // nextPos >= limit
    if (!isReadAllFromFile) { // 文件还没有读完就先填充，在尝试读取
      fillBuffer();
      return read();
    }
    return eof;
  }

  private void fillBuffer() { // nextPos >= limit
    if (limit < buffer.length) {
      fillRemainder();
    } else {
      if (start > 0) {
        compact();
        fillRemainder();
      } else {
        fillByExpansion();
      }
    }
  }

  private void compact() {
    int moveCount = limit - start;
    System.arraycopy(buffer, start, buffer, 0, moveCount);
    nextPos = nextPos - start;
    mark = mark - start;
    limit = moveCount;
    start = 0;
  }

  private void fillRemainder() {
    int countOfReaded = doRead(buffer, limit, buffer.length - limit);
    limit += countOfReaded;
  }

  private void fillByExpansion() {
    int nsz = buffer.length + buffer.length;
    byte[] newBuffer = new byte[nsz];
    System.arraycopy(buffer, 0, newBuffer, 0, limit);

    // extend read
    int countOfNewRead = doRead(newBuffer, limit, nsz - limit);

    this.buffer = newBuffer;
    this.limit = this.limit + countOfNewRead;
  }

  /** mark为空 */
  public void reset() {
    if (mark < 0) {
      return;
    }
    nextPos = mark + 1;
    if (nextPos >= limit) { // 数据全失效了
      nextPos = 0;
      limit = 0;
      start = eof;
    } else { // 还有可用数据
      start = nextPos;
    }
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
