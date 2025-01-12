use std::cmp::Ordering;

use crate::reducing_symbol::ReducingSymbol;

#[derive(Clone)]
pub(crate) struct BacktrackingBottomUpBranch {
    pub(crate) reducing_symbols: Vec<ReducingSymbol>,
}

impl PartialEq for BacktrackingBottomUpBranch {
    fn eq(&self, other: &Self) -> bool {
        if self.reducing_symbols.len() != other.reducing_symbols.len() {
            return false;
        }

        let mut self_reducing_symbols_it = self.reducing_symbols.iter();
        let mut other_reducing_symbols_it = other.reducing_symbols.iter();
        loop {
            let self_reducing_symbol = self_reducing_symbols_it.next();
            let other_reducing_symbol = other_reducing_symbols_it.next();
            if self_reducing_symbol.is_none() {
                break;
            }
            if !self_reducing_symbol
                .unwrap()
                .equals(other_reducing_symbol.unwrap())
            {
                return false;
            }
        }
        return true;
    }
}

impl Eq for BacktrackingBottomUpBranch {}

impl PartialOrd for BacktrackingBottomUpBranch {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        return Some(self.cmp(other));
    }
}

impl Ord for BacktrackingBottomUpBranch {
    fn cmp(&self, other: &Self) -> Ordering {
        let self_end_index = self.reducing_symbols.last().unwrap().end_index_of_token;
        let other_end_index = other.reducing_symbols.last().unwrap().end_index_of_token;
        if self_end_index < other_end_index {
            return Ordering::Less;
        }
        if self_end_index > other_end_index {
            return Ordering::Greater;
        }

        let self_len = self.reducing_symbols.len();
        let other_len = other.reducing_symbols.len();
        if self_len < other_len {
            return Ordering::Less;
        }
        if self_len > other_len {
            return Ordering::Greater;
        }

        let mut self_reducing_symbols_it = self.reducing_symbols.iter();
        let mut other_reducing_symbols_it = other.reducing_symbols.iter();
        loop {
            let self_reducing_symbol = self_reducing_symbols_it.next();
            let other_reducing_symbol = other_reducing_symbols_it.next();
            if self_reducing_symbol.is_none() {
                break;
            }
            let compare = self_reducing_symbol
                .unwrap()
                .compare(other_reducing_symbol.unwrap());
            if compare.is_ne() {
                return compare;
            }
        }
        return Ordering::Equal;
    }
}

impl Default for BacktrackingBottomUpBranch {
    fn default() -> Self {
        Self {
            reducing_symbols: Default::default(),
        }
    }
}
