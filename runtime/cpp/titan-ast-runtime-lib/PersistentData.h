//
// Created by tian wei jun on 2022/11/24 0024.
//

#ifndef AST__RUNTIME__PERSISTENTDATA_H_
#define AST__RUNTIME__PERSISTENTDATA_H_
#include "AstAutomataType.h"
#include "ByteBuffer.h"
#include "Grammar.h"
#include "DerivedTerminalGrammarAutomataData.h"
#include "ProductionRule.h"
#include "Result.h"
#include "TokenDfa.h"
#include "TokenDfaState.h"
#include <fstream>
#include <map>
#include <string>
#include <unordered_set>

struct ReadIntResult {
  bool isOk{false};
  int data{0};
};

struct GetSyntaxDfaByInputStreamResult {
  bool isOk{false};
  SyntaxDfa *data{nullptr};
};

struct GetGrammarByInputStreamResult {
  bool isOk{false};
  Grammar *data{nullptr};
};

struct GetTokenDfaByInputStreamResult {
  bool isOk{false};
  TokenDfa *data{nullptr};
};

struct GetProductionRulesByInputStreamResult {
  bool isOk{false};
  ProductionRule **data{nullptr};
  int size{0};
};

struct GetDerivedTerminalGrammarAutomataDataByInputStreamResult {
  bool isOk{false};
  DerivedTerminalGrammarAutomataData *data{nullptr};
};

struct GetRootTerminalGrammarMapByInputStreamResult {
  bool isOk{false};
};


struct GetGrammarsByInputStreamResult {
  bool isOk{false};
  Grammar **data{nullptr};
  int size{0};
};

struct ReadStringResult {
  bool isOk{false};
  std::string *data{nullptr};
};

struct GetStringPoolByInputStreamResult {
  bool isOk{false};
  std::string **data{nullptr};
  int size{0};
};

struct GetAstAutomataTypeResult {
  bool isOk{false};
  AstAutomataType data{AstAutomataType::BACKTRACKING_BOTTOM_UP_AST_AUTOMATA};
};

struct GetNonterminalFollowMapByInputStreamResult {
  bool isOk{false};
  std::unordered_map<const Grammar *,
                     std::unordered_set<const Grammar *, PtrGrammarContentHash,
                                        PtrGrammarContentEq> *,
                     PtrGrammarContentHash, PtrGrammarContentEq> *data{nullptr};
};

class PersistentData {
 public:
  explicit PersistentData();
  PersistentData(const PersistentData &persistentData) = delete;
  PersistentData(const PersistentData &&persistentData) = delete;
  ~PersistentData();

  BuildAutomataResult init(const std::string *automataFilePath);

  std::ifstream inputStream{};
  ByteBuffer intByteBuffer;
  std::string **stringPool;
  int sizeOfStringPool;
  Grammar **grammars;
  int sizeOfGramamrs;
  ProductionRule **productionRules;
  int sizeOfProductionRules;

  GetSyntaxDfaByInputStreamResult getSyntaxDfaByInputStream();
  void deleteSyntaxDfaStates(SyntaxDfaState **syntaxDfaStates,int sizeOfSyntaxDfaStates);
  GetProductionRulesByInputStreamResult getProductionRulesByInputStream();
  void deleteProductionRules(ProductionRule **productionRules,
                             int sizeOfProductionRules);
  GetGrammarByInputStreamResult getGrammarByInputStream();
  GetTokenDfaByInputStreamResult getTokenDfaByInputStream();
  void deleteTokenDfaStates(TokenDfaState **tokenDfaStates,
                            int sizeOfTokenDfaStates);
  GetDerivedTerminalGrammarAutomataDataByInputStreamResult
  getDerivedTerminalGrammarAutomataDataByInputStream();
  GetRootTerminalGrammarMapByInputStreamResult getRootTerminalGrammarMapByInputStream(
      std::vector<RootTerminalGrammarMap> *rootTerminalGrammarMaps);
  GetGrammarsByInputStreamResult getGrammarsByInputStream();
  void deleteGrammars(Grammar **grammars, int sizeOfGrammars);
  Grammar *newGrammarByType(GrammarType type, int indexOfGrammar);
  ReadStringResult readByteString(int countOfStringBytes);
  GetStringPoolByInputStreamResult getStringPoolByInputStream();
  void deleteStrings(std::string **strings, int sizeOfStrings);
  GetAstAutomataTypeResult getAstAutomataTypeByInputStream();
  GetNonterminalFollowMapByInputStreamResult
  getNonterminalFollowMapByInputStream();

  bool doRead(byte bytes[], int offset, int length);
  BuildAutomataResult sourceDataError();
  ReadIntResult readInt();
  void compact();
};

#endif// AST__RUNTIME__PERSISTENTDATA_H_
