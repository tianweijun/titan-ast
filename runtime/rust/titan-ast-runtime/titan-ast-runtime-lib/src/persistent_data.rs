use std::{
    collections::{HashMap, HashSet},
    fs::File,
    io::Read,
};

use crate::{
    ast::{Grammar, GrammarType, NonterminalGrammar, TerminalGrammar},
    ast_automata::AstAutomataType,
    byte_buffer::{Byte, ByteBuffer},
    error::AstAppError,
    key_word_automata::{self, KeyWordAutomata},
    syntax_dfa::{ProductionRule, SyntaxDfa, SyntaxDfaState},
    token_dfa::{TokenDfa, TokenDfaState},
};

pub(crate) struct PersistentData {
    pub(crate) string_pool: Vec<String>,
    pub(crate) grammars: Vec<Grammar>,
    pub(crate) production_rules: Vec<ProductionRule>,

    byte_input_stream: Option<File>,
    i32_byte_buffer: ByteBuffer,
}

impl Clone for PersistentData {
    fn clone(&self) -> Self {
        Self {
            string_pool: self.string_pool.clone(),
            grammars: self.grammars.clone(),
            production_rules: self.production_rules.clone(),
            byte_input_stream: None,
            i32_byte_buffer: self.i32_byte_buffer.clone(),
        }
    }
}

impl PersistentData {
    pub(crate) fn init(&mut self, automata_file_path: &String) -> Result<(), AstAppError> {
        let automata_file = File::open(automata_file_path)?;
        self.byte_input_stream = Some(automata_file);
        Ok(())
    }

    pub(crate) fn compact(&mut self) {
        self.byte_input_stream = None;
    }

    pub(crate) fn get_nonterminal_follow_map_by_input_stream(
        &mut self,
    ) -> HashMap<usize, HashSet<usize>> {
        let size = self.read_i32() as usize;
        let mut nonterminal_follow_map: HashMap<usize, HashSet<usize>> =
            HashMap::with_capacity(size);
        for _ in 0..size {
            let nonterminal = self.read_i32() as usize;
            let size_of_follow = self.read_i32() as usize;
            let mut follow: HashSet<usize> = HashSet::with_capacity(size_of_follow);
            for _ in 0..size_of_follow {
                follow.insert(self.read_i32() as usize);
            }
            nonterminal_follow_map.insert(nonterminal, follow);
        }
        return nonterminal_follow_map;
    }

    pub(crate) fn get_index_of_grammar_by_input_stream(&mut self) -> usize {
        let index_of_grammar = self.read_i32() as usize;
        return index_of_grammar;
    }

    pub(crate) fn get_ast_automata_type_by_input_stream(&mut self) -> AstAutomataType {
        let int_of_ast_automata_type = self.read_i32();
        return int_of_ast_automata_type.try_into().unwrap();
    }

    pub(crate) fn get_production_rules_by_input_stream(&mut self) {
        let count_of_production_rules = self.read_i32().clone() as usize;
        self.production_rules = Vec::with_capacity(count_of_production_rules);

        for _ in 0..count_of_production_rules {
            let grammar = self.read_i32() as usize;

            let index_of_alias_in_string_pool = self.read_i32();
            let mut alias: Option<String> = None;
            if index_of_alias_in_string_pool >= 0 {
                alias = Some(self.string_pool[index_of_alias_in_string_pool as usize].clone());
            }

            let syntax_dfa = self.get_syntax_dfa_by_input_stream();
            let reducing_dfa = syntax_dfa.into();
            self.production_rules.push(ProductionRule {
                grammar,
                alias,
                reducing_dfa,
            });
        }
    }

    pub(crate) fn get_syntax_dfa_by_input_stream(&mut self) -> SyntaxDfa {
        let mut syntax_dfa: SyntaxDfa = SyntaxDfa::default();
        syntax_dfa.start = 0;

        let size_of_syntax_dfa_states = self.read_i32() as usize;
        syntax_dfa.states = Vec::with_capacity(size_of_syntax_dfa_states);

        // countOfSyntaxDfaStates-(type-countOfEdges-[ch,dest]{countOfEdges}-countOfProductions-productions)
        for index_of_syntax_dfa_states in 0..size_of_syntax_dfa_states {
            let type_ = self.read_i32();

            let size_of_edges = self.read_i32() as usize;
            let mut edges: HashMap<usize, usize> = HashMap::with_capacity(size_of_edges);
            for _ in 0..size_of_edges {
                let index_of_ch = self.read_i32() as usize;
                let index_of_ch_to_state = self.read_i32() as usize;
                edges.insert(index_of_ch, index_of_ch_to_state);
            }
            let size_of_productions = self.read_i32() as usize;
            let mut closing_production_rules = Vec::with_capacity(size_of_productions);
            for _ in 0..size_of_productions {
                let index_of_production_rule = self.read_i32() as usize;
                closing_production_rules.push(index_of_production_rule);
            }

            syntax_dfa.states.push(SyntaxDfaState {
                index: index_of_syntax_dfa_states,
                type_: type_,
                edges: edges,
                closing_production_rules: closing_production_rules,
            });
        }
        return syntax_dfa;
    }

