use crate::{
    ast_automata::SubBacktrackingBottomUpAstAutomata, automata_data::AutomataData,
    super_backtracking_bottom_up_ast_automata::SuperBacktrackingBottomUpAstAutomata,
};

pub(crate) fn build(automata_data: &AutomataData) -> SuperBacktrackingBottomUpAstAutomata {
    let sub_backtracking_bottom_up_ast_automata;
    match automata_data.ast_automata_type {
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
        ast_dfa: automata_data.ast_dfa.clone(),
        start_grammar: automata_data.start_grammar,
    };
}
