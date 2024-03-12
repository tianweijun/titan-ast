//
// Created by tian wei jun on 2022/12/2 0002.
//

#ifndef AST__BYTEBUFFEREDINPUTSTREAM_H_
#define AST__BYTEBUFFEREDINPUTSTREAM_H_

#include <fstream>
using byte = uint8_t;

class ByteBufferedInputStream {
public:
  ByteBufferedInputStream();
  ByteBufferedInputStream(
      const ByteBufferedInputStream &byteBufferedInputStream) = delete;
  ByteBufferedInputStream(
      const ByteBufferedInputStream &&byteBufferedInputStream) = delete;
  ~ByteBufferedInputStream();

public:
  void init(const std::string *sourceFilePath);
  int read();
  void fillByExpansion();
  void reset();
  void mark();
  int doReadBuffer(byte *readBuffer, int offset, int len);
  void fillBuffer();
  void fillRemainder();
  void compact();

  void clear();

public:
  int nextReadIndex;

private:
  const static int standardBufferCapacity;

private:
  const int eof;

  int nextPos;
  int limit;
  int start;
  int markFlag;
  bool isReadAllFromFile;

  byte *buffer;
  int sizeOfBuffer;
  std::ifstream byteInputStream;
};

#endif // AST__BYTEBUFFEREDINPUTSTREAM_H_