    pub(crate) fn get_token_dfa_by_input_stream(&mut self) -> TokenDfa {
        let mut token_dfa: TokenDfa = Default::default();

        let size_of_token_dfa_states = self.read_i32() as usize;
        token_dfa.states = Vec::with_capacity(size_of_token_dfa_states);
        // countOfTokenDfaStates-(type-weight-terminal-countOfEdges-[ch,dest]{countOfEdges})
        for _ in 0..size_of_token_dfa_states {
            let type_ = self.read_i32();
            let weight = self.read_i32();
            let terminal = self.read_i32();

            let size_of_edges = self.read_i32() as usize;
            let mut edges = HashMap::with_capacity(size_of_edges);
            for _ in 0..size_of_edges {
                let ch = self.read_i32();
                let ch_to_state = self.read_i32() as usize;
                edges.insert(ch, ch_to_state);
            }
            token_dfa.states.push(TokenDfaState {
                type_,
                weight,
                terminal,
                edges,
            });
        }
        token_dfa.start = token_dfa.states[0].clone();
        return token_dfa;
    }

    pub(crate) fn get_key_word_automata_by_input_stream(&mut self) -> KeyWordAutomata {
        let mut key_word_automata = KeyWordAutomata::default();

        key_word_automata.empty_or_not = self.read_i32();
        if key_word_automata.empty_or_not == key_word_automata::EMPTY {
            return key_word_automata;
        }

        let index_of_root_key_word = self.read_i32() as usize;
        key_word_automata.root_key_word = self.grammars[index_of_root_key_word].clone();

        let key_words_size = self.read_i32() as usize;

        let mut text_terminal_map: HashMap<Vec<u8>, Grammar> =
            HashMap::with_capacity(key_words_size);
        for _ in 0..key_words_size {
            let int_of_text = self.read_i32() as usize;
            let text = self.string_pool[int_of_text].clone();

            let int_of_terminal = self.read_i32() as usize;
            let terminal = self.grammars[int_of_terminal].clone();

            text_terminal_map.insert(
                text.into_bytes(),
                terminal,
            );
        }
        key_word_automata.text_terminal_map = text_terminal_map;

        return key_word_automata;
    }

    pub(crate) fn get_grammars_by_input_stream(&mut self) {
        let size_of_grammars = self.read_i32() as usize;
        self.grammars = Vec::with_capacity(size_of_grammars);
        for index_of_grammars in 0..size_of_grammars {
            let i32_type = self.read_i32();
            let grammar_type: GrammarType = i32_type.try_into().unwrap();

            let index_string_of_name = self.read_i32() as usize;
            let name = (self.string_pool[index_string_of_name]).clone();

            let i32_action = self.read_i32();
            let action = i32_action.try_into().unwrap();

            match grammar_type {
                GrammarType::Terminal => {
                    let i32_lookahead_matching_mode = self.read_i32();
                    let lookahead_matching_mode = i32_lookahead_matching_mode.try_into().unwrap();

                    self.grammars
                        .push(Grammar::TerminalGrammar(TerminalGrammar {
                            index: index_of_grammars,
                            name: name,
                            type_: grammar_type,
                            action: action,
                            lookahead_matching_mode: lookahead_matching_mode,
                        }));
                }
                GrammarType::Nonterminal => {
                    self.grammars
                        .push(Grammar::NonterminalGrammar(NonterminalGrammar {
                            index: index_of_grammars,
                            name: name,
                            type_: grammar_type,
                            action: action,
                        }));
                }
                GrammarType::TerminalFragment => {}
            }
        }
    }

    pub(crate) fn get_string_pool_by_input_stream(&mut self) {
        let size_of_strings = self.read_i32();
        self.string_pool = Vec::with_capacity(size_of_strings as usize);
        for _ in 0..size_of_strings {
            let count_of_string_bytes = self.read_i32();
            let str = self.read_string(count_of_string_bytes as usize);
            self.string_pool.push(str);
        }
    }

    fn read_string(&mut self, count_of_string_bytes: usize) -> String {
        let mut bytes: Vec<Byte> = Vec::with_capacity(count_of_string_bytes);
        for _ in 0..count_of_string_bytes {
            bytes.push(0);
        }
        self.do_buffer_read(&mut bytes[0..count_of_string_bytes]);
        let mut str = String::with_capacity(count_of_string_bytes);
        for byte in bytes {
            str.push(byte as char);
        }
        return str;
    }

    fn do_buffer_read(&mut self, buffer: &mut [Byte]) -> usize {
        let input_stream = self.byte_input_stream.as_mut().unwrap();
        let count = input_stream.read(buffer).unwrap();
        return count;
    }

    fn read_i32(&mut self) -> i32 {
        let end = self.i32_byte_buffer.buffer.len();
        self.do_i32_buffer_read(0, end);
        self.i32_byte_buffer.set_position(end);
        return self.i32_byte_buffer.get_i32();
    }

    fn do_i32_buffer_read(&mut self, offset: usize, end: usize) -> usize {
        let input_stream = self.byte_input_stream.as_mut().unwrap();
        let count = input_stream
            .read(&mut self.i32_byte_buffer.buffer[offset..end])
            .unwrap();
        return count;
    }
}

impl Default for PersistentData {
    fn default() -> Self {
        Self {
            grammars: Vec::with_capacity(0),
            string_pool: Vec::with_capacity(0),
            byte_input_stream: None,
            i32_byte_buffer: ByteBuffer {
                is_big_endian: true,
                buffer: vec![0, 0, 0, 0],
                position: 0,
            },
            production_rules: Default::default(),
        }
    }
}
