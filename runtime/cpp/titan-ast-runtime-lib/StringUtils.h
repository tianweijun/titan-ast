//
// Created by tian wei jun on 2022/11/24 0024.
//

#ifndef AST__STRINGUTILS_H_
#define AST__STRINGUTILS_H_

#include <string>

class StringUtils {
 private:
  StringUtils() = default;

 public:
  StringUtils(const StringUtils &stringUtils) = delete;
  StringUtils(const StringUtils &&stringUtils) = delete;
  static bool isEmpty(const std::string *str);
  static bool isNotEmpty(const std::string *str);
  static bool isBlank(const std::string *str);
  static bool isNotBlank(const std::string *str);
};

#endif// AST__STRINGUTILS_H_
