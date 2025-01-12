//
// Created by tian wei jun on 2023/6/27.
//

#ifndef AST__RUNTIME_RUNTIME_RUNTIME_H_
#define AST__RUNTIME_RUNTIME_RUNTIME_H_
#if defined WIN || defined __CYGWIN__
#define DLL_LOCAL
#ifdef BUILDING_DLL
#ifdef __GNUC__
#define DLL_PUBLIC __attribute__((dllexport))
#else
#define DLL_PUBLIC __declspec(dllexport)
#endif
#else
#ifdef __GNUC__
#define DLL_PUBLIC __attribute__((dllimport))
#else
#define DLL_PUBLIC __declspec(dllimport)
#endif
#endif
#else
#if __GNUC__ >= 4 && defined BUILDING_DLL
#define DLL_PUBLIC __attribute__((visibility("default")))
#define DLL_LOCAL __attribute__((visibility("hidden")))
#else
#define DLL_PUBLIC
#define DLL_LOCAL
#endif
#endif
#endif// AST__RUNTIME_RUNTIME_RUNTIME_H_
