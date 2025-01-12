//
// Created by tian wei jun on 2023/2/18.
//

#ifndef AST__TEST_RUNTIME_AUTOMATATMPTOKEN_H_
#define AST__TEST_RUNTIME_AUTOMATATMPTOKEN_H_

#include "AstToken.h"
#include "Grammar.h"
#include "Token.h"
#include "TokenType.h"

class AutomataTmpToken {
 public:
  AutomataTmpToken();
  AutomataTmpToken(Grammar *terminal, int start, std::string *text,
                   TokenType type);
  AutomataTmpToken(const AutomataTmpToken &token);
  AutomataTmpToken(AutomataTmpToken &&token) noexcept;
  AutomataTmpToken &operator=(const AutomataTmpToken &a);
  ~AutomataTmpToken() = default;
  const AutomataTmpToken *clone() const;

  AstToken toAstToken() const;
  void shallowCopy(Token *token);

  Grammar *terminal;
  int start;
  std::string *text;
  TokenType type;
};

#endif// AST__TEST_RUNTIME_AUTOMATATMPTOKEN_H_
