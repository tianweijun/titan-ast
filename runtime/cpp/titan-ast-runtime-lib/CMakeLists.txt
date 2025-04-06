cmake_minimum_required(VERSION 3.21)
project(titan_ast_runtime_lib)

set(CMAKE_CXX_STANDARD 14)

set(CMAKE_PREFIX_PATH "D:/Qt/Qt5.9.0/5.9/mingw53_32/lib/cmake")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-exceptions")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread")

#windows build lib(dll)
add_definitions(-DWIN)
add_definitions(-DBUILDING_DLL)

#windows build exe

#windows use lib
#add_definitions(-DWIN)

#linux build lib
#add_definitions(-DBUILDING_DLL)

#linux build exe

SET(CMAKE_BUILD_TYPE "Debug")

add_library(titan-ast-runtime-lib SHARED Ast.cpp Ast.h
        Token.cpp Token.h Grammar.cpp Grammar.h AutomataTmpToken.h AutomataTmpToken.cpp
        GrammarType.h GrammarAction.h TokenType.h
        RuntimeAutomataAstApplication.cpp RuntimeAutomataAstApplication.h
        PersistentData.cpp PersistentData.h
        StringUtils.cpp StringUtils.h PersistentObject.cpp PersistentObject.h
        DfaTokenAutomata.cpp DfaTokenAutomata.h TokenDfa.cpp TokenDfa.h
        BacktrackingBottomUpAstAutomata.cpp BacktrackingBottomUpAstAutomata.h
        SyntaxDfa.cpp SyntaxDfa.h
        ByteBuffer.cpp ByteBuffer.h TokenDfaState.cpp TokenDfaState.h FaStateType.h
        FaStateType.cpp AutomataTmpAst.h AutomataTmpAst.cpp
        SyntaxDfaState.cpp SyntaxDfaState.h ProductionRule.cpp ProductionRule.h
        ByteBufferedInputStream.cpp ByteBufferedInputStream.h TokenType.cpp
        TokenReducingSymbolInputStream.cpp TokenReducingSymbolInputStream.h
        BacktrackingBottomUpBranch.cpp BacktrackingBottomUpBranch.h ReducingSymbol.cpp
        ReducingSymbol.h AstToken.h AstToken.cpp
        Runtime.h TokenAutomata.h
        TokenAutomataBuilder.cpp TokenAutomataBuilder.h
        AstAutomata.h
        AstAutomataType.h AstAutomataBuilder.cpp
        AstAutomataBuilder.h FollowFilterBacktrackingBottomUpAstAutomata.cpp
        FollowFilterBacktrackingBottomUpAstAutomata.h AstGrammar.h AstGrammar.cpp
        AutomataData.cpp AutomataData.h Result.cpp Result.h
        RuntimeAutomataRichAstApplication.cpp RuntimeAutomataRichAstApplication.h
        AstGeneratorResult2RichResultConverter.cpp
        AstGeneratorResult2RichResultConverter.h
        DerivedTerminalGrammarAutomata.h DerivedTerminalGrammarAutomata.cpp
        DerivedTerminalGrammarAutomataData.h DerivedTerminalGrammarAutomataData.cpp
        SingleDerivedTerminalGrammarAutomata.h SingleDerivedTerminalGrammarAutomata.cpp
        )