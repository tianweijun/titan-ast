//
// Created by tian wei jun on 2022/12/1 0001.
//

#include "SyntaxDfaState.h"

SyntaxDfaState::SyntaxDfaState() : type(0),
                                   edges(std::map<const Grammar *, SyntaxDfaState *>()),
                                   closingProductionRules(std::list<ProductionRule *>()) {
}

//edges grammar delete by PersistentData.gammars
//edges SyntaxDfaState delete by dfa
//closingProductionRules delete by  PersistentData.productionRules
SyntaxDfaState::~SyntaxDfaState() = default;