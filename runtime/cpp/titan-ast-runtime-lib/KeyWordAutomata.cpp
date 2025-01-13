//
// Created by tian wei jun on 2024/1/9.
//

#include "KeyWordAutomata.h"

KeyWordAutomata::KeyWordAutomata()
    : emptyOrNot(KeyWordAutomata::EMPTY), rootKeyWord(nullptr),
      textTerminalMap(
          std::unordered_map<std::string *, Grammar *, TextTerminalMapHash,
                             TextTerminalMapEq>()) {}

KeyWordAutomata::KeyWordAutomata(int isEmpty, const Grammar *rootKeyWord)
    : emptyOrNot(isEmpty), rootKeyWord(rootKeyWord),
      textTerminalMap(
          std::unordered_map<std::string *, Grammar *, TextTerminalMapHash,
                             TextTerminalMapEq>()) {}

KeyWordAutomata::~KeyWordAutomata() = default;

std::vector<Token *> *
KeyWordAutomata::buildToken(std::vector<Token *> *tokens) const {
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