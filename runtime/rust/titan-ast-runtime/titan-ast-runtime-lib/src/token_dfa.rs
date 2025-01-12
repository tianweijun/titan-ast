use std::collections::HashMap;

use crate::{ast::Grammar, fa::FaStateType};

#[derive(Clone)]
pub(crate) struct TokenDfaState {
    pub(crate) type_: i32,
    pub(crate) weight: i32,
    pub(crate) terminal: i32,
    pub(crate) edges: HashMap<i32, usize>,
}

impl Default for TokenDfaState {
    fn default() -> Self {
        return TokenDfaState {
            type_: FaStateType::NONE as i32,
            weight: 0,
            terminal: Default::default(),
            edges: Default::default(),
        };
    }
}

#[derive(Clone)]
pub(crate) struct TokenDfa {
    pub(crate) states: Vec<TokenDfaState>,
    pub(crate) start: TokenDfaState,
    pub(crate) grammrs: Vec<Grammar>,
}

impl TokenDfa {
    pub(crate) fn get_next_state(&self, state: &TokenDfaState, ch: i32) -> Option<TokenDfaState> {
        let index_of_next_state = state.edges.get(&ch);
        if let Some(next_index) = index_of_next_state {
            let index = *next_index;
            let next_state = self.states[index].clone();
            return Some(next_state);
        }

        return None;
    }

    pub(crate) fn get_terminal(&self, state: &TokenDfaState) -> Option<Grammar> {
        if state.terminal >= 0 {
            return Some(self.grammrs[state.terminal as usize].clone());
        }
        return None;
    }
}

impl Default for TokenDfa {
    fn default() -> Self {
        Self {
            states: Default::default(),
            start: Default::default(),
            grammrs: Default::default(),
        }
    }
}
