use crate::{
    ast_automata::{AstBuilder, SubBacktrackingBottomUpAstAutomata},
    syntax_dfa::SyntaxDfa,
};

#[derive(Clone)]
pub(crate) struct SuperBacktrackingBottomUpAstAutomata {
    pub(crate) ast_dfa: SyntaxDfa,
    pub(crate) start_grammar: usize,

    pub(crate) sub_ast_automata: SubBacktrackingBottomUpAstAutomata,
}

impl SuperBacktrackingBottomUpAstAutomata {}

impl AstBuilder for SuperBacktrackingBottomUpAstAutomata {
    fn build_ast(
        &mut self,
        tokens: Vec<crate::ast::Token>,
    ) -> Result<crate::ast::Ast, crate::error::AstAppError> {
        Ok(Default::default())
    }
}

impl Default for SuperBacktrackingBottomUpAstAutomata {
    fn default() -> Self {
        Self {
            sub_ast_automata: Default::default(),
            ast_dfa: Default::default(),
            start_grammar: 0,
        }
    }
}
