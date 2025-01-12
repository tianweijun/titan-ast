//
// Created by tian wei jun on 2022/11/25 0025.
//

#ifndef AST__PERSISTENTOBJECT_H_
#define AST__PERSISTENTOBJECT_H_

#include "AutomataData.h"
#include "Grammar.h"
#include "KeyWordAutomata.h"
#include "PersistentData.h"
#include "SyntaxDfa.h"
#include "TokenDfa.h"
#include <unordered_set>

class PersistentObject {
 public:
  PersistentObject();
  explicit PersistentObject(PersistentData *persistentData);
  PersistentObject(const PersistentObject &persistentObject) = delete;
  PersistentObject(const PersistentObject &&persistentObject) = delete;
  ~PersistentObject();

  BuildAutomataResult init();

  void *setAutomataData(AutomataData *automataData) const;

 private:
  BuildAutomataResult initStringPool() const;
  BuildAutomataResult initGrammars() const;
  BuildAutomataResult initKeyWordAutomata();
  BuildAutomataResult initTokenDfa();
  BuildAutomataResult initProductionRules() const;

  BuildAutomataResult initAstAutomata();
  BuildAutomataResult initBacktrackingBottomUpAstAutomata();
  BuildAutomataResult initStartGrammar();
  BuildAutomataResult initAstDfa();
  BuildAutomataResult initFollowFilterBacktrackingBottomUpAstAutomata();
  BuildAutomataResult initEofGrammar();
  BuildAutomataResult initNonterminalFollowMap();

 public:
  PersistentData *persistentData;
  const KeyWordAutomata *keyWordAutomata;
  const TokenDfa *tokenDfa;

  AstAutomataType astAutomataType;
  const SyntaxDfa *astDfa;
  const Grammar *startGrammar;

  const Grammar *eofGrammar;
  const std::unordered_map<
      const Grammar *,
      std::unordered_set<const Grammar *, PtrGrammarContentHash,
                         PtrGrammarContentEq> *,
      PtrGrammarContentHash, PtrGrammarContentEq> *nonterminalFollowMap;
  BuildAutomataResult initByPersistentData(PersistentData *persistentData);
};

#endif// AST__PERSISTENTOBJECT_H_
