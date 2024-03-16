use crate::ast::{Token, TokenType};

#[derive(Clone)]
pub(crate) struct TokenReducingSymbolInputStream {
    pub(crate) next_read_index: usize,
    pub(crate) token_reducing_symbols: Vec<Token>,
}

impl TokenReducingSymbolInputStream {
    pub(crate) fn init(&mut self, tokens: Vec<Token>) {
        let mut text_tokens: Vec<Token> = Vec::with_capacity(tokens.len());
        for token in tokens.into_iter() {
            if token.type_ == TokenType::Text {
                text_tokens.push(token);
            }
        }
        self.token_reducing_symbols = Vec::with_capacity(text_tokens.len());
        self.token_reducing_symbols.append(&mut text_tokens);

        self.next_read_index = 0;
    }

    pub(crate) fn read(&mut self) -> usize {
        let index_of_token = self.next_read_index;
        self.next_read_index += 1;
        return index_of_token;
    }

    pub(crate) fn has_next(&self) -> bool {
        return self.next_read_index >= 0
            && self.next_read_index < self.token_reducing_symbols.len();
    }

    pub(crate) fn has_read_all(&self) -> bool {
        return self.next_read_index >= self.token_reducing_symbols.len();
    }
}

impl Default for TokenReducingSymbolInputStream {
    fn default() -> Self {
        Self {
            next_read_index: 0,
            token_reducing_symbols: Default::default(),
        }
    }
}
