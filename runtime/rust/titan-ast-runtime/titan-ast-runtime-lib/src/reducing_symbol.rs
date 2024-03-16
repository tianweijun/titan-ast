use crate::automata_tmp_ast::AutomataTmpAst;

#[derive(Clone)]
pub(crate) struct ReducingSymbol {
    pub(crate) ast_of_current_dfa_state: AutomataTmpAst,
    pub(crate) current_dfa_state: usize,
}

impl Default for ReducingSymbol {
    fn default() -> Self {
        Self {}
    }
}
