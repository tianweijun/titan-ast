//
// Created by tian wei jun on 2024/1/10.
//

#ifndef AST_RUNTIME_RUNTIME_TOKENAUTOMATA_H_
#define AST_RUNTIME_RUNTIME_TOKENAUTOMATA_H_

#include "Token.h"
#include <list>
class TokenAutomata {
public:
  virtual std::list<Token *> *buildToken(const std::string *sourceFilePath) = 0;
  virtual ~TokenAutomata() = default;
};

#endif // AST_RUNTIME_RUNTIME_TOKENAUTOMATA_H_
