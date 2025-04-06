//
// Created by tian wei jun on 2024/1/9.
//

#include "DerivedTerminalGrammarAutomataData.h"

RootTerminalGrammarMap::RootTerminalGrammarMap(
    Grammar *rootTerminalGrammar,
    std::unordered_map<std::string *, Grammar *, TextTerminalMapHash,
                       TextTerminalMapEq>
        textTerminalMap)
    : rootTerminalGrammar(rootTerminalGrammar),
      textTerminalMap(textTerminalMap) {}
RootTerminalGrammarMap::~RootTerminalGrammarMap() = default;

DerivedTerminalGrammarAutomataData::DerivedTerminalGrammarAutomataData()
    : count(0), rootTerminalGrammarMaps(std::vector<RootTerminalGrammarMap>()) {}

DerivedTerminalGrammarAutomataData::~DerivedTerminalGrammarAutomataData() =
    default;
