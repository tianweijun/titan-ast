use crate::reducing_symbol::ReducingSymbol;

#[derive(Clone)]
pub(crate) struct BacktrackingBottomUpBranch {
    pub(crate) reducing_symbols: Vec<ReducingSymbol>,
}

impl PartialEq for BacktrackingBottomUpBranch {
    fn eq(&self, other: &Self) -> bool {
        self.reducing_symbols.len() == other.reducing_symbols.len()
    }
}

impl Eq for BacktrackingBottomUpBranch {}

impl PartialOrd for BacktrackingBottomUpBranch {
    fn partial_cmp(&self, other: &Self) -> Option<std::cmp::Ordering> {
        Some(
            self.reducing_symbols
                .len()
                .cmp(&other.reducing_symbols.len()),
        )
    }
}

impl Ord for BacktrackingBottomUpBranch {
    fn cmp(&self, other: &Self) -> std::cmp::Ordering {
        self.reducing_symbols
            .len()
            .cmp(&other.reducing_symbols.len())
    }
}

impl Default for BacktrackingBottomUpBranch {
    fn default() -> Self {
        Self {
            reducing_symbols: Default::default(),
        }
    }
}
