use std::collections::HashMap;

use crate::ast::{Grammar, Token};

pub(crate) const EMPTY: i32 = 0;
pub(crate) const NOT_EMPTY: i32 = 1;

#[derive(Clone)]
pub(crate) struct KeyWordAutomata {
    pub(crate) empty_or_not: i32,
    pub(crate) root_key_word: Grammar,
    pub(crate) text_terminal_map: HashMap<Vec<u8>, Grammar>,
}

impl KeyWordAutomata {
    pub(crate) fn build_token(&self, tokens: &mut Vec<Token>) {
        for i in 0..tokens.len() {
            let token = &mut tokens[i];
            if token.terminal == self.root_key_word {
                let key_word_grammar = self.text_terminal_map.get(&token.text);
                if let Some(key_word) = key_word_grammar {
                    (*token).terminal = (*key_word).clone();
                }
            }
        }
    }
}

impl Default for KeyWordAutomata {
    fn default() -> Self {
        Self {
            empty_or_not: EMPTY,
            root_key_word: Default::default(),
            text_terminal_map: Default::default(),
        }
    }
}
