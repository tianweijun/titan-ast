//
// Created by tian wei jun on 2024/1/9.
//

#ifndef AST_RUNTIME_RUNTIME_KEYWORDAUTOMATA_H_
#define AST_RUNTIME_RUNTIME_KEYWORDAUTOMATA_H_

#include <list>
#include <map>
#include <string>

#include "Grammar.h"
#include "Token.h"

class TextTerminalMapCompare {
public:
  bool operator()(const std::string *t1, const std::string *t2) const;
};

class KeyWordAutomata {

public:
  KeyWordAutomata();
  ~KeyWordAutomata();
  KeyWordAutomata(int isEmpty, const Grammar *rootKeyWord);
  std::list<Token *> *buildToken(std::list<Token *> *tokens) const;

public:
  int emptyOrNot;
  const Grammar *rootKeyWord;
  std::map<std::string *, Grammar *, TextTerminalMapCompare> textTerminalMap;

  static const int EMPTY = 0;
  static const int NOT_EMPTY = 1;
};

#endif // AST_RUNTIME_RUNTIME_KEYWORDAUTOMATA_H_
