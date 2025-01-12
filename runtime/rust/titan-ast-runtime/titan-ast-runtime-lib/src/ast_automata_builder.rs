use crate::{
    ast_automata::SubBacktrackingBottomUpAstAutomata, automata_data::AutomataData,
    follow_filter_backtracking_bottom_up_ast_automata::FollowFilterBacktrackingBottomUpAstAutomata,
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
                    FollowFilterBacktrackingBottomUpAstAutomata {
                        eof_grammar: automata_data.eof_grammar,
                        nonterminal_follow_map: automata_data.nonterminal_follow_map.clone(),
                    },
                )
        }
    }
    return SuperBacktrackingBottomUpAstAutomata {
        sub_ast_automata: sub_backtracking_bottom_up_ast_automata,
        ast_dfa: automata_data.ast_dfa.clone(),
        start_grammar: automata_data.start_grammar,
        token_reducing_symbol_input_stream: Default::default(),
        result: Default::default(),
        bottom_up_branchs: Default::default(),
        tried_bottom_up_branchs: Default::default(),
    };
}
