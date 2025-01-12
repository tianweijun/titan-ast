use std::convert::TryFrom;
use std::hash::{Hash, Hasher};

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum GrammarAction {
    Text = 0,
    Skip = 1,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum GrammarType {
    TerminalFragment = 0,
    Terminal = 1,
    Nonterminal = 2,
}

#[derive(Debug, Clone)]
pub struct NonterminalGrammar {
    pub index: usize,
    pub name: String,
    pub type_: GrammarType,
    pub action: GrammarAction,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum LookaheadMatchingMode {
    Greediness = 0,
    Laziness = 1,
}

#[derive(Debug, Clone)]
pub struct TerminalGrammar {
    pub index: usize,
    pub name: String,
    pub type_: GrammarType,
    pub action: GrammarAction,
    pub lookahead_matching_mode: LookaheadMatchingMode,
}

#[derive(Debug, Clone, PartialEq, Eq, Hash)]
pub enum Grammar {
    TerminalGrammar(TerminalGrammar),
    NonterminalGrammar(NonterminalGrammar),
}

#[derive(Debug, Clone)]
pub struct AstToken {
    pub start: usize,
    pub text: Vec<u8>,
}

#[derive(Debug, PartialEq, Eq, Clone)]
pub enum TokenType {
    Text = 0,
    Skip = 1,
}

impl From<GrammarAction> for TokenType {
    fn from(value: GrammarAction) -> Self {
        match value {
            GrammarAction::Text => TokenType::Text,
            GrammarAction::Skip => TokenType::Skip,
        }
    }
}

#[derive(Debug, Clone)]
pub struct Token {
    pub start: usize,
    pub text: Vec<u8>,
    pub terminal: Grammar,
    pub type_: TokenType,
}

impl Grammar {
    pub fn is_greediness(&self) -> bool {
        return match self {
            Grammar::TerminalGrammar(terminal) => {
                terminal.lookahead_matching_mode == LookaheadMatchingMode::Greediness
            }
            Grammar::NonterminalGrammar(_) => false,
        };
    }

    pub fn get_action(&self) -> GrammarAction {
        match self {
            Grammar::TerminalGrammar(terminal) => terminal.action.clone(),
            Grammar::NonterminalGrammar(nonterminal) => nonterminal.action.clone(),
        }
    }

    pub fn get_index(&self) -> usize {
        match self {
            Grammar::TerminalGrammar(terminal) => terminal.index,
            Grammar::NonterminalGrammar(nonterminal) => nonterminal.index,
        }
    }

    pub fn get_ast_grammar(&self) -> AstGrammar {
        match self {
            Grammar::TerminalGrammar(terminal) => AstGrammar {
                name: terminal.name.clone(),
                type_: terminal.type_.clone(),
            },
            Grammar::NonterminalGrammar(nonterminal) => AstGrammar {
                name: nonterminal.name.clone(),
                type_: nonterminal.type_.clone(),
            },
        }
    }
}

#[derive(Debug, Clone)]
pub struct AstGrammar {
    pub name: String,
    pub type_: GrammarType,
}

#[derive(Debug, Clone)]
pub enum Ast {
    TerminalAst(TerminalAst),
    NonterminalAst(NonterminalAst),
}

#[derive(Debug, Clone)]
pub struct TerminalAst {
    pub grammar: AstGrammar,
    pub token: AstToken,
    pub children: Vec<Ast>,
}

#[derive(Debug, Clone)]
pub struct NonterminalAst {
    pub grammar: AstGrammar,
    pub alias: Option<String>,
    pub children: Vec<Ast>,
}

impl Ast {
    pub fn to_utf8_string(&self) -> String {
        match self {
            Ast::TerminalAst(terminal_ast) => {
                String::from_utf8_lossy(&terminal_ast.token.text).to_string()
            }
            Ast::NonterminalAst(nonterminal_ast) => match &nonterminal_ast.alias {
                Some(_alias) => format!(
                    "{}[{}]",
                    nonterminal_ast.grammar.name.clone(),
                    _alias.clone()
                ),
                None => nonterminal_ast.grammar.name.clone(),
            },
        }
    }

    pub fn get_grammar(&self) -> &AstGrammar {
        match self {
            Ast::TerminalAst(terminal_ast) => &terminal_ast.grammar,
            Ast::NonterminalAst(nonterminal_ast) => &nonterminal_ast.grammar,
        }
    }

    pub fn get_grammar_mut(&mut self) -> &mut AstGrammar {
        match self {
            Ast::TerminalAst(terminal_ast) => &mut terminal_ast.grammar,
            Ast::NonterminalAst(nonterminal_ast) => &mut nonterminal_ast.grammar,
        }
    }

    pub fn get_children(&self) -> &Vec<Ast> {
        match self {
            Ast::TerminalAst(terminal_ast) => &terminal_ast.children,
            Ast::NonterminalAst(nonterminal_ast) => &nonterminal_ast.children,
        }
    }

    pub fn get_children_mut(&mut self) -> &mut Vec<Ast> {
        match self {
            Ast::TerminalAst(terminal_ast) => &mut terminal_ast.children,
            Ast::NonterminalAst(nonterminal_ast) => &mut nonterminal_ast.children,
        }
    }
}



impl Default for AstToken {
    fn default() -> Self {
        Self {
            start: Default::default(),
            text: Default::default(),
        }
    }
}

impl Default for TerminalAst {
    fn default() -> Self {
        Self {
            grammar: Default::default(),
            token: Default::default(),
            children: Default::default(),
        }
    }
}

impl Default for NonterminalAst {
    fn default() -> Self {
        Self {
            grammar: Default::default(),
            alias: Default::default(),
            children: Default::default(),
        }
    }
}

impl Default for Ast {
    fn default() -> Self {
        Ast::NonterminalAst(Default::default())
    }
}

impl Default for AstGrammar {
    fn default() -> Self {
        Self {
            name: "".to_string(),
            type_: GrammarType::Terminal,
        }
    }
}

impl Default for Grammar {
    fn default() -> Self {
        Grammar::TerminalGrammar(Default::default())
    }
}

impl Default for TerminalGrammar {
    fn default() -> Self {
        Self {
            index: 0,
            name: Default::default(),
            type_: GrammarType::Terminal,
            action: GrammarAction::Skip,
            lookahead_matching_mode: LookaheadMatchingMode::Greediness,
        }
    }
}

impl Default for NonterminalGrammar {
    fn default() -> Self {
        Self {
            index: 0,
            name: Default::default(),
            type_: GrammarType::Nonterminal,
            action: GrammarAction::Skip,
        }
    }
}

impl PartialEq for TerminalGrammar {
    fn eq(&self, other: &Self) -> bool {
        self.index == other.index
    }
}
impl Eq for TerminalGrammar {}
impl Hash for TerminalGrammar {
    fn hash<H: Hasher>(&self, hasher: &mut H) {
        self.index.hash(hasher);
    }
}

impl PartialEq for NonterminalGrammar {
    fn eq(&self, other: &Self) -> bool {
        self.index == other.index
    }
}
impl Eq for NonterminalGrammar {}
impl Hash for NonterminalGrammar {
    fn hash<H: Hasher>(&self, hasher: &mut H) {
        self.index.hash(hasher);
    }
}

impl TryFrom<i32> for GrammarType {
    type Error = ();

    fn try_from(value: i32) -> Result<Self, Self::Error> {
        match value {
            v if v == GrammarType::TerminalFragment as i32 => Ok(GrammarType::TerminalFragment),
            v if v == GrammarType::Terminal as i32 => Ok(GrammarType::Terminal),
            v if v == GrammarType::Nonterminal as i32 => Ok(GrammarType::Nonterminal),
            _ => Err(()),
        }
    }
}

impl TryFrom<i32> for GrammarAction {
    type Error = ();

    fn try_from(value: i32) -> Result<Self, Self::Error> {
        match value {
            v if v == GrammarAction::Text as i32 => Ok(GrammarAction::Text),
            v if v == GrammarAction::Skip as i32 => Ok(GrammarAction::Skip),
            _ => Err(()),
        }
    }
}

impl TryFrom<i32> for LookaheadMatchingMode {
    type Error = ();

    fn try_from(value: i32) -> Result<Self, Self::Error> {
        match value {
            v if v == LookaheadMatchingMode::Greediness as i32 => {
                Ok(LookaheadMatchingMode::Greediness)
            }
            v if v == LookaheadMatchingMode::Laziness as i32 => Ok(LookaheadMatchingMode::Laziness),
            _ => Err(()),
        }
    }
}
