use crate::{
    ast::Token, dfa_token_automata::DfaTokenAutomata, error::AstAppError,
    derived_terminal_grammar_automata::DerivedTerminalGrammarAutomata,
};

pub(crate) trait TokenBuilder {
    fn build_token(&mut self, source_code_file_path: &String) -> Result<Vec<Token>, AstAppError>;
}

#[derive(Clone)]
pub(crate) enum SubDfaTokenAutomata {
    DfaTokenAutomata(DfaTokenAutomata),
    DerivedTerminalGrammarAutomata(DerivedTerminalGrammarAutomata),
}

impl Default for SubDfaTokenAutomata {
    fn default() -> Self {
        return Self::DfaTokenAutomata(Default::default());
    }
}
