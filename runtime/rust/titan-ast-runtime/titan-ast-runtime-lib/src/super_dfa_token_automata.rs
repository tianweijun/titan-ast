use crate::{
    ast::{Token, TokenData},
    byte_buffer::ByteBuffer,
    byte_buffered_input_stream::{self, ByteBufferedInputStream},
    error::AstAppError,
    fa::FaStateType,
    token_automata::{SubDfaTokenAutomata, TokenBuilder},
    token_dfa::{TokenDfa, TokenDfaState},
};

#[derive(Clone)]
pub(crate) struct SuperDfaTokenAutomata {
    pub(crate) dfa: TokenDfa,
    pub(crate) byte_buffered_input_stream: ByteBufferedInputStream,

    pub(crate) tokens: Vec<Token>,
    pub(crate) one_token_string_builder: ByteBuffer,
    pub(crate) start_index_of_token: usize,

    pub(crate) sub_dfa_token_automata: SubDfaTokenAutomata,
}

impl TokenBuilder for SuperDfaTokenAutomata {
    fn build_token(&mut self, source_code_file_path: &String) -> Result<Vec<Token>, AstAppError> {
        let mut tokens = self.build_token(source_code_file_path)?;
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
    fn build_token(&mut self, source_code_file_path: &String) -> Result<Vec<Token>, AstAppError> {
        //init
        self.byte_buffered_input_stream
            .init(source_code_file_path)?;
        self.tokens.clear();

        //build token
        loop {
            let has_builded_token = self.build_one_token();
            match has_builded_token {
                Ok(has_builded) => {
                    if !has_builded {
                        break;
                    }
                }
                Err(err) => {
                    return Err(err);
                }
            }
        }
        //ret
        let mut ret = Vec::with_capacity(self.tokens.len());
        ret.append(&mut self.tokens);
        self.clear();
        return Ok(ret);
    }

    fn build_one_token(&mut self) -> Result<bool, AstAppError> {
        let terminal_state = self.get_terminal_state()?;
        if let Some(state) = terminal_state {
            let text_len = self.one_token_string_builder.len();
            let mut bytes = Vec::with_capacity(text_len);
            for i in 0..text_len {
                bytes.push(self.one_token_string_builder.buffer[i]);
            }

            let terminal = self.dfa.get_terminal(&state).unwrap();

            let token = Token {
                start: self.start_index_of_token,
                data: TokenData { data: bytes },
                type_: terminal.get_action().into(),
                terminal: terminal,
            };
            self.tokens.push(token);
            return Ok(true);
        } else {
            return Ok(false);
        }
    }

    fn get_terminal_state(&mut self) -> Result<Option<TokenDfaState>, AstAppError> {
        self.one_token_string_builder.clear();
        self.start_index_of_token = self.byte_buffered_input_stream.next_read_index;
        let mut ch = self.byte_buffered_input_stream.read();
        if ch == byte_buffered_input_stream::EOF {
            return Ok(None);
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
                self.byte_buffered_input_stream.mark();
                break;
            }
            ch = self.byte_buffered_input_stream.read();
        }

        if first_terminal_state.is_none() {
            return Err(AstAppError {
                msg: format!(
                    "[{},{}):'{}' does not match any token",
                    self.start_index_of_token,
                    self.start_index_of_token + self.one_token_string_builder.len(),
                    self.one_token_string_builder.to_string(),
                ),
            });
        }
        // 重复嗅探更高优先级或贪婪
        let mut length_of_token = self.one_token_string_builder.len();
        // heaviest terminal state
        let mut heaviest_terminal_state = first_terminal_state.unwrap();

        // 如果没有文本嗅探了直接跳出循环
        // 贪婪或者嗅探高优先级
        ch = self.byte_buffered_input_stream.read();
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
                    self.byte_buffered_input_stream.mark();
                }
                // 新token优先级更低直接被覆盖，不接受替换旧终态
            }
            ch = self.byte_buffered_input_stream.read();
        }

        self.byte_buffered_input_stream.reset();
        self.one_token_string_builder.set_limit(length_of_token);

        return Ok(Some(heaviest_terminal_state));
    }

    fn clear(&mut self) {
        self.tokens.clear();
        self.byte_buffered_input_stream.clear();
        self.one_token_string_builder.clear();
    }
}

impl Default for SuperDfaTokenAutomata {
    fn default() -> Self {
        Self {
            dfa: Default::default(),
            byte_buffered_input_stream: Default::default(),
            tokens: Default::default(),
            one_token_string_builder: Default::default(),
            start_index_of_token: 0,
            sub_dfa_token_automata: Default::default(),
        }
    }
}
