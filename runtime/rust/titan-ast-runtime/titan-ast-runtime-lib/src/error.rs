use std::fmt;

use crate::ast::Token;

#[derive(Debug, Clone)]
pub enum AstAppError {
    IoError {
        msg: String,
    },
    TokenParseError {
        finished_tokens: Vec<Token>,
        start: usize,
        end: usize,
        error_text: Vec<u8>
    },
    TokensError,
    AstParseError {
        start: usize,
        end: usize,
        error_text: Vec<u8>,
    },
    RichTokenParseError {
        finished_tokens: Vec<Token>,
        start: usize,
        end: usize,
        start_line_number: usize,
        start_offset_in_line: usize,
        end_line_number: usize,
        end_offset_in_line: usize,
        error_text: Vec<u8>,
    },
    RichAstParseError {
        start: usize,
        end: usize,
        start_line_number: usize,
        start_offset_in_line: usize,
        end_line_number: usize,
        end_offset_in_line: usize,
        error_text: Vec<u8>,
    },
}

impl fmt::Display for AstAppError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            AstAppError::IoError { msg } => {
                write!(f, "{}", msg)
            }
            AstAppError::AstParseError {
                start,
                end,
                error_text,
            } => {
                write!(
                    f,
                    "generate ast failed,error near [{},{}):{}",
                    start, end, String::from_utf8_lossy(error_text),
                )
            }
            AstAppError::TokenParseError {
                finished_tokens,
                start,
                end,
                error_text,
            } => {
                write!(
                    f,
                    "[{},{}):'{}' does not match any token",
                    start, end, String::from_utf8_lossy(error_text),
                )
            }
            AstAppError::TokensError => {
                write!(f, "{}", "error in generate tokes")
            }
            AstAppError::RichTokenParseError {
                finished_tokens,
                start,
                end,
                start_line_number,
                start_offset_in_line,
                end_line_number,
                end_offset_in_line,
                error_text,
            } => {
                write!(
                    f,
                    "generate token error,error near [{}-{},{}-{}): '{}'",
                    start_line_number,
                    start_offset_in_line,
                    end_line_number,
                    end_offset_in_line,
                    String::from_utf8_lossy(error_text)
                )
            }
            AstAppError::RichAstParseError {
                start,
                end,
                start_line_number,
                start_offset_in_line,
                end_line_number,
                end_offset_in_line,
                error_text,
            } => {
                write!(
                    f,
                    "generate ast error,error near [{}-{},{}-{}): '{}'",
                    start_line_number,
                    start_offset_in_line,
                    end_line_number,
                    end_offset_in_line,
                    String::from_utf8_lossy(error_text)
                )
            }
        }
    }
}

impl From<std::io::Error> for AstAppError {
    fn from(value: std::io::Error) -> Self {
        AstAppError::IoError {
            msg: value.to_string(),
        }
    }
}
