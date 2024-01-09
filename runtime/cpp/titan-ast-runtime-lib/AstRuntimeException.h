//
// Created by tian wei jun on 2022/11/23 0023.
//

#ifndef AST_RUNTIME__ASTRUNTIMEEXCEPTION_H_
#define AST_RUNTIME__ASTRUNTIMEEXCEPTION_H_

#include "Runtime.h"
#include <exception>
#include <list>
#include <string>

enum class AstRuntimeExceptionCode : int {
  LOGIC_ERROR = 0,
  IO_ERROR,
  INVALID_ARGUMENT
};

class DLL_PUBLIC AstRuntimeException : public std::exception {
public:
  explicit AstRuntimeException(AstRuntimeExceptionCode code,
                               std::string msg) noexcept;
  explicit AstRuntimeException(AstRuntimeExceptionCode code,
                               const char *msg) noexcept;
  AstRuntimeException(const AstRuntimeException &ex);
  ~AstRuntimeException() noexcept override;

  const char *what() const noexcept override;

  const AstRuntimeExceptionCode code;
  const std::string msg;
};

class DLL_PUBLIC AstRuntimeExceptionResolver {
private:
  AstRuntimeExceptionResolver();
  ~AstRuntimeExceptionResolver();

public:
  AstRuntimeExceptionResolver(AstRuntimeExceptionResolver &ExResolver) = delete;
  AstRuntimeExceptionResolver(AstRuntimeExceptionResolver &&ExResolver) =
      delete;

public:
  static void throwException(const AstRuntimeException &ex);
  static void clearExceptions();
  static bool hasThrewException();
  static const std::list<AstRuntimeException> *getExceptions();
  static void destory();
};
#endif // AST__RUNTIME__ASTRUNTIMEEXCEPTION_H_
