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
mod derived_terminal_grammar_automata_data;
mod derived_terminal_grammar_automata;
mod persistent_data;
mod persistent_object;
mod reducing_symbol;
mod result;
mod super_backtracking_bottom_up_ast_automata;
mod super_dfa_token_automata;
mod syntax_dfa;
mod token_automata;
mod token_automata_builder;
mod token_dfa;
mod token_reducing_symbol_input_stream;
mod ast_generator_result_to_rich_result_converter;

pub mod ast;
pub mod ast_application;

#[cfg(test)]
mod tests_in_lib {
    use std::collections::BTreeSet;

    use crate::{
        ast_application::{RuntimeAutomataAstApplication, RuntimeAutomataRichAstApplication},
        token_automata::TokenBuilder,
    };

    #[test]
    fn build_json_ast() {
        let automata_file_path =
            "D:/github-pro/titan/titan-ast/test/json/automata.data".to_string();
        let source_file_path =
            "D://github-pro/titan/titan-ast/test/json/titanLanguageConfig.json".to_string();
        build_ast(&automata_file_path, &source_file_path);
    }

    #[test]
    fn build_c_ast() {
        let automata_file_path = "D:/github-pro/titan/titan-ast/test/c/automata.data".to_string();
        let source_file_path = "D://github-pro/titan/titan-ast/test/c/helloworld.c".to_string();
        build_ast(&automata_file_path, &source_file_path);
    }

    fn build_ast(automata_file_path: &String, source_file_path: &String) {
        let mut app = RuntimeAutomataRichAstApplication::default();

        let set_context_result = app.set_context(automata_file_path);
        if set_context_result.is_err() {
            println!("{}", set_context_result.err().unwrap());
            return;
        }

        let ast_generator_result = app.build_rich_ast(source_file_path);
        if ast_generator_result.is_ok() {
            println!("{:#?}", ast_generator_result.get_ok_ast());
        } else {
            println!("{}", ast_generator_result.get_error_msg());
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
                        String::from_utf8_lossy(&(t[i].text)).to_string(),
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
