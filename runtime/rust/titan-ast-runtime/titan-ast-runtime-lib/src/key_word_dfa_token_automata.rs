use crate::{ast::Token, key_word_automata::KeyWordAutomata};

#[derive(Clone)]
pub(crate) struct KeyWordDfaTokenAutomata {
    pub(crate) key_word_automata: KeyWordAutomata,
}

impl KeyWordDfaTokenAutomata {
    pub(crate) fn build_token(&self, tokens: &mut Vec<Token>) {
        self.key_word_automata.build_token(tokens);
    }
}

impl Default for KeyWordDfaTokenAutomata {
    fn default() -> Self {
        Self {
            key_word_automata: Default::default(),
        }
    }
}
