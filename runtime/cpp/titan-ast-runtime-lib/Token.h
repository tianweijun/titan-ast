//
// Created by tian wei jun on 2022/11/17 0017.
//

#ifndef AST__RUNTIME__TOKEN_H_
#define AST__RUNTIME__TOKEN_H_
#include <string>

#include "Grammar.h"
#include "TokenType.h"
#include "AstToken.h"

class Token {
public:
  Token();
  Token(Grammar terminal, int start, std::string text, TokenType type);
  Token(const Token &token);
  Token(Token &&token) noexcept;
  Token &operator=(const Token &other);
  ~Token() = default;
  const Token *clone() const;
  AstToken toAstToken() const;

  Grammar terminal;
  int start;
  std::string text;
  TokenType type;
};

#endif // AST__RUNTIME__TOKEN_H_
