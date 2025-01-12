package titan.ast.runtime;

import java.io.IOException;
import java.io.InputStream;

/**
 * 类似BufferedInpustream功能的流.
 *
 * @author tian wei jun
 */
public class ByteBufferedInputStream {
  private static final int STANDARD_BUFFER_CAPACITY = 512;
  private static final int EOF = -1;

  public int nextReadIndex = 0;
  public int markNextReadIndex = 0;

  private int nextPos = 0;
  private int limit = 0;
  private int limitOfInvalidData = 0;
  private int mark = -1;

  private boolean isReadAllFromFile = false;

  private byte[] buffer;
  private final InputStream byteInputStream;

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
    buffer = new byte[STANDARD_BUFFER_CAPACITY];
  }

  /**
   * 读取一个字符.
   *
   * @return 所读取的字符，若没有文本了，返回-1
   */
  public int read() throws IOException {
    if (nextPos < limit) { // 从缓冲中正常读取
      int read = buffer[nextPos++] & (int) 0xFF;
      ++nextReadIndex;
      return read;
    }
    // nextPos >= limit
    if (!isReadAllFromFile) { // 文件还没有读完就先填充，在尝试读取
      fillBuffer();
      return read();
    } else {
      return EOF;
    }
  }

  private void fillBuffer() throws IOException { // nextPos >= limit
    if (limit < buffer.length) {
      fillRemainder();
    } else {
      if (limitOfInvalidData > 0) {
        compact();
        fillRemainder();
      } else {
        fillByExpansion();
      }
    }
  }

  private void compact() {
    int moveCount = limit - limitOfInvalidData;
    System.arraycopy(buffer, limitOfInvalidData, buffer, 0, moveCount);
    nextPos = nextPos - limitOfInvalidData;
    mark = mark - limitOfInvalidData;
    limit = moveCount;
    limitOfInvalidData = 0;
  }

  private void fillRemainder() throws IOException {
    int countOfReaded = doRead(buffer, limit, buffer.length - limit);
    limit += countOfReaded;
  }

  private void fillByExpansion() throws IOException {
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
    nextReadIndex = markNextReadIndex;
    if (nextPos >= limit) { // 数据全失效了
      nextPos = 0;
      limit = 0;
      limitOfInvalidData = 0;
    } else { // 还有可用数据
      limitOfInvalidData = nextPos;
    }
    mark = EOF;
  }

  public void mark() {
    this.mark = nextPos - 1;
    this.markNextReadIndex = nextReadIndex;
  }

  private int doRead(byte[] buffer, int offset, int len) throws IOException {
    int countOfRead = byteInputStream.read(buffer, offset, len);

    if (countOfRead == EOF) {
      isReadAllFromFile = true;
    }
    return Math.max(countOfRead, 0);
  }
}
