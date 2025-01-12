package titan.ast.runtime;

import java.nio.charset.StandardCharsets;

/**
 * .
 *
 * @author tian wei jun
 */
public class ByteBuffer {
  boolean isBigEndian = true;
  int position = 0;
  byte[] buffer;

  public ByteBuffer(int capacity) {
    this.buffer = new byte[capacity];
  }

  void setPosition(int position) {
    this.position = position;
  }

  int length() {
    return position;
  }

  void append(int b) {
    if (position >= buffer.length) {
      extendBuffer();
    }
    buffer[position++] = (byte) (b & 0xFF);
  }

  void append(byte b) {
    if (position >= buffer.length) {
      extendBuffer();
    }
    buffer[position++] = b;
  }

  void extendBuffer() {
    int newCapacity = buffer.length + buffer.length;
    byte[] newBuffer = new byte[newCapacity];
    System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
    buffer = newBuffer;
  }

  void clear() {
    position = 0;
  }

  int getInt() {
    return isBigEndian ? getIntB() : getIntL();
  }

  int getIntB() {
    int base = 0;
    int value = 0;
    for (int i = position - 1; i >= 0; i--) {
      byte tmp = buffer[i];
      value = value | tmp << base;
      base += 8;
    }
    return value;
  }

  int getIntL() {
    int base = 0;
    int value = 0;
    for (int i = 0; i < position; i++) {
      byte tmp = buffer[i];
      value = value | tmp << base;
      base += 8;
    }
    return value;
  }

  @Override
  public String toString() {
    return new String(buffer, 0, position, StandardCharsets.ISO_8859_1);
  }
}
