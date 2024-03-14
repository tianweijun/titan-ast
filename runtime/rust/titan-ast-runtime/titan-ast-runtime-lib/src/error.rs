use std::fmt;

pub struct AstAppError {
    pub msg: String,
}

impl fmt::Display for AstAppError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.msg)
    }
}

impl From<std::io::Error> for AstAppError {
    fn from(value: std::io::Error) -> Self {
        AstAppError {
            msg: value.to_string(),
        }
    }
}
