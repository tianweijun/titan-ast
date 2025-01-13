//
// Created by tian wei jun on 2022/12/4 0004.
//

#ifndef AST__TOKENREDUCINGSYMBOLINPUTSTREAM_H_
#define AST__TOKENREDUCINGSYMBOLINPUTSTREAM_H_

#include "AutomataTmpToken.h"
#include "Token.h"
#include "Result.h"
#include <vector>
#include <set>

class TokenReducingSymbolInputStream {
 public:
  TokenReducingSymbolInputStream(Grammar **innerGrammars,
                                 int countOfInnerGrammars);
  ~TokenReducingSymbolInputStream();

  unsigned int sizeOfTokenReducingSymbols;
  int nextReadIndex;
  AutomataTmpToken *tokenReducingSymbols;
  std::vector<Token *> *sourceTokens;

  AutomataTmpToken *read();
  bool hasNext() const;
  bool hasReadAll() const;
  void clear();
  void init(std::vector<Token *> *sourceTokens);

  AstParseErrorData *getAstParseErrorData(int startIndexOfTokenReducingSymbols,
                                          int endIndexOfTokenReducingSymbols);

private:
  std::set<Grammar *, PtrGrammarContentCompare> innerGrammars;
};

#endif// AST__TOKENREDUCINGSYMBOLINPUTSTREAM_H_
