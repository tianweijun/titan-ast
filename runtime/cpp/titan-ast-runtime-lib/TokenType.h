//
// Created by tian wei jun on 2022/11/17 0017.
//

#ifndef AST__RUNTIME__TOKENTYPE_H_
#define AST__RUNTIME__TOKENTYPE_H_

#include "GrammarAction.h"

enum class TokenType : int { TEXT = 0,
                             SKIP = 1 };

namespace TokenTypeNamespace {
TokenType getByGrammarAction(GrammarAction grammarAction);
}// namespace TokenTypeNamespace
#endif// AST__RUNTIME__TOKENTYPE_H_
