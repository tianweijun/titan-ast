//
// Created by tian wei jun on 2024/10/26.
//

#ifndef AST_RUNTIME_RUNTIME_RUNTIMEAUTOMATARICHASTAPPLICATION_H_
#define AST_RUNTIME_RUNTIME_RUNTIMEAUTOMATARICHASTAPPLICATION_H_
#include "AstGeneratorResult2RichResultConverter.h"
#include "Runtime.h"
#include "RuntimeAutomataAstApplication.h"

class DLL_PUBLIC RuntimeAutomataRichAstApplication
    : public RuntimeAutomataAstApplication {
 public:
  RuntimeAutomataRichAstApplication();
  RuntimeAutomataRichAstApplication(
      const RuntimeAutomataRichAstApplication
          &runtimeAutomataRichAstApplication) = delete;
  RuntimeAutomataRichAstApplication(
      const RuntimeAutomataRichAstApplication
          &&runtimeAutomataRichAstApplication) = delete;
  ~RuntimeAutomataRichAstApplication() override;

  RuntimeAutomataAstApplication *clone() override;

  void setNewline(byte newline);
  RichAstGeneratorResult *buildRichAst(const std::string *sourceFilePath);

 private:
  AstGeneratorResult2RichResultConverter richResultConverter;
};

#endif// AST_RUNTIME_RUNTIME_RUNTIMEAUTOMATARICHASTAPPLICATION_H_
