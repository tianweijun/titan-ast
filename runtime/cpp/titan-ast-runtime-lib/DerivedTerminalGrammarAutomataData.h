//
// Created by tian wei jun on 2024/1/9.
//

#ifndef AST_RUNTIME_RUNTIME_DERIVEDTERMINALGRAMMARAUTOMATADATA_H_
#define AST_RUNTIME_RUNTIME_DERIVEDTERMINALGRAMMARAUTOMATADATA_H_

#include <string>
#include <unordered_map>
#include <vector>

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

class RootTerminalGrammarMap {
public:
  RootTerminalGrammarMap(
      Grammar *rootTerminalGrammar,
      std::unordered_map<std::string *, Grammar *, TextTerminalMapHash,
                         TextTerminalMapEq>
          textTerminalMap);
  ~RootTerminalGrammarMap();

public:
  Grammar *rootTerminalGrammar;
  std::unordered_map<std::string *, Grammar *, TextTerminalMapHash,
                     TextTerminalMapEq>
      textTerminalMap;
};

class DerivedTerminalGrammarAutomataData {

public:
  DerivedTerminalGrammarAutomataData();
  ~DerivedTerminalGrammarAutomataData();

public:
  int count;
  std::vector<RootTerminalGrammarMap> rootTerminalGrammarMaps;
};

#endif // AST_RUNTIME_RUNTIME_DERIVEDTERMINALGRAMMARAUTOMATADATA_H_
