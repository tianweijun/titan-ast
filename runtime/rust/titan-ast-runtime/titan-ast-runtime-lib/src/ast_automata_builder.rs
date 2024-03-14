use crate::{
    ast_automata::SubBacktrackingBottomUpAstAutomata, persistent_object::PersistentObject,
    super_backtracking_bottom_up_ast_automata::SuperBacktrackingBottomUpAstAutomata,
};

pub(crate) fn build(persistent_object: &PersistentObject) -> SuperBacktrackingBottomUpAstAutomata {
    let sub_backtracking_bottom_up_ast_automata;
    match persistent_object.ast_automata_type {
        crate::ast_automata::AstAutomataType::BacktrackingBottomUpAstAutomata => {
            sub_backtracking_bottom_up_ast_automata =
                SubBacktrackingBottomUpAstAutomata::BacktrackingBottomUpAstAutomata(
                    Default::default(),
                )
        }
        crate::ast_automata::AstAutomataType::FollowFilterBacktrackingBottomUpAstAutomata => {
            sub_backtracking_bottom_up_ast_automata =
                SubBacktrackingBottomUpAstAutomata::FollowFilterBacktrackingBottomUpAstAutomata(
                    Default::default(),
                )
        }
    }
    return SuperBacktrackingBottomUpAstAutomata {
        sub_ast_automata: sub_backtracking_bottom_up_ast_automata,
    };
}
