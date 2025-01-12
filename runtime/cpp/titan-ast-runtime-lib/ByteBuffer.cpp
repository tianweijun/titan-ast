//
// Created by tian wei jun on 2022/11/27 0027.
//

#include "ByteBuffer.h"

ByteBuffer::ByteBuffer(int capacity)
    : isBigEndian(true), capacity(capacity), position(0) {
  buffer = new byte[capacity];
}

ByteBuffer::ByteBuffer(int capacity, bool isBigEndian)
    : isBigEndian(isBigEndian), capacity(capacity), position(0) {
  buffer = new byte[capacity];
}

ByteBuffer::ByteBuffer(const ByteBuffer &byteBuffer) {
  this->isBigEndian = byteBuffer.isBigEndian;
  this->capacity = byteBuffer.capacity;
  this->position = byteBuffer.position;
  this->buffer = new byte[this->capacity];
  for (int i = 0; i < this->capacity; i++) {
    this->buffer[i] = byteBuffer.buffer[i];
  }
}

ByteBuffer::ByteBuffer(ByteBuffer &&byteBuffer) {
  this->isBigEndian = byteBuffer.isBigEndian;
  this->capacity = byteBuffer.capacity;
  this->position = byteBuffer.position;
  this->buffer = byteBuffer.buffer;

  byteBuffer.buffer = nullptr;
  byteBuffer.capacity = 0;
  byteBuffer.position = 0;
}

ByteBuffer::~ByteBuffer() {
  delete[] buffer;
  buffer = nullptr;
}

void ByteBuffer::setPosition(int position) { this->position = position; }

int ByteBuffer::length() const { return position; }

void ByteBuffer::append(byte b) {
  if (position >= capacity) {
    extendBuffer();
  }
  buffer[position++] = b;
}

void ByteBuffer::extendBuffer() {
  int newCapacity = capacity + capacity;
  byte *newBuffer = new byte[newCapacity];
  for (int i = 0; i < capacity; i++) {
    newBuffer[i] = buffer[i];
  }
  delete[] buffer;
  buffer = newBuffer;
  capacity = newCapacity;
}

void ByteBuffer::clear() { position = 0; }

int ByteBuffer::getInt() const { return isBigEndian ? getIntB() : getIntL(); }

int ByteBuffer::getIntB() const {
  int base = 0;
  int value = 0;
  for (int i = position - 1; i >= 0; i--) {
    byte tmp = buffer[i];
    value = value | tmp << base;
    base += 8;
  }
  return value;
}

int ByteBuffer::getIntL() const {
  int base = 0;
  int value = 0;
  for (int i = 0; i < position; i++) {
    byte tmp = buffer[i];
    value = value | tmp << base;
    base += 8;
  }
  return value;
}
