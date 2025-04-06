use std::collections::HashMap;

use crate::ast::{Grammar, Token};

#[derive(Clone)]
pub(crate) struct RootTerminalGrammarMap {
    pub(crate) root_terminal_grammar: Grammar,
    pub(crate) text_terminal_map: HashMap<Vec<u8>, Grammar>,
}

#[derive(Clone)]
pub(crate) struct DerivedTerminalGrammarAutomataData {
    pub(crate) count: i32,
    pub(crate) root_terminal_grammar_maps: Vec<RootTerminalGrammarMap>,
}

impl Default for DerivedTerminalGrammarAutomataData {
    fn default() -> Self {
        Self {
            count: 0,
            root_terminal_grammar_maps: Default::default()
        }
    }
}
