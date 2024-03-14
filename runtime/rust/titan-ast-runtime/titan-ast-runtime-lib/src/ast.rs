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
    pub data: TokenData,
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

#[derive(Debug, Clone, PartialEq, Eq, Hash)]
pub struct TokenData {
    pub data: Vec<u8>,
}

#[derive(Debug, Clone)]
pub struct Token {
    pub start: usize,
    pub data: TokenData,
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
}

#[derive(Debug, Clone)]
pub struct Ast {
    pub grammar: Grammar,
    pub alias: Option<String>,
    pub token: Option<AstToken>,
    pub children: Vec<Ast>,
}

impl Default for Ast {
    fn default() -> Self {
        Ast {
            grammar: Default::default(),
            alias: Default::default(),
            token: Default::default(),
            children: Default::default(),
        }
    }
}

impl Default for Grammar {
    fn default() -> Self {
        Grammar::TerminalGrammar(Default::default())
    }
}

impl TerminalGrammar {
    pub fn new(
        name: String,
        type_: GrammarType,
        action: GrammarAction,
        lookahead_matching_mode: LookaheadMatchingMode,
    ) -> TerminalGrammar {
        return TerminalGrammar {
            name,
            type_,
            action,
            lookahead_matching_mode,
        };
    }
}

impl Default for TerminalGrammar {
    fn default() -> Self {
        Self {
            name: Default::default(),
            type_: GrammarType::Terminal,
            action: GrammarAction::Skip,
            lookahead_matching_mode: LookaheadMatchingMode::Greediness,
        }
    }
}

impl NonterminalGrammar {
    pub fn new(name: String, type_: GrammarType, action: GrammarAction) -> Self {
        return NonterminalGrammar {
            name,
            type_,
            action,
        };
    }
}

impl Default for NonterminalGrammar {
    fn default() -> Self {
        Self {
            name: Default::default(),
            type_: GrammarType::Nonterminal,
            action: GrammarAction::Skip,
        }
    }
}

impl PartialEq for TerminalGrammar {
    fn eq(&self, other: &Self) -> bool {
        self.name == other.name
    }
}
impl Eq for TerminalGrammar {}
impl Hash for TerminalGrammar {
    fn hash<H: Hasher>(&self, hasher: &mut H) {
        self.name.hash(hasher);
    }
}

impl PartialEq for NonterminalGrammar {
    fn eq(&self, other: &Self) -> bool {
        self.name == other.name
    }
}
impl Eq for NonterminalGrammar {}
impl Hash for NonterminalGrammar {
    fn hash<H: Hasher>(&self, hasher: &mut H) {
        self.name.hash(hasher);
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
