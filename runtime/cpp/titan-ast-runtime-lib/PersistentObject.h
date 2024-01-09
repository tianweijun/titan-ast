//
// Created by tian wei jun on 2022/11/25 0025.
//

#ifndef AST__PERSISTENTOBJECT_H_
#define AST__PERSISTENTOBJECT_H_

#include "Grammar.h"
#include "KeyWordAutomata.h"
#include "PersistentData.h"
#include "SyntaxDfa.h"
#include "TokenDfa.h"

class PersistentObject {
public:
  PersistentObject();
  explicit PersistentObject(PersistentData *persistentData);
  PersistentObject(const PersistentObject &persistentObject) = delete;
  PersistentObject(const PersistentObject &&persistentObject) = delete;
  ~PersistentObject();

  void init();

private:
  void initStringPool() const;
  void initGrammars() const;
  void initKeyWordAutomata();
  void initTokenDfa();
  void initStartGrammar();
  void initProductionRules() const;
  void initAstDfa();

public:
  PersistentData *persistentData;
  KeyWordAutomata *keyWordAutomata;
  const TokenDfa *tokenDfa;
  const SyntaxDfa *astDfa;
  const Grammar *startGrammar;
};

#endif // AST__PERSISTENTOBJECT_H_
