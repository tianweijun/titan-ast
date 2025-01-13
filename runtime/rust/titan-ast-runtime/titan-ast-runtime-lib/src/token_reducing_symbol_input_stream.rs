use crate::{ast::{Token, TokenType}, error::AstAppError};

#[derive(Clone)]
pub(crate) struct TokenReducingSymbolInputStream {
    pub(crate) next_read_index: usize,
    pub(crate) token_reducing_symbols: Vec<Token>,
    pub(crate) source_tokens: Vec<Token>,
}

impl TokenReducingSymbolInputStream {
    pub(crate) fn init(&mut self, tokens: &Vec<Token>) {
        let mut text_tokens: Vec<Token> = Vec::with_capacity(tokens.len());
        for token in tokens.into_iter() {
            if token.type_ == TokenType::Text {
                text_tokens.push(token.clone());
            }
        }
        self.token_reducing_symbols = Vec::with_capacity(text_tokens.len());
        self.token_reducing_symbols.append(&mut text_tokens);
        self.source_tokens = tokens.clone();
        self.next_read_index = 0;
    }

    pub(crate) fn read(&mut self) -> usize {
        let index_of_token = self.next_read_index;
        self.next_read_index += 1;
        return index_of_token;
    }

    pub(crate) fn has_next(&self) -> bool {
        return self.next_read_index < self.token_reducing_symbols.len();
    }

    pub(crate) fn has_read_all(&self) -> bool {
        return self.next_read_index >= self.token_reducing_symbols.len();
    }

    pub(crate) fn clear(&mut self) {
        self.token_reducing_symbols.clear();
        self.next_read_index = 0;
    }

    pub(crate) fn get_token_ref(&self, index_of_token: usize) -> &Token {
        return &(self.token_reducing_symbols[index_of_token]);
    }

    pub(crate) fn get_token_terminal_index(&self, index_of_token: usize) -> usize {
        return self.token_reducing_symbols[index_of_token]
            .terminal
            .get_index();
    }

    pub(crate) fn get_ast_parse_error_data(&self,
        start_index_of_token_reducing_symbols: i32,
        end_index_of_token_reducing_symbols: i32,
    ) -> AstAppError {

        let start_index_byte = self.token_reducing_symbols[start_index_of_token_reducing_symbols as usize].start;
        let end_token = &self.token_reducing_symbols[end_index_of_token_reducing_symbols as usize];
        let end_index_byte = end_token.start + end_token.text.len();

        let mut token_info: Vec<u8> = Vec::with_capacity(end_index_byte - start_index_byte + 1);
        let mut index_of_start_source_token : i32 = 0;
        for  index_of_source_token in  start_index_of_token_reducing_symbols..self.source_tokens.len() as i32 {
          let token = self.source_tokens.get(index_of_source_token as usize).unwrap();
          if token.start == start_index_byte {
            index_of_start_source_token = index_of_source_token;
            break;
          }
        }
        for  indexOfSourceToken in  index_of_start_source_token..self.source_tokens.len() as i32 {
          let token = self.source_tokens.get(indexOfSourceToken as usize).unwrap();
          if token.start < end_index_byte {
            token_info.append(&mut token.text.clone());
          } else {
            break;
          }
        }

        return AstAppError::AstParseError {
            start: start_index_byte,
            end: end_index_byte,
            error_text: token_info,
        };
    }
}

impl Default for TokenReducingSymbolInputStream {
    fn default() -> Self {
        Self {
            next_read_index: 0,
            token_reducing_symbols: Default::default(),
            source_tokens:Default::default()
        }
    }
}
