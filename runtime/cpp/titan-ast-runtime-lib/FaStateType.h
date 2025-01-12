//
// Created by tian wei jun on 2022/12/1 0001.
//

#ifndef AST__FASTATETYPE_H_
#define AST__FASTATETYPE_H_

enum class FaStateEnumType : int {
  NONE = 0,
  NORMAL = 1,
  OPENING_TAG = 2,
  CLOSING_TAG = 4
};

class FaStateType {
 public:
  static int appendState(int state, FaStateEnumType appendState);
  static int removeState(int state, FaStateEnumType removeState);
  static bool isClosingTag(int state);
};

#endif// AST__FASTATETYPE_H_
