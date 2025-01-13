//
// Created by tian wei jun on 2024/1/9.
//

#ifndef AST_RUNTIME_RUNTIME_KEYWORDAUTOMATA_H_
#define AST_RUNTIME_RUNTIME_KEYWORDAUTOMATA_H_

#include <vector>
#include <string>
#include <unordered_map>

#include "Grammar.h"
#include "Token.h"

class TextTerminalMapCompare {
 public:
  bool operator()(const std::string *t1, const std::string *t2) const {
    return *t1 < *t2;
  }
};

class TextTerminalMapHash {
 public:
  size_t operator()(const std::string *t1) const {
    return std::hash<std::string>()(*t1);
  }
};

class TextTerminalMapEq {
 public:
  bool operator()(const std::string *t1, const std::string *t2) const {
    return *t1 == *t2;
  }
};

class KeyWordAutomata {

 public:
  KeyWordAutomata();
  ~KeyWordAutomata();
  KeyWordAutomata(int isEmpty, const Grammar *rootKeyWord);
  std::vector<Token *> *buildToken(std::vector<Token *> *tokens) const;

 public:
  int emptyOrNot;
  const Grammar *rootKeyWord;
  std::unordered_map<std::string *, Grammar *, TextTerminalMapHash,
                     TextTerminalMapEq>
      textTerminalMap;

  static const int EMPTY = 0;
  static const int NOT_EMPTY = 1;
};

#endif// AST_RUNTIME_RUNTIME_KEYWORDAUTOMATA_H_
