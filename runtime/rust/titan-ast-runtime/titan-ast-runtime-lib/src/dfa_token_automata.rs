use crate::{
    ast::{Token, TokenData},
    byte_buffer::ByteBuffer,
    byte_buffered_input_stream::{self, ByteBufferedInputStream},
    error::AstAppError,
    fa::FaStateType,
    token_dfa::{TokenDfa, TokenDfaState},
};

#[derive(Clone)]
pub(crate) struct DfaTokenAutomata {}

impl Default for DfaTokenAutomata {
    fn default() -> Self {
        Self {}
    }
}
