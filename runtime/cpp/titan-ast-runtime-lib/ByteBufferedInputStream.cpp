//
// Created by tian wei jun on 2022/12/2 0002.
//

#include "ByteBufferedInputStream.h"
#include "AstRuntimeException.h"

const int ByteBufferedInputStream::standardBufferCapacity = 256;

ByteBufferedInputStream::ByteBufferedInputStream()
    : nextReadIndex(0), eof(-1), nextPos(0), count(0), markPos(-1),
      sizeOfBuffer(0), buffer(nullptr), byteInputStream(std::ifstream()) {}

ByteBufferedInputStream::~ByteBufferedInputStream() {
  delete[] buffer;
  buffer = nullptr;
  if (byteInputStream.is_open()) {
    byteInputStream.close();
  }
}

int ByteBufferedInputStream::read() {
  int readedByte = eof;
  if (nextPos < count) {
    readedByte = buffer[nextPos++];
    ++nextReadIndex;
  } else {
    if (fill()) {
      readedByte = read();
    }
  }
  return readedByte;
}

/*
 * 只有当读完缓冲后才能调用，其他时候不能调用，只能在read方法中调用.
 */
bool ByteBufferedInputStream::fill() {
  if (markPos == eof) { // 重新填入
    return fillByNoMark();
  } else if (markPos == 0) { // 扩容
    return fillByExpansion();
  } else { // mark>0 && mark<=count 移动
    for (int indexOfBuffer = markPos; indexOfBuffer < count; indexOfBuffer++) {
      buffer[indexOfBuffer - markPos] = buffer[indexOfBuffer];
    }
    int oldCount = count - markPos;
    int newCount = oldCount;

    if (oldCount < sizeOfBuffer) {
      int countOfRead = doReadBuffer(buffer, oldCount, sizeOfBuffer - oldCount);
      newCount += countOfRead;
    }

    this->count = newCount;
    this->nextPos -= markPos;
    this->markPos = 0;
    return this->count > 0;
  }
}

bool ByteBufferedInputStream::fillByExpansion() {
  int nsz = sizeOfBuffer + standardBufferCapacity;
  byte *nBuffer = new byte[nsz];
  int oldCount = count;
  for (int i = 0; i < oldCount; i++) {
    nBuffer[i] = buffer[i];
  }
  int newCount = oldCount;
  // extend read
  int countOfRead = doReadBuffer(nBuffer, oldCount, nsz - oldCount);
  newCount += countOfRead;

  delete[] this->buffer;
  this->buffer = nBuffer;
  this->count = newCount;
  this->sizeOfBuffer = nsz;
  return newCount > oldCount;
}

bool ByteBufferedInputStream::fillByNoMark() {
  int read = doRead();
  if (eof == read) {
    return false;
  }
  markPos = eof;
  nextPos = 0;
  count = 0;

  buffer[0] = (byte)read;
  ++count;

  if (count < sizeOfBuffer) {
    int countOfRead = doReadBuffer(buffer, count, sizeOfBuffer - count);
    count += countOfRead;
  }

  return true;
}

void ByteBufferedInputStream::skip(int skipSteps) {
  for (int skipTimes = 0; skipTimes < skipSteps; skipTimes++) {
    read();
  }
}

void ByteBufferedInputStream::reset() {
  if (markPos == eof) {
    return;
  }
  nextReadIndex = nextReadIndex - (nextPos - markPos);
  nextPos = markPos;
  markPos = eof;
}

void ByteBufferedInputStream::mark() { this->markPos = nextPos; }

int ByteBufferedInputStream::doReadBuffer(byte *readBuffer, int offset,
                                          int len) {
  char *base = reinterpret_cast<char *>(readBuffer + offset);
  int countOfRead = byteInputStream.read(base, len).gcount();
  if (byteInputStream.bad() || countOfRead <= 0) {
    countOfRead = 0;
  }
  return countOfRead;
}

int ByteBufferedInputStream::doRead() {
  int read = byteInputStream.get();
  if (byteInputStream.fail()) {
    read = -1;
  }
  return read;
}

void ByteBufferedInputStream::clear() {
  nextReadIndex = 0;
  eof = -1;
  nextPos = 0;
  count = 0;
  markPos = -1;
  if (byteInputStream.is_open()) {
    byteInputStream.close();
  }
  byteInputStream.clear();

  if (buffer) {
    delete[] buffer;
    buffer = nullptr;
  }
}

void ByteBufferedInputStream::init(const std::string *sourceFilePath) {
  clear();

  if (!buffer) {
    buffer = new byte[standardBufferCapacity];
    sizeOfBuffer = standardBufferCapacity;
  }
  byteInputStream.open(*sourceFilePath, std::ios::in | std::ios::binary);
  if (!byteInputStream.is_open()) {
    AstRuntimeExceptionResolver::throwException(AstRuntimeException(
        AstRuntimeExceptionCode::IO_ERROR,
        "open source File error,path:'" + *sourceFilePath + "'"));
  }
}