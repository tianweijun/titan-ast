//
// Created by tian wei jun on 2022/12/2 0002.
//

#include "ByteBufferedInputStream.h"
#include "AstRuntimeException.h"
#include "Logger.h"

const int ByteBufferedInputStream::standardBufferCapacity = 256;

ByteBufferedInputStream::ByteBufferedInputStream()
    : nextReadIndex(0), eof(-1), nextPos(0), count(0), markFlag(-1),
      hasReadFromFile2FillBuffer(false),isReadAllFromFile(false),
      sizeOfBuffer(0), buffer(nullptr), byteInputStream(std::ifstream()) {}

ByteBufferedInputStream::~ByteBufferedInputStream() {
  delete[] buffer;
  buffer = nullptr;
  if (byteInputStream.is_open()) {
    byteInputStream.close();
  }
}

int ByteBufferedInputStream::read() {
  if (nextPos < count) {
    int readedByte = ((int)buffer[nextPos++]) & 0x000000FF;
    ++nextReadIndex;
    return readedByte;
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

void ByteBufferedInputStream::fillBuffer() {
  if (count < sizeOfBuffer) {
    fillRemainder();
  } else {
    fillByExpansion();
  }
}

void ByteBufferedInputStream::fillRemainder() {
  int countOfReaded = doReadBuffer(buffer, count, sizeOfBuffer - count);
  count += countOfReaded;
}

bool ByteBufferedInputStream::fillByExpansion() {
  int nsz = sizeOfBuffer + standardBufferCapacity;
  byte *nBuffer = new byte[nsz];

  for (int i = 0; i < count; i++) {
    nBuffer[i] = buffer[i];
  }
  // extend read
  int countOfNewRead = doReadBuffer(nBuffer, count, nsz - count);

  delete[] this->buffer;
  this->buffer = nBuffer;
  this->sizeOfBuffer = nsz;
  this->count = this->count+ countOfNewRead;
  return countOfNewRead > 0;
}

void ByteBufferedInputStream::firstFillBuffer() {
  count = doReadBuffer(buffer, 0, sizeOfBuffer);
  hasReadFromFile2FillBuffer = true;
}

void ByteBufferedInputStream::moveBuffer() {
  if (markFlag < 0) {
    return;
  }
  int mark = markFlag +1;
  for (int indexOfBuffer = mark; indexOfBuffer < count; indexOfBuffer++) {//移动
    buffer[indexOfBuffer - mark] = buffer[indexOfBuffer];
  }
  count = count - mark;
}

void ByteBufferedInputStream::reset() {
  moveBuffer();
  nextPos = 0;
  markFlag = eof;
}

void ByteBufferedInputStream::mark() { this->markFlag = nextPos-1; }

int ByteBufferedInputStream::doReadBuffer(byte *readBuffer, int offset,
                                          int len) {
  char *base = reinterpret_cast<char *>(readBuffer + offset);
  int countOfRead = byteInputStream.read(base, len).gcount();
  if (byteInputStream.bad()) {
    isReadAllFromFile = true;
    error("%s","bad read from file");
  }
  if(byteInputStream.eof()){
    isReadAllFromFile = true;
  }
  if(countOfRead<0){
    countOfRead = 0;
  }
  return countOfRead;
}

void ByteBufferedInputStream::clear() {
  nextReadIndex = 0;
  nextPos = 0;
  count = 0;
  markFlag = eof;

  hasReadFromFile2FillBuffer = false;
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

