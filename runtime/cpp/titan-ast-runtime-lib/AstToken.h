//
// Created by tian wei jun on 2023/6/26.
//

#ifndef AST__RUNTIME_RUNTIME_ASTTOKEN_H_
#define AST__RUNTIME_RUNTIME_ASTTOKEN_H_
#include "Runtime.h"
#include <string>

class DLL_PUBLIC AstToken {
 public:
  AstToken();
  AstToken(int start, std::string text);
  AstToken(const AstToken &token);
  AstToken(AstToken &&token) noexcept;
  AstToken &operator=(const AstToken &other);
  ~AstToken();
  const AstToken *clone() const;

  int start;
  std::string text;
};

#endif// AST__RUNTIME_RUNTIME_ASTTOKEN_H_
