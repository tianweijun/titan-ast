use crate::{
    ast::{Ast, Token},
    backtracking_bottom_up_ast_automata::BacktrackingBottomUpAstAutomata,
    error::AstAppError,
    follow_filter_backtracking_bottom_up_ast_automata::FollowFilterBacktrackingBottomUpAstAutomata,
};

pub(crate) trait AstBuilder {
    fn build_ast(&mut self, tokens: &Vec<Token>) -> Result<Ast, AstAppError>;
}

#[derive(Clone)]
pub(crate) enum SubBacktrackingBottomUpAstAutomata {
    BacktrackingBottomUpAstAutomata(BacktrackingBottomUpAstAutomata),
    FollowFilterBacktrackingBottomUpAstAutomata(FollowFilterBacktrackingBottomUpAstAutomata),
}

impl SubBacktrackingBottomUpAstAutomata {
    pub(crate) fn get_type(&self) -> AstAutomataType {
        match self {
            SubBacktrackingBottomUpAstAutomata::BacktrackingBottomUpAstAutomata(_) => {
                AstAutomataType::BacktrackingBottomUpAstAutomata
            }
            SubBacktrackingBottomUpAstAutomata::FollowFilterBacktrackingBottomUpAstAutomata(_) => {
                AstAutomataType::FollowFilterBacktrackingBottomUpAstAutomata
            }
        }
    }

    pub(crate) fn get_follow_filter_backtracking_bottom_up_ast_automata_ref(
        &self,
    ) -> Option<&FollowFilterBacktrackingBottomUpAstAutomata> {
        match self {
            SubBacktrackingBottomUpAstAutomata::BacktrackingBottomUpAstAutomata(_) => None,
            SubBacktrackingBottomUpAstAutomata::FollowFilterBacktrackingBottomUpAstAutomata(
                follow_automata,
            ) => Some(follow_automata),
        }
    }
}

impl Default for SubBacktrackingBottomUpAstAutomata {
    fn default() -> Self {
        Self::BacktrackingBottomUpAstAutomata(Default::default())
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub(crate) enum AstAutomataType {
    BacktrackingBottomUpAstAutomata = 0,
    FollowFilterBacktrackingBottomUpAstAutomata = 1,
}

impl Default for AstAutomataType {
    fn default() -> Self {
        Self::BacktrackingBottomUpAstAutomata
    }
}

impl TryFrom<i32> for AstAutomataType {
    type Error = ();

    fn try_from(value: i32) -> Result<Self, Self::Error> {
        match value {
            v if v == AstAutomataType::BacktrackingBottomUpAstAutomata as i32 => {
                Ok(AstAutomataType::BacktrackingBottomUpAstAutomata)
            }
            v if v == AstAutomataType::FollowFilterBacktrackingBottomUpAstAutomata as i32 => {
                Ok(AstAutomataType::FollowFilterBacktrackingBottomUpAstAutomata)
            }
            _ => Err(()),
        }
    }
}
