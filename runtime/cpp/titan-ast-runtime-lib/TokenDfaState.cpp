//
// Created by tian wei jun on 2022/11/30 0030.
//

#include "TokenDfaState.h"

TokenDfaState::TokenDfaState()
    : type((int) FaStateEnumType::NONE), weight(0), terminal(nullptr),
      edges(std::unordered_map<byte, TokenDfaState *>()) {}
// terminal delete by PersistentData.grammars
// TokenDfaState in edges delete by TokenDfa
TokenDfaState::~TokenDfaState() = default;