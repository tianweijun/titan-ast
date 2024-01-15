//
// Created by tian wei jun on 2023/2/18.
//

#include "AutomataTmpToken.h"

AutomataTmpToken::AutomataTmpToken()
    : terminal(nullptr), start(0), text(nullptr), type(TokenType::TEXT) {}

AutomataTmpToken::AutomataTmpToken(Grammar *terminal, int start,
                                   std::string *text, TokenType type)
    : terminal(terminal), start(start), text(text), type(type) {}

AutomataTmpToken::AutomataTmpToken(const AutomataTmpToken &token) = default;

AutomataTmpToken::AutomataTmpToken(AutomataTmpToken &&token) noexcept = default;

AutomataTmpToken &
AutomataTmpToken::operator=(const AutomataTmpToken &other) = default;

const AutomataTmpToken *AutomataTmpToken::clone() const {
  auto *token =
      new AutomataTmpToken(this->terminal, this->start, this->text, this->type);
  return token;
}

AstToken AutomataTmpToken::toAstToken() const {
  AstToken token(this->start, *this->text);
  return token;
}

void AutomataTmpToken::shallowCopy(Token *token) {
  this->terminal = &token->terminal;
  this->start = token->start;
  this->text = &token->text;
  this->type = token->type;
}
bool AutomataTmpToken::equals(const AutomataTmpToken *o) const {
  return this->terminal == o->terminal && this->start == o->start &&
         this->text == o->text && this->type == o->type;
}

bool AutomataTmpToken::compare(const AutomataTmpToken *o) const {
  if (!this->terminal->equals(*o->terminal)) {
    return this->terminal->compare(*o->terminal);
  }
  if (this->start != o->start) {
    return this->start < o->start;
  }
  if ((*this->text) != (*o->text)) {
    return (*this->text) < (*o->text);
  }
  if (this->type != o->type) {
    return this->type < o->type;
  }
  return false;
}
