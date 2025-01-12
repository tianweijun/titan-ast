use crate::{
    ast::Token, dfa_token_automata::DfaTokenAutomata, error::AstAppError,
    key_word_dfa_token_automata::KeyWordDfaTokenAutomata,
};

pub(crate) trait TokenBuilder {
    fn build_token(&mut self, source_code_file_path: &String) -> Result<Vec<Token>, AstAppError>;
}

#[derive(Clone)]
pub(crate) enum SubDfaTokenAutomata {
    DfaTokenAutomata(DfaTokenAutomata),
    KeyWordDfaTokenAutomata(KeyWordDfaTokenAutomata),
}

impl Default for SubDfaTokenAutomata {
    fn default() -> Self {
        return Self::DfaTokenAutomata(Default::default());
    }
}
