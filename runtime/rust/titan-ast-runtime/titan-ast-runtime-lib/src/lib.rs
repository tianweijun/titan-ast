mod ast_automata;
mod ast_automata_builder;
mod automata_data;
mod automata_tmp_ast;
mod backtracking_bottom_up_ast_automata;
mod backtracking_bottom_up_branch;
mod byte_buffer;
mod byte_buffered_input_stream;
mod dfa_token_automata;
mod error;
mod fa;
mod follow_filter_backtracking_bottom_up_ast_automata;
mod key_word_automata;
mod key_word_dfa_token_automata;
mod persistent_data;
mod persistent_object;
mod reducing_symbol;
mod super_backtracking_bottom_up_ast_automata;
mod super_dfa_token_automata;
mod syntax_dfa;
mod token_automata;
mod token_automata_builder;
mod token_dfa;
mod token_reducing_symbol_input_stream;

pub mod ast;
pub mod ast_application;

#[cfg(test)]
mod tests_in_lib {
    use std::collections::BTreeSet;

    use crate::{ast_application::RuntimeAutomataAstApplication, token_automata::TokenBuilder};

    #[test]
    fn build_c_ast() {
        let mut app = RuntimeAutomataAstApplication::default();

        let automata_file_path = String::from("D:/github-pro/titan/titan-ast/test/c/automata.data");
        let set_context_result = app.set_context(&automata_file_path);
        if set_context_result.is_err() {
            println!("{}", set_context_result.err().unwrap());
            return;
        }

        let source_code_file_path =
            String::from("D://github-pro/titan/titan-ast/test/c/helloworld.c");

        let build_ast_result = app.build_ast(&source_code_file_path);

        match build_ast_result {
            Ok(ast) => println!("{:#?}", ast),
            Err(err) => println!("{err}"),
        }
    }

    #[test]
    fn build_c_token() {
        let mut app = RuntimeAutomataAstApplication::default();

        let automata_file_path = String::from("D:/github-pro/titan/titan-ast/test/c/automata.data");
        let set_context_result = app.set_context(&automata_file_path);
        if set_context_result.is_err() {
            println!("{}", set_context_result.err().unwrap());
            return;
        }

        let source_code_file_path =
            String::from("D://github-pro/titan/titan-ast/test/c/helloworld.c");

        let tokens = app.token_automata.build_token(&source_code_file_path);

        match tokens {
            Ok(t) => {
                for i in 0..t.len() {
                    let grammar_name = match &t[i].terminal {
                        crate::ast::Grammar::TerminalGrammar(terminal) => terminal.name.clone(),
                        crate::ast::Grammar::NonterminalGrammar(nonterminal) => {
                            nonterminal.name.clone()
                        }
                    };
                    println!(
                        "$-->>>>>{}-{}<<<<<--$",
                        String::from_utf8_lossy(&(t[i].data.data)).to_string(),
                        grammar_name
                    );
                }
            }
            Err(err) => println!("{err}"),
        };
    }

    #[test]
    fn simple_test() {
        let mut tree_set = BTreeSet::from([1, 2, 3, 4, 5, 99, 87, 6, 9]);
        print!("{:?}", tree_set);

        let reducing_symbol = 0;
    }
}
