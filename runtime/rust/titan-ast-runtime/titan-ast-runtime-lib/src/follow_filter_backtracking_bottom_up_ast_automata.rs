use std::collections::{HashMap, HashSet};

#[derive(Clone)]
pub(crate) struct FollowFilterBacktrackingBottomUpAstAutomata {
    pub(crate) eof_grammar: usize,
    pub(crate) nonterminal_follow_map: HashMap<usize, HashSet<usize>>,
}

impl Default for FollowFilterBacktrackingBottomUpAstAutomata {
    fn default() -> Self {
        Self {
            eof_grammar: 0,
            nonterminal_follow_map: Default::default(),
        }
    }
}
