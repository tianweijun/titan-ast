//
// Created by tian wei jun on 2022/11/23 0023.
//

#ifndef AST__RUNTIME__PERSISTENTAUTOMATAASTAPPLICATION_H_
#define AST__RUNTIME__PERSISTENTAUTOMATAASTAPPLICATION_H_

#include "Ast.h"
#include "BacktrackingBottomUpAstAutomata.h"
#include "DfaTokenAutomata.h"
#include "PersistentData.h"
#include "PersistentObject.h"
#include <memory>
#include <mutex>
#include <string>

class PersistentAutomataAstApplication {
public:
  PersistentAutomataAstApplication();
  explicit PersistentAutomataAstApplication(
      const std::string *persistentDataFilePath);
  PersistentAutomataAstApplication(
      const PersistentAutomataAstApplication
          &persistentAutomataAstApplication) = delete;
  PersistentAutomataAstApplication(
      const PersistentAutomataAstApplication
          &&persistentAutomataAstApplication) = delete;
  ~PersistentAutomataAstApplication();

  void buildContext(const std::string *persistentDataFilePath);
  const Ast *buildAst(const std::string *sourceCodeFilePath) const;
  const PersistentAutomataAstApplication *clone() const;

private:
  std::shared_ptr<PersistentObject> persistentObject;
  TokenAutomata *tokenAutomata;
  AstAutomata *astAutomata;

  static std::mutex cloneLock;
};

#endif // AST__RUNTIME__PERSISTENTAUTOMATAASTAPPLICATION_H_
