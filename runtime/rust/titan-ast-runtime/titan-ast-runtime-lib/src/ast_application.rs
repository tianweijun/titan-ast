use crate::{
    ast::{Ast, AstGrammar},
    ast_automata::AstBuilder,
    ast_automata_builder,
    automata_data::AutomataData,
    error::AstAppError,
    persistent_object::PersistentObject,
    super_backtracking_bottom_up_ast_automata::SuperBacktrackingBottomUpAstAutomata,
    super_dfa_token_automata::SuperDfaTokenAutomata,
    token_automata::TokenBuilder,
    token_automata_builder,
};

#[derive(Clone)]
pub struct RuntimeAutomataAstApplication {
    //matadata
    pub(crate) grammars: Vec<AstGrammar>,
    //dfa
    pub(crate) token_automata: SuperDfaTokenAutomata,
    pub(crate) ast_automata: SuperBacktrackingBottomUpAstAutomata,
}

impl RuntimeAutomataAstApplication {
    pub fn build_ast(&mut self, source_code_file_path: &String) -> Result<Ast, AstAppError> {
        let tokens = self.token_automata.build_token(source_code_file_path)?;
        self.ast_automata.build_ast(tokens)
    }

    pub fn set_context(&mut self, automata_file_path: &String) -> Result<(), AstAppError> {
        let mut persistent_object: PersistentObject = Default::default();

        persistent_object.persistent_data.init(automata_file_path)?;
        persistent_object.init();

        let automata_data: AutomataData = persistent_object.into();

        self.token_automata = token_automata_builder::build(&automata_data);
        self.ast_automata = ast_automata_builder::build(&automata_data);

        self.set_meta_data(automata_data);

        Ok(())
    }

    pub fn get_grammars(&self) -> Vec<AstGrammar> {
        self.grammars.clone()
    }

    fn set_meta_data(&mut self, automata_data: AutomataData) {
        let mut grammars: Vec<AstGrammar> = Vec::with_capacity(automata_data.grammars.len());
        for original_grammar in automata_data.grammars.iter() {
            grammars.push(original_grammar.get_ast_grammar());
        }
        self.grammars = grammars;
    }
}

impl Default for RuntimeAutomataAstApplication {
    fn default() -> Self {
        Self {
            token_automata: Default::default(),
            ast_automata: Default::default(),
            grammars: Default::default(),
        }
    }
}
