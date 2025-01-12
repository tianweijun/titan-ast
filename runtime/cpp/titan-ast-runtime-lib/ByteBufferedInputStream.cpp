//
// Created by tian wei jun on 2022/12/2 0002.
//

#include "ByteBufferedInputStream.h"

const int ByteBufferedInputStream::STANDARD_BUFFER_CAPACITY = 512;

ByteBufferedInputStream::ByteBufferedInputStream()
    : nextReadIndex(0), eof(-1), nextPos(0), limit(0), markFlag(-1),
      markNextReadIndex(0), limitOfInvalidData(0), isReadAllFromFile(false),
      sizeOfBuffer(0), buffer(nullptr), byteInputStream(std::ifstream()) {}

ByteBufferedInputStream::~ByteBufferedInputStream() {
  delete[] buffer;
  buffer = nullptr;
  if (byteInputStream.is_open()) {
    byteInputStream.close();
  }
}

ReadResult ByteBufferedInputStream::read() {
  if (nextPos < limit) {
    int readedByte = buffer[nextPos++] & (int) 0xFF;
    ++nextReadIndex;
    return {true, readedByte};
  }
  if (!isReadAllFromFile) {// 文件还没有读完就先填充，在尝试读取
    ReadResult fillBufferResult = fillBuffer();
    if (!fillBufferResult.isOk) {
      return fillBufferResult;
    }
    return read();
  } else {
    return {true, eof};
  }
}

ReadResult ByteBufferedInputStream::fillBuffer() {
  if (limit < sizeOfBuffer) {
    return fillRemainder();
  } else {
    if (limitOfInvalidData > 0) {
      compact();
      return fillRemainder();
    } else {
      return fillByExpansion();
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

ReadResult ByteBufferedInputStream::fillRemainder() {
  ReadResult doReadBufferResult =
      doReadBuffer(buffer, limit, sizeOfBuffer - limit);
  if (!doReadBufferResult.isOk) {
    return doReadBufferResult;
  }
  int countOfReaded = doReadBufferResult.data;
  limit += countOfReaded;
  return {true};
}

ReadResult ByteBufferedInputStream::fillByExpansion() {
  int nsz = sizeOfBuffer + sizeOfBuffer;
  byte *nBuffer = new byte[nsz];

  for (int i = 0; i < limit; i++) {
    nBuffer[i] = buffer[i];
  }
  // extend read
  ReadResult doReadBufferResult = doReadBuffer(nBuffer, limit, nsz - limit);
  if (!doReadBufferResult.isOk) {
    return doReadBufferResult;
  }
  int countOfNewRead = doReadBufferResult.data;

  delete[] this->buffer;
  this->buffer = nBuffer;
  this->sizeOfBuffer = nsz;
  this->limit = this->limit + countOfNewRead;
  return {true};
}

void ByteBufferedInputStream::reset() {
  if (markFlag < 0) {
    return;
  }
  nextPos = markFlag + 1;
  nextReadIndex = markNextReadIndex;
  if (nextPos >= limit) {// 数据全失效了
    nextPos = 0;
    limit = 0;
    limitOfInvalidData = 0;
  } else {// 还有可用数据
    limitOfInvalidData = nextPos;
  }
  markFlag = eof;
}

void ByteBufferedInputStream::mark() {
  this->markFlag = nextPos - 1;
  this->markNextReadIndex = nextReadIndex;
}

ReadResult ByteBufferedInputStream::doReadBuffer(byte *readBuffer, int offset,
                                                 int len) {
  char *base = reinterpret_cast<char *>(readBuffer + offset);
  int countOfRead = byteInputStream.read(base, len).gcount();
  if (byteInputStream.bad()) {
    return {false};
  }
  if (byteInputStream.eof()) {
    isReadAllFromFile = true;
  }
  if (countOfRead < 0) {
    countOfRead = 0;
  }
  return {true, countOfRead};
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

ByteBufferedInputStreamInitResult
ByteBufferedInputStream::init(const std::string *sourceFilePath) {
  clear();

  if (!buffer) {
    buffer = new byte[STANDARD_BUFFER_CAPACITY];
    sizeOfBuffer = STANDARD_BUFFER_CAPACITY;
  }
  byteInputStream.open(*sourceFilePath, std::ios::in | std::ios::binary);
  if (!byteInputStream.is_open()) {
    return {false, "open source File error,path:'" + *sourceFilePath + "'"};
  }
  return {true, ""};
}
