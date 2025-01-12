use std::collections::{HashMap, HashSet};

use crate::{
    ast::Grammar,
    ast_automata::AstAutomataType,
    key_word_automata::KeyWordAutomata,
    persistent_object::PersistentObject,
    syntax_dfa::{ProductionRule, SyntaxDfa},
    token_dfa::TokenDfa,
};

pub(crate) struct AutomataData {
    //matadata
    pub(crate) string_pool: Vec<String>,
    pub(crate) grammars: Vec<Grammar>,
    pub(crate) production_rules: Vec<ProductionRule>,
    //token dfa
    pub(crate) key_word_automata: KeyWordAutomata,
    pub(crate) token_dfa: TokenDfa,
    //ast dfa
    pub(crate) ast_automata_type: AstAutomataType,
    pub(crate) start_grammar: usize,
    pub(crate) ast_dfa: SyntaxDfa,
    pub(crate) eof_grammar: usize,
    pub(crate) nonterminal_follow_map: HashMap<usize, HashSet<usize>>,
}

impl From<PersistentObject> for AutomataData {
    fn from(value: PersistentObject) -> Self {
        Self {
            string_pool: value.persistent_data.string_pool,
            grammars: value.persistent_data.grammars,
            production_rules: value.persistent_data.production_rules,
            key_word_automata: value.key_word_automata,
            token_dfa: value.token_dfa,
            ast_automata_type: value.ast_automata_type,
            start_grammar: value.start_grammar,
            ast_dfa: value.ast_dfa,
            eof_grammar: value.eof_grammar,
            nonterminal_follow_map: value.nonterminal_follow_map,
        }
    }
}

impl Default for AutomataData {
    fn default() -> Self {
        Self {
            string_pool: Default::default(),
            grammars: Default::default(),
            production_rules: Default::default(),
            key_word_automata: Default::default(),
            token_dfa: Default::default(),
            ast_automata_type: Default::default(),
            start_grammar: Default::default(),
            ast_dfa: Default::default(),
            eof_grammar: Default::default(),
            nonterminal_follow_map: Default::default(),
        }
    }
}
