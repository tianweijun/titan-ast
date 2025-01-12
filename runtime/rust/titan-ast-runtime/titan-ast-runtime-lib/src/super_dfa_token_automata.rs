use crate::{
    ast::Token,
    byte_buffer::ByteBuffer,
    byte_buffered_input_stream::{self, ByteBufferedInputStream},
    error::AstAppError,
    fa::FaStateType,
    token_automata::{SubDfaTokenAutomata, TokenBuilder},
    token_dfa::{TokenDfa, TokenDfaState},
};

pub(crate) enum BuildOneTokenMethodResult {
    Token {
        token: Token,
    },
    AllTextHasBeenBuilt,
    TokenParseError {
        start: usize,
        end: usize,
        error_text: String,
    },
}

#[derive(Clone)]
pub(crate) struct SuperDfaTokenAutomata {
    pub(crate) dfa: TokenDfa,
    pub(crate) sub_dfa_token_automata: SubDfaTokenAutomata,
    pub(crate) one_token_string_builder : ByteBuffer,
}

impl TokenBuilder for SuperDfaTokenAutomata {
    fn build_token(&mut self, source_code_file_path: &String) -> Result<Vec<Token>, AstAppError> {
        let mut tokens = self.do_build_token(source_code_file_path)?;
        match &self.sub_dfa_token_automata {
            SubDfaTokenAutomata::DfaTokenAutomata(_) => Ok(tokens),
            SubDfaTokenAutomata::KeyWordDfaTokenAutomata(key_word_dfa_token_automata) => {
                key_word_dfa_token_automata.build_token(&mut tokens);
                Ok(tokens)
            }
        }
    }
}

impl SuperDfaTokenAutomata {
    fn do_build_token(
        &mut self,
        source_code_file_path: &String,
    ) -> Result<Vec<Token>, AstAppError> {
        //init
        let mut byte_buffered_input_stream: ByteBufferedInputStream = Default::default();
        byte_buffered_input_stream.init(source_code_file_path)?;
        let mut tokens: Vec<Token> = Vec::with_capacity(0);
        //build token
        loop {
            let build_one_token_result = self.build_one_token(&mut byte_buffered_input_stream);
            match build_one_token_result {
                BuildOneTokenMethodResult::Token { token } => {
                    tokens.push(token);
                }
                BuildOneTokenMethodResult::AllTextHasBeenBuilt => {
                    break;
                }
                BuildOneTokenMethodResult::TokenParseError {
                    start,
                    end,
                    error_text,
                } => {
                    return Err(AstAppError::TokenParseError {
                        finished_tokens: tokens,
                        start: start,
                        end: end,
                        error_text: error_text,
                    });
                }
            }
        }
        //ret
        return Ok(tokens);
    }

    fn build_one_token(
        &mut self,
        byte_buffered_input_stream: &mut ByteBufferedInputStream,
    ) -> BuildOneTokenMethodResult {
        self.one_token_string_builder.clear();
        let start_index_of_token = byte_buffered_input_stream.next_read_index;
        let mut ch = byte_buffered_input_stream.read();
        if ch == byte_buffered_input_stream::EOF {
            return BuildOneTokenMethodResult::AllTextHasBeenBuilt;
        }

        // first terminal state
        let mut first_terminal_state: Option<TokenDfaState> = None;
        let mut current_state = self.dfa.start.clone();
        while ch != byte_buffered_input_stream::EOF {
            let next_state = self.dfa.get_next_state(&current_state, ch);
            self.one_token_string_builder.append(ch as u8);
            match next_state {
                Some(next) => {
                    current_state = next;
                }
                None => {
                    //不通
                    break;
                }
            }
            if FaStateType::is_closing_tag(current_state.type_) {
                // 找到终态
                first_terminal_state = Some(current_state.clone());
                byte_buffered_input_stream.mark();
                break;
            }
            ch = byte_buffered_input_stream.read();
        }

        if first_terminal_state.is_none() {
            return BuildOneTokenMethodResult::TokenParseError {
                start: start_index_of_token,
                end: start_index_of_token + self.one_token_string_builder.len(),
                error_text: self.one_token_string_builder.to_string(),
            };
        }
        // 重复嗅探更高优先级或贪婪
        let mut length_of_token = self.one_token_string_builder.len();
        // heaviest terminal state
        let mut heaviest_terminal_state = first_terminal_state.unwrap();

        // 如果没有文本嗅探了直接跳出循环
        // 贪婪或者嗅探高优先级
        ch = byte_buffered_input_stream.read();
        while ch != byte_buffered_input_stream::EOF {
            let next_state = self.dfa.get_next_state(&current_state, ch);
            self.one_token_string_builder.append(ch as u8);
            match next_state {
                Some(next) => {
                    current_state = next;
                }
                None => {
                    //不通
                    break;
                }
            }
            if FaStateType::is_closing_tag(current_state.type_) {
                // 找到终态
                // 新状态具有更高优先级的，接受状态转移
                let is_higher_priority = current_state.weight > heaviest_terminal_state.weight;
                // 相同优先级说明状态是同一个token的终态
                // 如果是贪婪的，则增加识别的字符，接受终态转移
                // 不是贪婪的，则不接受终态转移
                let is_same_and_greediness = heaviest_terminal_state.terminal
                    == current_state.terminal
                    && self
                        .dfa
                        .get_terminal(&heaviest_terminal_state)
                        .unwrap()
                        .is_greediness();

                if is_higher_priority || is_same_and_greediness {
                    heaviest_terminal_state = current_state.clone();
                    length_of_token = self.one_token_string_builder.len();
                    byte_buffered_input_stream.mark();
                }
                // 新token优先级更低直接被覆盖，不接受替换旧终态
            }
            ch = byte_buffered_input_stream.read();
        }
        byte_buffered_input_stream.reset();
        self.one_token_string_builder.set_position(length_of_token);
        //build token
        let terminal_state = heaviest_terminal_state;
        let text_len = self.one_token_string_builder.len();
        let mut bytes = Vec::with_capacity(text_len);
        for i in 0..text_len {
            bytes.push(self.one_token_string_builder.buffer[i]);
        }
        let terminal = self.dfa.get_terminal(&terminal_state).unwrap();
        let token = Token {
            start: start_index_of_token,
            text: bytes,
            type_: terminal.get_action().into(),
            terminal: terminal,
        };
        return BuildOneTokenMethodResult::Token { token: token };
    }
}

impl Default for SuperDfaTokenAutomata {
    fn default() -> Self {
        Self {
            dfa: Default::default(),
            sub_dfa_token_automata: Default::default(),
            one_token_string_builder:ByteBuffer {
                is_big_endian: false,
                buffer: Vec::with_capacity(256),
                position: 0,
            },
        }
    }
}
