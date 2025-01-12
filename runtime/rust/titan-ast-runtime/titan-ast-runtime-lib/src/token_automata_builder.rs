use crate::{
    automata_data::AutomataData, byte_buffer::ByteBuffer, key_word_automata, key_word_dfa_token_automata::KeyWordDfaTokenAutomata, super_dfa_token_automata::SuperDfaTokenAutomata, token_automata::SubDfaTokenAutomata
};

pub(crate) fn build(automata_data: &AutomataData) -> SuperDfaTokenAutomata {
    let sub_dfa_token_automata;

    let key_word_empty_or_not = automata_data.key_word_automata.empty_or_not;
    if key_word_empty_or_not == key_word_automata::EMPTY {
        sub_dfa_token_automata = SubDfaTokenAutomata::DfaTokenAutomata(Default::default());
    } else {
        sub_dfa_token_automata =
            SubDfaTokenAutomata::KeyWordDfaTokenAutomata(KeyWordDfaTokenAutomata {
                key_word_automata: automata_data.key_word_automata.clone(),
            });
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
