use crate::{
    automata_data::AutomataData, byte_buffer::ByteBuffer, derived_terminal_grammar_automata::DerivedTerminalGrammarAutomata, super_dfa_token_automata::SuperDfaTokenAutomata, token_automata::SubDfaTokenAutomata
};

pub(crate) fn build(automata_data: &AutomataData) -> SuperDfaTokenAutomata {
    let sub_dfa_token_automata;

    if automata_data.derived_terminal_grammar_automata_data.count==0 {
        sub_dfa_token_automata = SubDfaTokenAutomata::DfaTokenAutomata(Default::default());
    } else {
        sub_dfa_token_automata =
            SubDfaTokenAutomata::DerivedTerminalGrammarAutomata(DerivedTerminalGrammarAutomata::build_derived_terminal_grammar_automata(
                &automata_data.derived_terminal_grammar_automata_data,
            ));
    }

    let super_token_automata: SuperDfaTokenAutomata = SuperDfaTokenAutomata {
        dfa: automata_data.token_dfa.clone(),
        sub_dfa_token_automata: sub_dfa_token_automata,
        one_token_string_builder: ByteBuffer {
            is_big_endian: false,
            buffer: Vec::with_capacity(256),
            position: 0,
        },
    };
    return super_token_automata;
}
