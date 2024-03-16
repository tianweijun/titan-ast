//
// Created by tian wei jun on 2024/1/9.
//

#include "KeyWordAutomata.h"
bool TextTerminalMapCompare::operator()(const std::string *t1,
                                        const std::string *t2) const {
  return *t1 < *t2;
}

KeyWordAutomata::KeyWordAutomata()
    : emptyOrNot(KeyWordAutomata::EMPTY), rootKeyWord(nullptr),
      textTerminalMap(
          std::map<std::string *, Grammar *, TextTerminalMapCompare>()) {}

KeyWordAutomata::KeyWordAutomata(int isEmpty, const Grammar *rootKeyWord)
    : emptyOrNot(isEmpty), rootKeyWord(rootKeyWord),
      textTerminalMap(
          std::map<std::string *, Grammar *, TextTerminalMapCompare>()) {}

KeyWordAutomata::~KeyWordAutomata() = default;

std::list<Token *> *
KeyWordAutomata::buildToken(std::list<Token *> *tokens) const {
  for (auto token : *tokens) {
    if (rootKeyWord->index == token->terminal.index) {
      auto findIt = textTerminalMap.find(&token->text);
      if (findIt != textTerminalMap.end()) {
        auto terminal = findIt->second;
        token->terminal = *terminal;
      }
    }
  }
  return tokens;
}