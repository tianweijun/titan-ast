//
// Created by tian wei jun on 2022/11/25 0025.
//

#ifndef AST__PERSISTENTOBJECT_H_
#define AST__PERSISTENTOBJECT_H_

#include <unordered_set>
#include "Grammar.h"
#include "KeyWordAutomata.h"
#include "PersistentData.h"
#include "SyntaxDfa.h"
#include "TokenDfa.h"
#include "AutomataData.h"

class PersistentObject {
public:
  PersistentObject();
  explicit PersistentObject(PersistentData *persistentData);
  PersistentObject(const PersistentObject &persistentObject) = delete;
  PersistentObject(const PersistentObject &&persistentObject) = delete;
  ~PersistentObject();

  void init();

  void* setAutomataData(AutomataData* automataData) const;

private:
  void initStringPool() const;
  void initGrammars() const;
  void initKeyWordAutomata();
  void initTokenDfa();
  void initProductionRules() const;

  void initAstAutomata();
  void initBacktrackingBottomUpAstAutomata();
  void initStartGrammar();
  void initAstDfa();
  void initFollowFilterBacktrackingBottomUpAstAutomata();
  void initEofGrammar();
  void initNonterminalFollowMap();

public:
  PersistentData *persistentData;
  const KeyWordAutomata *keyWordAutomata;
  const TokenDfa *tokenDfa;

  AstAutomataType astAutomataType;
  const SyntaxDfa *astDfa;
  const Grammar *startGrammar;

  const Grammar *eofGrammar;
  const std::unordered_map<const Grammar *,
                 std::unordered_set<const Grammar *, PtrGrammarContentHash,PtrGrammarContentEq> *,
                 PtrGrammarContentHash,PtrGrammarContentEq> *nonterminalFollowMap;
  void initByPersistentData(PersistentData *persistentData);
};

#endif // AST__PERSISTENTOBJECT_H_
