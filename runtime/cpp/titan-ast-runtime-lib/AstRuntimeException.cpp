//
// Created by tian wei jun on 2022/11/23 0023.
//

#include "AstRuntimeException.h"
#include "ThreadLocalContext.h"

AstRuntimeException::AstRuntimeException(AstRuntimeExceptionCode code, std::string msg) noexcept
    : code(code), msg(msg) {
}

AstRuntimeException::AstRuntimeException(AstRuntimeExceptionCode code, const char *msg) noexcept
    : code(code), msg(msg) {
}

AstRuntimeException::AstRuntimeException(const AstRuntimeException &ex) noexcept
    : code(ex.code), msg(ex.msg) {
}

AstRuntimeException::~AstRuntimeException() noexcept = default;

const char *AstRuntimeException::what() const noexcept {
  return msg.c_str();
}

AstRuntimeExceptionResolver::AstRuntimeExceptionResolver() = default;

AstRuntimeExceptionResolver::~AstRuntimeExceptionResolver() = default;

void AstRuntimeExceptionResolver::throwException(const AstRuntimeException& ex) {
  ThreadLocalContext::exceptions->push_back(ex);
}

void AstRuntimeExceptionResolver::clearExceptions() {
  ThreadLocalContext::exceptions->clear();
}

bool AstRuntimeExceptionResolver::hasThrewException() {
  return !ThreadLocalContext::exceptions->empty();
}

const std::list<AstRuntimeException> *AstRuntimeExceptionResolver::getExceptions() {
  return ThreadLocalContext::exceptions;
}
void AstRuntimeExceptionResolver::destory() {
  if(nullptr!=ThreadLocalContext::exceptions){
    delete ThreadLocalContext::exceptions;
    ThreadLocalContext::exceptions= nullptr;
  }
}
