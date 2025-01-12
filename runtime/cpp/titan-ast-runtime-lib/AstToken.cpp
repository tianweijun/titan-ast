//
// Created by tian wei jun on 2023/6/26.
//

#include "AstToken.h"

AstToken::AstToken() : start(0), text(std::string()) {}

AstToken::AstToken(int start, std::string text) : start(start), text(text) {}

AstToken::AstToken(const AstToken &token) = default;

AstToken::AstToken(AstToken &&token) noexcept = default;

AstToken &AstToken::operator=(const AstToken &other) = default;

AstToken::~AstToken() = default;

const AstToken *AstToken::clone() const {
  auto *token = new AstToken(this->start, this->text);
  return token;
}
