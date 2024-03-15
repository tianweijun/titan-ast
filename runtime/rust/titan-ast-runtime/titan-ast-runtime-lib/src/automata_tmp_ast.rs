use crate::ast::{AstToken, Grammar};

pub(crate) struct AutomataTmpAst {
    pub grammar: usize, //ast dfa pool
    pub alias: usize,   //ast dfa production rule pool
    pub token: i32,     //token input stream
    pub children: Vec<AutomataTmpAst>,
}
