//
// Created by tian wei jun on 2022/12/4 0004.
//

#ifndef AST__TOKENREDUCINGSYMBOLINPUTSTREAM_H_
#define AST__TOKENREDUCINGSYMBOLINPUTSTREAM_H_

#include "AutomataTmpToken.h"
#include "Token.h"
#include <list>
#include <set>

class GrammarCompare {
 public:
  bool operator()(const Grammar *t1, const Grammar *t2);
};

class TokenReducingSymbolInputStream {
 public:
  TokenReducingSymbolInputStream(Grammar **innerGrammars, int countOfInnerGrammars);
  ~TokenReducingSymbolInputStream();

  int sizeOfTokenReducingSymbols;
  int nextReadIndex;
  AutomataTmpToken *tokenReducingSymbols;

  AutomataTmpToken *read();
  bool hasNext() const;
  bool hasReadAll() const;
  void clear();
  void init(std::list<Token *> *sourceTokens);

 private:
  std::set<Grammar *, GrammarCompare> innerGrammars;
};

#endif//AST__TOKENREDUCINGSYMBOLINPUTSTREAM_H_
