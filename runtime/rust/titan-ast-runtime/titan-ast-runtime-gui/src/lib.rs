pub mod ast_gui_outputer;
mod string_tree;
mod tree_data_builder;

#[cfg(test)]
mod tests {
    use titan_ast_runtime_lib::ast_application::RuntimeAutomataRichAstApplication;

    use crate::{ast_gui_outputer::AstGuiOutputer, string_tree::StringTree};

    #[test]
    fn show_c_ast() {
        let automata_file_path = "D:/github-pro/titan/titan-ast/test/c/automata.data".to_string();
        let source_file_path = "D://github-pro/titan/titan-ast/test/c/helloworld.c".to_string();
        show_ast(&automata_file_path, &source_file_path);
    }

    #[test]
    fn show_json_ast() {
        let automata_file_path = "D:/github-pro/titan/titan-ast/test/json/automata.data".to_string();
        let source_file_path = "D://github-pro/titan/titan-ast/test/json/titanLanguageConfig.json".to_string();
        show_ast(&automata_file_path, &source_file_path);
    }

    fn show_ast(automata_file_path: &String, source_file_path: &String) {
        let mut app = RuntimeAutomataRichAstApplication::default();

        let set_context_result = app.set_context(automata_file_path);
        if set_context_result.is_err() {
            println!("{}", set_context_result.err().unwrap());
            return;
        }

        let ast_generator_result = app.build_rich_ast(source_file_path);
        if ast_generator_result.is_ok() {
            let _ = AstGuiOutputer::show_ast_by_utf8_string_tree(ast_generator_result.get_ok_ast());
        }else{
            println!("{}", ast_generator_result.get_error_msg());
        }
    }

    fn it_works() {
        let string_tree: StringTree = StringTree {
            text: "Root".into(),
            children: vec![
                StringTree {
                    text: "child-1-1".into(),
                    children: vec![],
                },
                StringTree {
                    text: "child-1-2".into(),
                    children: vec![],
                },
            ],
        };
        let _ = AstGuiOutputer::show(string_tree);
    }
}
