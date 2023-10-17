//
// Created by tian wei jun on 2022/11/24 0024.
//

#ifndef AST__RUNTIME__PERSISTENTDATA_H_
#define AST__RUNTIME__PERSISTENTDATA_H_
#include "ByteBuffer.h"
#include "Grammar.h"
#include "ProductionRule.h"
#include "TokenDfa.h"
#include "TokenDfaState.h"
#include <fstream>
#include <string>

class PersistentData {
 public:
  explicit PersistentData(const std::string *automataFilePath);
  PersistentData(const PersistentData &persistentData) = delete;
  PersistentData(const PersistentData &&persistentData) = delete;
  ~PersistentData();

  void init(const std::string *automataFilePath);

  std::ifstream inputStream{};
  ByteBuffer intByteBuffer;
  std::string **stringPool;
  int sizeOfStringPool;
  Grammar **grammars;
  int sizeOfGramamrs;
  ProductionRule **productionRules;
  int sizeOfProductionRules;

  SyntaxDfa *getSyntaxDfaByInputStream();
  void getProductionRulesByInputStream();
  Grammar *getStartGrammarByInputStream();
  TokenDfa *getTokenDfaByInputStream();
  Grammar **getGrammarsByInputStream();
  Grammar *newGrammarByType(GrammarType type);
  std::string *readByteString(int countOfStringBytes);
  std::string **getStringPoolByInputStream();
  void doRead(byte bytes[], int offset, int length);
  int readInt();
  void compact();
};

#endif//AST__RUNTIME__PERSISTENTDATA_H_
