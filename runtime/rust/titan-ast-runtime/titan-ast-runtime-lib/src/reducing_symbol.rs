use std::cmp::Ordering;

use crate::automata_tmp_ast::AutomataTmpAst;

#[derive(Clone)]
pub(crate) struct ReducingSymbol {
    pub(crate) ast_of_current_dfa_state: AutomataTmpAst,
    pub(crate) current_dfa_state: usize,
    pub(crate) end_index_of_token: i32,
}

impl ReducingSymbol {
    pub(crate) fn equals(&self, other: &Self) -> bool {
        self.end_index_of_token == other.end_index_of_token
            && self.current_dfa_state == other.current_dfa_state
            && self.ast_of_current_dfa_state.get_grammar()
                == other.ast_of_current_dfa_state.get_grammar()
    }

    pub(crate) fn compare(&self, other: &Self) -> Ordering {
        if self.end_index_of_token < other.end_index_of_token {
            return Ordering::Less;
        }
        if self.end_index_of_token > other.end_index_of_token {
            return Ordering::Greater;
        }

        if self.current_dfa_state < other.current_dfa_state {
            return Ordering::Less;
        }
        if self.current_dfa_state > other.current_dfa_state {
            return Ordering::Greater;
        }

        if self.ast_of_current_dfa_state.get_grammar()
            < other.ast_of_current_dfa_state.get_grammar()
        {
            return Ordering::Less;
        }
        if self.ast_of_current_dfa_state.get_grammar()
            > other.ast_of_current_dfa_state.get_grammar()
        {
            return Ordering::Greater;
        }

        return Ordering::Equal;
    }
}

impl Default for ReducingSymbol {
    fn default() -> Self {
        Self {
            ast_of_current_dfa_state: Default::default(),
            current_dfa_state: 0,
            end_index_of_token: -1,
        }
    }
}
