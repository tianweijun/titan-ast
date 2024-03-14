use std::collections::HashSet;

use titan_ast_runtime_lib;

use titan_ast_runtime_lib::ast::{
    Grammar, GrammarAction, GrammarType, LookaheadMatchingMode, NonterminalGrammar, TerminalGrammar,
};

#[test]
fn test_ast_set() {
    let a = Grammar::TerminalGrammar(TerminalGrammar {
        name: "a".to_string(),
        type_: GrammarType::Terminal,
        action: GrammarAction::Text,
        lookahead_matching_mode: LookaheadMatchingMode::Greediness,
    });
    let b = Grammar::NonterminalGrammar(NonterminalGrammar {
        name: "a".to_string(),
        type_: GrammarType::Terminal,
        action: GrammarAction::Text,
    });
    let c = Grammar::TerminalGrammar(TerminalGrammar {
        name: "a".to_string(),
        type_: GrammarType::Terminal,
        action: GrammarAction::Text,
        lookahead_matching_mode: LookaheadMatchingMode::Greediness,
    });

    let mut set: HashSet<&Grammar> = HashSet::new();

    set.insert(&a);
    //println!("{:#?}", set);

    set.insert(&b);
    //println!("{:#?}", set);

    assert_eq!(set, HashSet::from([&a, &b, &c]));
}
