use std::collections::HashMap;
use std::hash::{Hash, Hasher};

use crate::ast::Grammar;
#[derive(Clone)]
pub(crate) struct SyntaxDfaState {
    pub(crate) index: usize,
    pub(crate) type_: i32,
    pub(crate) edges: HashMap<usize, usize>,
    pub(crate) closing_production_rules: Vec<usize>,
}

impl PartialEq for SyntaxDfaState {
    fn eq(&self, other: &Self) -> bool {
        self.index == other.index
    }
}
impl Eq for SyntaxDfaState {}
impl Hash for SyntaxDfaState {
    fn hash<H: Hasher>(&self, hasher: &mut H) {
        self.index.hash(hasher);
    }
}

impl Default for SyntaxDfaState {
    fn default() -> Self {
        Self {
            index: Default::default(),
            type_: Default::default(),
            edges: Default::default(),
            closing_production_rules: Default::default(),
        }
    }
}

#[derive(Clone)]
pub(crate) struct SyntaxDfa {
    pub(crate) states: Vec<SyntaxDfaState>,
    pub(crate) start: usize,
    pub(crate) grammars: Vec<Grammar>,
    pub(crate) production_rules: Vec<ProductionRule>,
}

impl SyntaxDfa {
    pub(crate) fn get_state(&self, index: usize) -> SyntaxDfaState {
        return self.states[index].clone();
    }

    pub(crate) fn get_state_ref(&self, index: usize) -> &SyntaxDfaState {
        return &(self.states[index]);
    }

    pub(crate) fn get_production_rule(&self, index: usize) -> ProductionRule {
        return self.production_rules[index].clone();
    }

    pub(crate) fn get_production_rule_grammar(&self, index: usize) -> usize {
        return self.production_rules[index].grammar;
    }
}

#[derive(Clone)]
pub(crate) struct ReducingSyntaxDfa {
    pub(crate) states: Vec<SyntaxDfaState>,
    pub(crate) start: usize,
}

impl ReducingSyntaxDfa {
    pub(crate) fn get_state(&self, index: usize) -> SyntaxDfaState {
        return self.states[index].clone();
    }

    pub(crate) fn get_state_ref(&self, index: usize) -> &SyntaxDfaState {
        return &(self.states[index]);
    }
}

#[derive(Clone)]
pub(crate) struct ProductionRule {
    // notNull
    pub(crate) grammar: usize,
    pub(crate) alias: Option<String>,
    // 用于收敛产生式
    pub(crate) reducing_dfa: ReducingSyntaxDfa,
}

impl Default for ProductionRule {
    fn default() -> Self {
        return ProductionRule {
            grammar: Default::default(),
            alias: None,
            reducing_dfa: Default::default(),
        };
    }
}

impl From<SyntaxDfa> for ReducingSyntaxDfa {
    fn from(value: SyntaxDfa) -> Self {
        Self {
            states: value.states,
            start: value.start,
        }
    }
}

impl Default for ReducingSyntaxDfa {
    fn default() -> Self {
        Self {
            states: Default::default(),
            start: 0,
        }
    }
}

impl Default for SyntaxDfa {
    fn default() -> Self {
        Self {
            states: Default::default(),
            grammars: Default::default(),
            production_rules: Default::default(),
            start: 0,
        }
    }
}
