//
// Created by tian wei jun on 2024/10/26.
//

#include "RuntimeAutomataRichAstApplication.h"

RuntimeAutomataRichAstApplication::RuntimeAutomataRichAstApplication()
    : RuntimeAutomataAstApplication(),
      richResultConverter(AstGeneratorResult2RichResultConverter()) {}
RuntimeAutomataRichAstApplication::~RuntimeAutomataRichAstApplication() =
    default;

RuntimeAutomataAstApplication *RuntimeAutomataRichAstApplication::clone() {
  auto *app = new RuntimeAutomataRichAstApplication();
  this->cloneDataToCloner(app);
  // set newline
  app->richResultConverter.setNewline(this->richResultConverter.getNewline());
  return app;
}

void RuntimeAutomataRichAstApplication::setNewline(byte newline) {
  richResultConverter.setNewline(newline);
}

RichAstGeneratorResult *RuntimeAutomataRichAstApplication::buildRichAst(
    const std::string *sourceFilePath) {
  return richResultConverter.convert(buildAst(sourceFilePath));
}
