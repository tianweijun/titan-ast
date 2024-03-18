#[derive(Clone)]
pub(crate) struct AutomataTmpAst {
    pub(crate) grammar: usize,       //ast dfa pool
    pub(crate) alias: Option<usize>, //ast dfa production rule pool
    pub(crate) token: Option<usize>, //token input stream
    pub(crate) children: Vec<AutomataTmpAst>,
}

impl AutomataTmpAst {}

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
