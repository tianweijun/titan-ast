//
// Created by tian wei jun on 2022/11/17 0017.
//

#include "Token.h"

Token::Token()
    : terminal(Grammar(0)), start(0), text(std::string()),
      type(TokenType::TEXT) {}

Token::Token(Grammar terminal, int start, std::string text, TokenType type)
    : terminal(terminal), start(start), text(text), type(type) {}

Token::Token(const Token &token) = default;

Token::Token(Token &&token) noexcept = default;

Token &Token::operator=(const Token &other) = default;

const Token *Token::clone() const {
  auto *token = new Token(this->terminal, this->start, this->text, this->type);
  return token;
}
