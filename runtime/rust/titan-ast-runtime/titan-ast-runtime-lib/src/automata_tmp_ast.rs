#[derive(Clone)]
pub(crate) struct AutomataTmpAst {
    pub grammar: usize, //ast dfa pool
    pub alias: usize,   //ast dfa production rule pool
    pub token: i32,     //token input stream
    pub children: Vec<AutomataTmpAst>,
}

impl Default for AutomataTmpAst {
    fn default() -> Self {
        Self {
            grammar: Default::default(),
            alias: Default::default(),
            token: Default::default(),
            children: Default::default(),
        }
    }
}
