use std::collections::HashMap;

use crate::{
    ast::{Grammar, Token},
    derived_terminal_grammar_automata_data::DerivedTerminalGrammarAutomataData,
};

#[derive(Clone)]
pub(crate) struct DerivedTerminalGrammarAutomata {
    pub(crate) root_terminal_grammar_map: HashMap<Grammar, HashMap<Vec<u8>, Grammar>>,
}

impl DerivedTerminalGrammarAutomata {
    pub(crate) fn build_token(&self, tokens: &mut Vec<Token>) {
        for i in 0..tokens.len() {
            let token = &mut tokens[i];
            let text_terminal_map_option = self.root_terminal_grammar_map.get(&token.terminal);
            if let Some(text_terminal_map) = text_terminal_map_option {
                let terminal_option = text_terminal_map.get(&token.text);
                if let Some(terminal) = terminal_option {
                    (*token).terminal = (*terminal).clone();
                }
            }
        }
    }

    pub(crate) fn build_derived_terminal_grammar_automata(
        derived_terminal_grammar_automata_data: &DerivedTerminalGrammarAutomataData,
    ) -> DerivedTerminalGrammarAutomata {
        let mut derived_terminal_grammar_automata = DerivedTerminalGrammarAutomata {
            root_terminal_grammar_map: HashMap::with_capacity(
                derived_terminal_grammar_automata_data.count as usize,
            ),
        };
        for map in &derived_terminal_grammar_automata_data.root_terminal_grammar_maps {
            derived_terminal_grammar_automata
                .root_terminal_grammar_map
                .insert(
                    map.root_terminal_grammar.clone(),
                    map.text_terminal_map.clone(),
                );
        }
        return derived_terminal_grammar_automata;
    }
}
