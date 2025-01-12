//
// for ascll
// Created by tian wei jun on 2022/11/24 0024.
//
//

#include "StringUtils.h"

bool StringUtils::isEmpty(const std::string *str) {
  return !str || str->length() <= 0;
}

bool StringUtils::isNotEmpty(const std::string *str) { return !isEmpty(str); }

bool StringUtils::isBlank(const std::string *str) {
  if (!str || str->length() <= 0) {
    return true;
  }
  bool isBlank = true;
  const char *chars = str->data();
  const char *charIt = chars;
  for (int i = 0; i < str->length(); i++) {
    char tmpChar = *charIt;
    switch (tmpChar) {// {32, 9, 13}
      case 32:
      case 9:
      case 13:
        break;
      default:
        isBlank = false;
    }
    if (!isBlank) {
      break;
    }
    ++charIt;
  }
  return isBlank;
}

bool StringUtils::isNotBlank(const std::string *str) { return !isBlank(str); }