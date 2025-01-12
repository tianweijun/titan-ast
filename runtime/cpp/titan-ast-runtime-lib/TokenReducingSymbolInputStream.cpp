//
// Created by tian wei jun on 2022/12/4 0004.
//

#include "TokenReducingSymbolInputStream.h"

TokenReducingSymbolInputStream::TokenReducingSymbolInputStream(
    Grammar **innerGrammars, int countOfInnerGrammars)
    : innerGrammars(std::set<Grammar *, PtrGrammarContentCompare>()),
      tokenReducingSymbols(nullptr), nextReadIndex(0),
      sizeOfTokenReducingSymbols(0) {
  for (int i = 0; i < countOfInnerGrammars; i++) {
    Grammar *grammar = innerGrammars[i];
    if (grammar->type == GrammarType::TERMINAL) {
      this->innerGrammars.insert(grammar);
    }
  }
}

void TokenReducingSymbolInputStream::clear() {
  delete[] tokenReducingSymbols;
  tokenReducingSymbols = nullptr;
  sizeOfTokenReducingSymbols = 0;

  nextReadIndex = 0;
}

TokenReducingSymbolInputStream::~TokenReducingSymbolInputStream() { clear(); }

void TokenReducingSymbolInputStream::init(std::list<Token *> *sourceTokens) {
  clear();
  std::list<Token *> textTokens;
  for (auto token : *sourceTokens) {
    if (token->type == TokenType::TEXT) {
      textTokens.push_back(token);
    }
  }
  sizeOfTokenReducingSymbols = textTokens.size();
  tokenReducingSymbols = new AutomataTmpToken[sizeOfTokenReducingSymbols];
  int indexOfTokenReducingSymbol = 0;
  for (auto token : textTokens) {
    AutomataTmpToken *automataTmpToken =
        &tokenReducingSymbols[indexOfTokenReducingSymbol++];
    automataTmpToken->shallowCopy(token);
    // 使grammar变为context的内部grammar,以适应语法自动机要求
    automataTmpToken->terminal = *innerGrammars.find(&token->terminal);
  }
}

AutomataTmpToken *TokenReducingSymbolInputStream::read() {
  if (hasNext()) {
    return &tokenReducingSymbols[nextReadIndex++];
  }
  return nullptr;
}

bool TokenReducingSymbolInputStream::hasNext() const {
  return nextReadIndex >= 0 && nextReadIndex < sizeOfTokenReducingSymbols;
}

bool TokenReducingSymbolInputStream::hasReadAll() const {
  return nextReadIndex >= sizeOfTokenReducingSymbols;
}
