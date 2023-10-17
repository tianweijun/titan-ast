//
// Created by tian wei jun on 2023/4/28.
//

#ifndef AST_RUNTIME_RUNTIME_THREADLOCALCONTEXT_H_
#define AST_RUNTIME_RUNTIME_THREADLOCALCONTEXT_H_

#include <list>
#include "AstRuntimeException.h"

namespace ThreadLocalContext {

  thread_local static std::list<AstRuntimeException> *exceptions =
    new std::list<AstRuntimeException>();
};

#endif//AST_RUNTIME_RUNTIME_THREADLOCALCONTEXT_H_
