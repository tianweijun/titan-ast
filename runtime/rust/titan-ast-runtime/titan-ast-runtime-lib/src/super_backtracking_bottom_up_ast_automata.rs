use std::collections::BTreeSet;

use crate::{
    ast::{Ast, Token},
    ast_automata::{AstBuilder, SubBacktrackingBottomUpAstAutomata},
    automata_tmp_ast::AutomataTmpAst,
    backtracking_bottom_up_branch::BacktrackingBottomUpBranch,
    syntax_dfa::SyntaxDfa,
    token_reducing_symbol_input_stream::TokenReducingSymbolInputStream,
};

#[derive(Clone)]
pub(crate) struct SuperBacktrackingBottomUpAstAutomata {
    pub(crate) ast_dfa: SyntaxDfa,
    pub(crate) start_grammar: usize,
    pub(crate) token_reducing_symbol_input_stream: TokenReducingSymbolInputStream,
    pub(crate) bottom_up_branchs: BTreeSet<BacktrackingBottomUpBranch>,
    pub(crate) tried_bottom_up_branchs: BTreeSet<BacktrackingBottomUpBranch>,
    pub(crate) result: AutomataTmpAst,

    pub(crate) sub_ast_automata: SubBacktrackingBottomUpAstAutomata,
}

impl SuperBacktrackingBottomUpAstAutomata {
    fn init(&mut self, tokens: Vec<Token>) {
        self.clear();
        self.token_reducing_symbol_input_stream.init(tokens);
    }

    fn clear(&mut self) {}
}

impl AstBuilder for SuperBacktrackingBottomUpAstAutomata {
    fn build_ast(&mut self, tokens: Vec<Token>) -> Result<Ast, crate::error::AstAppError> {
        self.init(tokens);

        Ok(Default::default())
    }
}

impl Default for SuperBacktrackingBottomUpAstAutomata {
    fn default() -> Self {
        Self {
            sub_ast_automata: Default::default(),
            ast_dfa: Default::default(),
            start_grammar: 0,
            token_reducing_symbol_input_stream: Default::default(),
            result: Default::default(),
            bottom_up_branchs: Default::default(),
            tried_bottom_up_branchs: Default::default(),
        }
    }
}
