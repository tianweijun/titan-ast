//
// Created by tian wei jun on 2022/12/7 0007.
//

#include "FaStateType.h"

int FaStateType::appendState(int state, FaStateEnumType appendState) {
  return state | (int) appendState;
}

int FaStateType::removeState(int state, FaStateEnumType removeState) {
  return state & (~(int) removeState);
}

bool FaStateType::isClosingTag(int state) {
  return (state & (int) FaStateEnumType::CLOSING_TAG) != 0;
}
