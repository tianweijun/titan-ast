//
// Created by tian wei jun on 2022/11/27 0027.
//

#ifndef AST__BYTEBUFFER_H_
#define AST__BYTEBUFFER_H_

#include <cstdint>

using byte = unsigned char;

class ByteBuffer {
 public:
  explicit ByteBuffer(int capacity);
  ByteBuffer(int capacity, bool isBigEndian);
  ByteBuffer(const ByteBuffer &byteBuffer);
  ByteBuffer(ByteBuffer &&byteBuffer) noexcept;
  ~ByteBuffer();

  void setPosition(int position);
  int length() const;
  void append(byte b);
  void clear();
  int getInt() const;
  int getIntB() const;
  int getIntL() const;

  bool isBigEndian;
  int capacity;
  int position;
  byte *buffer;

 private:
  void extendBuffer();
};

#endif// AST__BYTEBUFFER_H_
