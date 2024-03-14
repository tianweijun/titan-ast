use crate::{
    byte_buffer::ByteBuffer, key_word_automata,
    key_word_dfa_token_automata::KeyWordDfaTokenAutomata, persistent_object::PersistentObject,
    super_dfa_token_automata::SuperDfaTokenAutomata, token_automata::SubDfaTokenAutomata,
};

pub(crate) fn build(persistent_object: &PersistentObject) -> SuperDfaTokenAutomata {
    let sub_dfa_token_automata;

    let key_word_empty_or_not = persistent_object.key_word_automata.empty_or_not;
    if key_word_empty_or_not == key_word_automata::EMPTY {
        sub_dfa_token_automata = SubDfaTokenAutomata::DfaTokenAutomata(Default::default());
    } else {
        sub_dfa_token_automata =
            SubDfaTokenAutomata::KeyWordDfaTokenAutomata(KeyWordDfaTokenAutomata {
                key_word_automata: persistent_object.key_word_automata.clone(),
            });
    }

    let super_token_automata: SuperDfaTokenAutomata = SuperDfaTokenAutomata {
        dfa: persistent_object.token_dfa.clone(),
        byte_buffered_input_stream: Default::default(),
        tokens: Default::default(),
        one_token_string_builder: ByteBuffer {
            is_big_endian: false,
            buffer: Vec::with_capacity(256),
            limit: 0,
        },
        start_index_of_token: 0,
        sub_dfa_token_automata: sub_dfa_token_automata,
    };
    return super_token_automata;
}
