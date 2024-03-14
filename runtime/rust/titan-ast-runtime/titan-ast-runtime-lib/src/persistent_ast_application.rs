use crate::{
    ast::Ast, ast_automata::AstBuilder, ast_automata_builder, error::AstAppError,
    persistent_object::PersistentObject,
    super_backtracking_bottom_up_ast_automata::SuperBacktrackingBottomUpAstAutomata,
    super_dfa_token_automata::SuperDfaTokenAutomata, token_automata::TokenBuilder,
    token_automata_builder,
};

#[derive(Clone)]
pub(crate) struct PersistentAutomataAstApplication {
    pub(crate) persistent_object: PersistentObject,
    pub(crate) token_automata: SuperDfaTokenAutomata,
    pub(crate) ast_automata: SuperBacktrackingBottomUpAstAutomata,
}

impl PersistentAutomataAstApplication {
    pub(crate) fn build_context(&mut self, automata_file_path: &String) -> Result<(), AstAppError> {
        let persistent_data = &mut self.persistent_object.persistent_data;
        persistent_data.init(automata_file_path)?;

        let persistent_object = &mut self.persistent_object;
        persistent_object.init();

        self.token_automata = token_automata_builder::build(&persistent_object);
        self.ast_automata = ast_automata_builder::build(&persistent_object);
        Ok(())
    }

    pub(crate) fn build_ast(&mut self, source_code_file_path: &String) -> Result<Ast, AstAppError> {
        let tokens = self.token_automata.build_token(source_code_file_path)?;
        self.ast_automata.build_ast(tokens)
    }
}

impl Default for PersistentAutomataAstApplication {
    fn default() -> Self {
        Self {
            persistent_object: Default::default(),
            token_automata: Default::default(),
            ast_automata: Default::default(),
        }
    }
}
