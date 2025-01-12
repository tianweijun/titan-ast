#[derive(Clone)]
pub(crate) enum AutomataTmpAst {
    TerminalAutomataTmpAst(TerminalAutomataTmpAst),
    NonterminalAutomataTmpAst(NonterminalAutomataTmpAst),
}

#[derive(Clone)]
pub(crate) struct TerminalAutomataTmpAst {
    pub(crate) grammar: usize, //ast dfa pool
    pub(crate) token: usize,   //token input stream
    pub(crate) children: Vec<AutomataTmpAst>,
}

#[derive(Clone)]
pub(crate) struct NonterminalAutomataTmpAst {
    pub(crate) grammar: usize, //ast dfa pool
    pub(crate) alias: usize,   //ast dfa production rule pool
    pub(crate) children: Vec<AutomataTmpAst>,
}

impl AutomataTmpAst {
    pub(crate) fn get_grammar(&self) -> usize {
        return match self {
            AutomataTmpAst::TerminalAutomataTmpAst(terminal_automata_tmp_ast) => {
                terminal_automata_tmp_ast.grammar
            }
            AutomataTmpAst::NonterminalAutomataTmpAst(nonterminal_automata_tmp_ast) => {
                nonterminal_automata_tmp_ast.grammar
            }
        };
    }

    pub(crate) fn get_children_mut(&mut self) -> &mut Vec<AutomataTmpAst> {
        return match self {
            AutomataTmpAst::TerminalAutomataTmpAst(terminal_automata_tmp_ast) => {
                &mut terminal_automata_tmp_ast.children
            }
            AutomataTmpAst::NonterminalAutomataTmpAst(nonterminal_automata_tmp_ast) => {
                &mut nonterminal_automata_tmp_ast.children
            }
        };
    }

    pub(crate) fn get_children(&self) -> &Vec<AutomataTmpAst> {
        return match self {
            AutomataTmpAst::TerminalAutomataTmpAst(terminal_automata_tmp_ast) => {
                &terminal_automata_tmp_ast.children
            }
            AutomataTmpAst::NonterminalAutomataTmpAst(nonterminal_automata_tmp_ast) => {
                &nonterminal_automata_tmp_ast.children
            }
        };
    }
}

impl Default for NonterminalAutomataTmpAst {
    fn default() -> Self {
        Self {
            grammar: Default::default(),
            alias: Default::default(),
            children: Default::default(),
        }
    }
}

impl Default for TerminalAutomataTmpAst {
    fn default() -> Self {
        Self {
            grammar: Default::default(),
            token: Default::default(),
            children: Default::default(),
        }
    }
}

impl Default for AutomataTmpAst {
    fn default() -> Self {
        AutomataTmpAst::NonterminalAutomataTmpAst(Default::default())
    }
}
