use std::collections::{HashMap, HashSet};

use crate::{
    ast::Grammar, ast_automata::AstAutomataType, key_word_automata::KeyWordAutomata,
    persistent_data::PersistentData, syntax_dfa::SyntaxDfa, token_dfa::TokenDfa,
};

#[derive(Clone)]
pub(crate) struct PersistentObject {
    //matadata
    pub(crate) persistent_data: PersistentData,
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

impl PersistentObject {
    pub(crate) fn init(&mut self) {
        // 按文件组织顺序获得各个部分数据，每个部分获取一次
        self.init_string_pool();
        self.init_grammars();
        self.init_key_word_automata();
        self.init_token_dfa();
        self.init_production_rules();
        self.init_ast_automata();

        //clear
        self.persistent_data.compact();
    }

    fn init_ast_automata(&mut self) {
        self.ast_automata_type = self.persistent_data.get_ast_automata_type_by_input_stream();
        match self.ast_automata_type {
            AstAutomataType::BacktrackingBottomUpAstAutomata => {
                self.init_backtracking_bottom_up_ast_automata();
            }
            AstAutomataType::FollowFilterBacktrackingBottomUpAstAutomata => {
                self.init_follow_filter_backtracking_bottom_up_ast_automata();
            }
        }
    }

    fn init_follow_filter_backtracking_bottom_up_ast_automata(&mut self) {
        self.start_grammar = self.persistent_data.get_index_of_grammar_by_input_stream();
        self.init_ast_dfa();

        self.eof_grammar = self.persistent_data.get_index_of_grammar_by_input_stream();
        self.nonterminal_follow_map = self
            .persistent_data
            .get_nonterminal_follow_map_by_input_stream();
    }

    fn init_backtracking_bottom_up_ast_automata(&mut self) {
        self.start_grammar = self.persistent_data.get_index_of_grammar_by_input_stream();
        self.init_ast_dfa();
    }

    fn init_ast_dfa(&mut self) {
        self.ast_dfa = self.persistent_data.get_syntax_dfa_by_input_stream();

        self.ast_dfa.grammars = self.persistent_data.grammars.clone();
        self.ast_dfa.production_rules = self.persistent_data.production_rules.clone();
    }

    fn init_production_rules(&mut self) {
        self.persistent_data.get_production_rules_by_input_stream();
    }

    fn init_token_dfa(&mut self) {
        self.token_dfa = self.persistent_data.get_token_dfa_by_input_stream();
        self.token_dfa.grammrs = self.persistent_data.grammars.clone();
    }

    fn init_key_word_automata(&mut self) {
        self.key_word_automata = self.persistent_data.get_key_word_automata_by_input_stream();
    }

    fn init_grammars(&mut self) {
        self.persistent_data.get_grammars_by_input_stream();
    }

    fn init_string_pool(&mut self) {
        self.persistent_data.get_string_pool_by_input_stream();
    }
}

impl Default for PersistentObject {
    fn default() -> Self {
        Self {
            persistent_data: Default::default(),
            key_word_automata: Default::default(),
            token_dfa: Default::default(),
            ast_automata_type: AstAutomataType::BacktrackingBottomUpAstAutomata,
            start_grammar: 0,
            ast_dfa: Default::default(),
            eof_grammar: Default::default(),
            nonterminal_follow_map: Default::default(),
        }
    }
}
