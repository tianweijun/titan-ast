//
// Created by tian wei jun on 2022/12/2 0002.
//

#include "ByteBufferedInputStream.h"
#include "AstRuntimeException.h"
#include "Logger.h"

const int ByteBufferedInputStream::STANDARD_BUFFER_CAPACITY = 512;

ByteBufferedInputStream::ByteBufferedInputStream()
    : nextReadIndex(0), eof(-1), nextPos(0), limit(0), markFlag(-1),
      limitOfInvalidData(0),
      isReadAllFromFile(false), sizeOfBuffer(0), buffer(nullptr),
      byteInputStream(std::ifstream()) {}

ByteBufferedInputStream::~ByteBufferedInputStream() {
  delete[] buffer;
  buffer = nullptr;
  if (byteInputStream.is_open()) {
    byteInputStream.close();
  }
}

int ByteBufferedInputStream::read() {
  if (nextPos < limit) {
    int readedByte = ((int)buffer[nextPos++]) & 0x000000FF;
    ++nextReadIndex;
    return readedByte;
  }
  if (!isReadAllFromFile) { // 文件还没有读完就先填充，在尝试读取
    fillBuffer();
    return read();
  }
  return eof;
}

void ByteBufferedInputStream::fillBuffer() {
  if (limit < sizeOfBuffer) {
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

void ByteBufferedInputStream::compact() {
  int moveCount = limit - limitOfInvalidData;
  for (int i = 0; i < moveCount; i++) {
    buffer[i] = buffer[i + limitOfInvalidData];
  }
  nextPos = nextPos - limitOfInvalidData;
  markFlag = markFlag - limitOfInvalidData;
  limit = moveCount;
  limitOfInvalidData = 0;
}

void ByteBufferedInputStream::fillRemainder() {
  int countOfReaded = doReadBuffer(buffer, limit, sizeOfBuffer - limit);
  limit += countOfReaded;
}

void ByteBufferedInputStream::fillByExpansion() {
  int nsz = sizeOfBuffer + sizeOfBuffer;
  byte *nBuffer = new byte[nsz];

  for (int i = 0; i < limit; i++) {
    nBuffer[i] = buffer[i];
  }
  // extend read
  int countOfNewRead = doReadBuffer(nBuffer, limit, nsz - limit);

  delete[] this->buffer;
  this->buffer = nBuffer;
  this->sizeOfBuffer = nsz;
  this->limit = this->limit + countOfNewRead;
}

void ByteBufferedInputStream::reset() {
  if (markFlag < 0) {
    return;
  }
  nextPos = markFlag + 1;
  if (nextPos >= limit) { // 数据全失效了
    nextPos = 0;
    limit = 0;
    limitOfInvalidData = 0;
  } else { // 还有可用数据
    limitOfInvalidData = nextPos;
  }
  markFlag = eof;
}

void ByteBufferedInputStream::mark() { this->markFlag = nextPos - 1; }

int ByteBufferedInputStream::doReadBuffer(byte *readBuffer, int offset,
                                          int len) {
  char *base = reinterpret_cast<char *>(readBuffer + offset);
  int countOfRead = byteInputStream.read(base, len).gcount();
  if (byteInputStream.bad()) {
    isReadAllFromFile = true;
    error("%s", "bad read from file");
  }
  if (byteInputStream.eof()) {
    isReadAllFromFile = true;
  }
  if (countOfRead < 0) {
    countOfRead = 0;
  }
  return countOfRead;
}

void ByteBufferedInputStream::clear() {
  nextReadIndex = 0;
  nextPos = 0;
  limit = 0;
  limitOfInvalidData = 0;
  markFlag = eof;
  
  isReadAllFromFile = false;

  if (byteInputStream.is_open()) {
    byteInputStream.close();
  }
  byteInputStream.clear();

  if (buffer) {
    delete[] buffer;
    buffer = nullptr;
    sizeOfBuffer = 0;
  }
}

void ByteBufferedInputStream::init(const std::string *sourceFilePath) {
  clear();

  if (!buffer) {
    buffer = new byte[STANDARD_BUFFER_CAPACITY];
    sizeOfBuffer = STANDARD_BUFFER_CAPACITY;
  }
  byteInputStream.open(*sourceFilePath, std::ios::in | std::ios::binary);
  if (!byteInputStream.is_open()) {
    AstRuntimeExceptionResolver::throwException(AstRuntimeException(
        AstRuntimeExceptionCode::IO_ERROR,
        "open source File error,path:'" + *sourceFilePath + "'"));
  }
}
