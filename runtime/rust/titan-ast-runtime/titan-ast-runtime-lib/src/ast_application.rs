use crate::{
    ast::Ast, error::AstAppError, persistent_ast_application::PersistentAutomataAstApplication,
};

#[derive(Clone)]
pub struct RuntimeAutomataAstApplication {
    pub(crate) persistent_automata_ast_application: PersistentAutomataAstApplication,
}

impl RuntimeAutomataAstApplication {
    pub fn set_context(&mut self, automata_file_path: &String) -> Result<(), AstAppError> {
        self.persistent_automata_ast_application
            .build_context(automata_file_path)?;
        Ok(())
    }

    pub fn build_ast(&mut self, source_code_file_path: &String) -> Result<Ast, AstAppError> {
        return self
            .persistent_automata_ast_application
            .build_ast(source_code_file_path);
    }
}

impl Default for RuntimeAutomataAstApplication {
    fn default() -> Self {
        Self {
            persistent_automata_ast_application: Default::default(),
        }
    }
}
