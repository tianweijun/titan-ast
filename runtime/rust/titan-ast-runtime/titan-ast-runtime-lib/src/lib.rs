mod ast_automata;
mod ast_automata_builder;
mod automata_tmp_ast;
mod backtracking_bottom_up_ast_automata;
mod byte_buffer;
mod byte_buffered_input_stream;
mod dfa_token_automata;
mod error;
mod fa;
mod follow_filter_backtracking_bottom_up_ast_automata;
mod key_word_automata;
mod key_word_dfa_token_automata;
mod persistent_ast_application;
mod persistent_data;
mod persistent_object;
mod super_backtracking_bottom_up_ast_automata;
mod super_dfa_token_automata;
mod syntax_dfa;
mod token_automata;
mod token_automata_builder;
mod token_dfa;

pub mod ast;
pub mod ast_application;

#[cfg(test)]
mod tests_in_lib {
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

        let tokens = app
            .persistent_automata_ast_application
            .token_automata
            .build_token(&source_code_file_path);

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
    fn load_c_persistent_data() {
        let mut app = RuntimeAutomataAstApplication::default();

        let automata_file_path = String::from("D:/github-pro/titan/titan-ast/test/c/automata.data");
        let set_context_result = app.set_context(&automata_file_path);
        if set_context_result.is_err() {
            println!("{}", set_context_result.err().unwrap());
            return;
        }

        assert_eq!(
            app.persistent_automata_ast_application
                .persistent_object
                .persistent_data
                .string_pool
                .len(),
            275
        );
        assert_eq!(
            app.persistent_automata_ast_application
                .persistent_object
                .persistent_data
                .grammars
                .len(),
            181
        );
        assert_eq!(
            app.persistent_automata_ast_application
                .persistent_object
                .key_word_automata
                .text_terminal_map
                .len(),
            58
        );
        assert_eq!(
            app.persistent_automata_ast_application
                .persistent_object
                .nonterminal_follow_map
                .len(),
            88
        );
        assert_eq!(2, 2);
    }

    #[test]
    fn simple_test() {
        let mut v = vec![1; 9];
        println!("{}-{}:{:?}", v.len(), v.capacity(), v);
        v.push(5);
        println!("{}-{}:{:?}", v.len(), v.capacity(), v);
        v.push(6);
        println!("{}-{}:{:?}", v.len(), v.capacity(), v);
        v.clear();
        println!("{}-{}:{:?}", v.len(), v.capacity(), v);
    }
}
