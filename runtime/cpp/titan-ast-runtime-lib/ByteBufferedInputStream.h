//
// Created by tian wei jun on 2022/12/2 0002.
//

#ifndef AST__BYTEBUFFEREDINPUTSTREAM_H_
#define AST__BYTEBUFFEREDINPUTSTREAM_H_

#include <fstream>
using byte = uint8_t;

struct ByteBufferedInputStreamInitResult {
  bool isOk{false};
  std::string msg{""};
};

struct ReadResult {
  bool isOk{false};
  int data{0};
};

class ByteBufferedInputStream {
 public:
  ByteBufferedInputStream();
  ByteBufferedInputStream(
      const ByteBufferedInputStream &byteBufferedInputStream) = delete;
  ByteBufferedInputStream(
      const ByteBufferedInputStream &&byteBufferedInputStream) = delete;
  ~ByteBufferedInputStream();

 public:
  ByteBufferedInputStreamInitResult init(const std::string *sourceFilePath);
  ReadResult read();
  ReadResult fillByExpansion();
  void reset();
  void mark();
  ReadResult doReadBuffer(byte *readBuffer, int offset, int len);
  ReadResult fillBuffer();
  ReadResult fillRemainder();
  void compact();

  void clear();

 public:
  int nextReadIndex;

 private:
  const static int STANDARD_BUFFER_CAPACITY;

 private:
  const int eof;

  int nextPos;
  int limit;
  int limitOfInvalidData;
  int markFlag;
  int markNextReadIndex;
  bool isReadAllFromFile;

  byte *buffer;
  int sizeOfBuffer;
  std::ifstream byteInputStream;
};

#endif// AST__BYTEBUFFEREDINPUTSTREAM_H_
