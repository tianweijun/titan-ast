//
// Created by tian wei jun on 2022/12/4 0004.
//

#include <list>
#include <sstream>
#include "TokenReducingSymbolInputStream.h"

TokenReducingSymbolInputStream::TokenReducingSymbolInputStream(
    Grammar **innerGrammars, int countOfInnerGrammars)
    : innerGrammars(std::set<Grammar *, PtrGrammarContentCompare>()),
      tokenReducingSymbols(nullptr), nextReadIndex(0),sourceTokens(nullptr),
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

  sizeOfTokenReducingSymbols = 0;
  nextReadIndex = 0;
}

TokenReducingSymbolInputStream::~TokenReducingSymbolInputStream() { clear(); }

void TokenReducingSymbolInputStream::init(std::vector<Token *> *sourceTokens) {
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
  this->sourceTokens = sourceTokens;
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


AstParseErrorData *TokenReducingSymbolInputStream::getAstParseErrorData(
    int startIndexOfTokenReducingSymbols, int endIndexOfTokenReducingSymbols) {
  AutomataTmpToken *startToken = &tokenReducingSymbols[startIndexOfTokenReducingSymbols];
  AutomataTmpToken *endToken = &tokenReducingSymbols[endIndexOfTokenReducingSymbols];
  int startIndexByte = startToken->start;
  int endIndexByte = endToken->start + (int) endToken->text->length();


  std::stringstream tokenInfo;
  int indexOfStartSourceToken = 0;
  int indexOfSourceToken = startIndexOfTokenReducingSymbols;
  for (; indexOfSourceToken < sourceTokens->size(); indexOfSourceToken++) {
    auto token = (*sourceTokens)[indexOfSourceToken];
    if (token->start == startIndexByte) {
      indexOfStartSourceToken = indexOfSourceToken;
      break;
    }
  }
  for (indexOfSourceToken = indexOfStartSourceToken;
       indexOfSourceToken < sourceTokens->size();
       indexOfSourceToken++) {
    auto token = (*sourceTokens)[indexOfSourceToken];
    if (token->start < endIndexByte) {
      tokenInfo << token->text;
    } else {
      break;
    }
  }
  return new AstParseErrorData(startIndexByte, endIndexByte, tokenInfo.str());
}
